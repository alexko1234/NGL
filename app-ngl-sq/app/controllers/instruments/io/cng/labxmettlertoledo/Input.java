package controllers.instruments.io.cng.labxmettlertoledo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Results.Todo;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.description.ReagentCatalog;
import models.laboratory.reagent.instance.ReagentUsed;

import org.mongojack.DBQuery;

public class Input extends AbstractInput {
	
   /* 25/10/2017 NGL-1326
    * Description du fichier a importer: fichier CSV generé par le Logiciel LabX de Mettler Toledo connecté a une balance XPE4002S
    * ";"  delim
    * verifier que le nom du fichier convient ? ou se baser sur la ligne 5 ???

 
   */

	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
		Logger.info ("LABXMETTLERTOLEDO INPUT !!!!");	
			
		// Avant de commencer le parsing du fichier récupérer la liste des kits actifs / box actives / reagents actifs pour le type d'expérience...
		List<KitCatalog> experimentKitList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, 
				//OK DBQuery.and(DBQuery.is("category", "Kit"),DBQuery.in("experimentTypeCodes", experiment.typeCode))).toList();
				DBQuery.is("category", "Kit").and(DBQuery.is("active", true)).and(DBQuery.in("experimentTypeCodes", experiment.typeCode))).toList();

		//hashmap pour les box et les reactifs
		Map<String,BoxCatalog> boxMap = new HashMap<String,BoxCatalog>(0);
		Map<String,ReagentCatalog> reagentMap = new HashMap<String,ReagentCatalog>(0);
		Map<String,String> boxCodeMap = new HashMap<String,String>(0); //pour retrouver le code d'une box d'apres son name...
		
		if ( experimentKitList.isEmpty() ) {
			Logger.info ("PAS DE KIT ACTIF TROUVE");
			// arrive pas a sortir ce message  !!!!
			contextValidation.addErrors("Erreurs fichier","Aucun kit actif n''est défini pour ce type d''expérience");
			return experiment;
		} else {
			for(KitCatalog kit:  experimentKitList){
				Logger.info ("KIT TROUVE : "+kit.name + " ("+ kit.code+")");
				// trouver les boites de chacun des kits
				List<BoxCatalog> kitBoxList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, 
						DBQuery.is("category", "Box").and(DBQuery.is("active", true)).and(DBQuery.is("kitCatalogCode", kit.code))).toList();
				for(BoxCatalog box:  kitBoxList){
					Logger.info (" >BOX ACTIVE TROUVEE :"+ box.name + " ("+box.code+")");
					boxMap.put(box.name, box);
					
					// trouverles reactifs de chaque boite
					List<ReagentCatalog>  boxReagentList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, 
                     		 DBQuery.is("category", "Reagent").and(DBQuery.is("active", true)).and(DBQuery.is("boxCatalogCode", box.code))).toList();
					for(ReagentCatalog reagent:  boxReagentList){
						Logger.info ("  >>REAG ACTIF TROUVEE :"+reagent.name + " ("+reagent.code+ ")");
						reagentMap.put(reagent.name, reagent);
					}
				}
			}
		}
		 
		
		byte[] ibuf = pfv.value;
		InputStream is = new ByteArrayInputStream(ibuf);
		
		// le fichier CSV sorti par le logiciel Labx est en ISO-8859 ( valeur retournee par la commande "file" Linux)
		String charset = "ISO-8859-15";
		
		// charset detection (N. Wiart)
		//String charset = "UTF-8"; //par defaut, convient aussi pour de l'ASCII pur
		// si le fichier commence par les 2 bytes ff/fe  alors le fichier est encodé en UTF-16 little endian
		//if (ibuf.length >= 2 && (0xff & ibuf[0]) == 0xff && (ibuf[1] & 0xff) == 0xfe) {
		//	charset = "UTF-16LE";
		//}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		
		int n = 0;
		boolean lastResult=false;
		String line="";	
		
		while (((line = reader.readLine()) != null) && !lastResult ){	 
			// attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!
			String[] cols = line.replace (",", ".").split(";");

			// verifier la 1 ere ligne : doit etre "Compte rendu de séquençage"
			if (n == 0) {
				if ( ! cols[0].trim().equals("Compte rendu de séquençage") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Compte rendu de séquençage");
					return experiment;
				}
			}
			
			// en 6eme ligne il y actuellement le nom d'un (kit ? reactif ? ) particulier qui permet de verifier que le fichier correspond à l'expérience en cours
			if ( n == 5){
				if ( ! cols[0].trim().equals("HiSeq X Flow Cell") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "HiSeq X Flow Cell");
					return experiment;
				} else {
					// verifier le code flow cell
					String flowcellId=cols[8].trim();
					Logger.info ("flowcellId="+flowcellId);
					
					if ( ! experiment.instrumentProperties.get("containerSupportCode").value.equals(flowcellId))  {
		        		 contextValidation.addErrors("Erreurs fichier", "Le barcode flowcell ligne 5 ("+flowcellId+") ne correspond pas à celui de l'expérience");
						 return experiment;
					}
				}
			}
			
			// commencer le traitement en sautant les 9 premieres lignes
			if (n > 9 ) {
				// ligne "Résultats principaux..." = fin des données a parser
				if ( cols[0].trim().matches("Résultats principaux(.*)")){
					lastResult=true;
				} 

				if ( !cols[0].trim().equals("") ){
					//Logger.info ("processing ligne "+ n +" section  boites..)...");	
					// voir avec Florence: c'est les lignes LOT ou RGT  qu'il faut traiter ?? voire les 2 ??????
					String item[]=cols[0].trim().split("LOT");	
					if (item.length != 2){
						//ignorer sans erreur ???  ( ligne Code Barre ou ligne Résultat ou ligne RGT...)
						n++;
						continue;
					} else {
						String fileItemName=item[1].trim(); // trim !!!!
						String fileItemCode=cols[8].trim();
						if ( fileItemCode.equals("")){
							//code barre manquant !!
							Logger.info ("ERROR line "+ n +": reagent barcode missing");
						}
						
						///Logger.info ("ligne "+ n +" chercher si :"+fileItemName+" est une boite OU un reactif associé");		
						//-1- chercher dans boxMap
						if ( boxMap.containsKey(fileItemName) ){	
							Logger.info (fileItemName+ " TROUVE DANS HASH BOX");
							BoxCatalog bc= boxMap.get(fileItemName);
							boxCodeMap.put(bc.code, fileItemCode+"_");// stocker le barcode de boite pour ses reagents plus tard...
							
							//  A FAIRE ??? ou faut il traiter les boites sans les reagents ?????
							  //construire un reagentUsed et l'ajouter a l'experiment
							  ReagentUsed ru=new ReagentUsed();  
							  ru.kitCatalogCode=bc.kitCatalogCode;  Logger.info("kitCatalogCode="+ru.kitCatalogCode);
							  ru.boxCatalogCode=bc.code; Logger.info("boxCatalogCode="+ru.boxCatalogCode);
							  ru.boxCode=fileItemCode+"_" ;  Logger.info("boxCode="+ ru.boxCode);  // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite;
							
						      experiment.reagents.add(ru);  //a faire uniquement si pas d'erreur !!!!
						
						} 
						//-2- chercher dans mapReagent
						else if ( reagentMap.containsKey(fileItemName) ){	
							Logger.info (fileItemName+ " TROUVE DANS HASH REAGENT");
							ReagentCatalog rc= reagentMap.get(fileItemName);
							
							if ( null == boxCodeMap.get(rc.boxCatalogCode));
							{
							  // la boite de ce reactif n'est pas presente dans la section de declaration des boites !!!
							  // que fait-on ????
							  Logger.info("WARNING line "+n+" reagent box not declared in file.!!!! pour l'instant continuer qd meme");
							}
							
							//construire un reagentUsed et l'ajouter a l'experiment
							ReagentUsed ru=new ReagentUsed();  
							ru.kitCatalogCode=rc.kitCatalogCode; Logger.info("kitCatalogCode="+ru.kitCatalogCode);
							ru.boxCatalogCode=rc.boxCatalogCode; Logger.info("boxCatalogCode="+ru.boxCatalogCode);
							ru.reagentCatalogCode=rc.code; Logger.info("reagentCatalogCode="+ru.reagentCatalogCode);
							 
							ru.boxCode=boxCodeMap.get(rc.boxCatalogCode);  Logger.info("boxCode="+ru.boxCode); 
							ru.code=fileItemCode+"_" ; Logger.info("code="+ ru.code); // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
							// ru.description=fileReagentWeight;	// pas de description dans cette section du fichier...	 
								
							experiment.reagents.add(ru);  //a faire uniquement si pas d'erreur !!!!
						} 
						else { Logger.info ("WARNING line "+ n +": item <"+fileItemName+ "> is not a reagent nor a box; continuer ????");}
					}
				} else {
					if ( ! cols[1].trim().equals("Position") ){
						//Logger.info ("processing ligne "+ n +" section  position...");		
						//reactifs en positions 1-->XX
						String fileReagentName=cols[2].trim();
						if ( fileReagentName.equals("")){
							// nom du reactif manquant....
							Logger.info("ERROR line "+ n+": reagent name missing");
						}
						
						// dans cette section ne doivent se trouver que des reactifs (pas de boites ni de kits)
						Logger.info ("ligne "+ n+ " chercher si :<"+fileReagentName+"> est un reactif actif");
						
						//chercher dans reagentMap
						if (reagentMap.containsKey(fileReagentName) ){	
							Logger.info (fileReagentName+" TROUVE DANS HASH REAGENT");
							ReagentCatalog rc= reagentMap.get(fileReagentName);
							
							if ( null == boxCodeMap.get(rc.boxCatalogCode));
							{
							  // la boite de ce reactif n'est pas presente dans la section de declaration des boites !!!
							  // que fait-on ????
							  Logger.info("WARNING line "+ n+": reagent box not declared in file.  !!!! pour l'instant continuer qd meme");
							}
							
							// verifier que le code barre se termine par -<REACTIF>
							String fileReagentCode = cols[5].trim();
							if ( fileReagentCode.equals("")){
								// code barre reactif manquant
								Logger.info("ERROR line "+ n+": barcode missing");
							}

							if (fileReagentCode.matches("(.*)-"+fileReagentName)) {	
								
								Logger.info ("code correct:"+ fileReagentCode);
								
								//construire un reagent et l'ajouter a l'experiment...EN COURS
								ReagentUsed ru=new ReagentUsed();  

							    ru.kitCatalogCode=rc.kitCatalogCode; Logger.info ("kitCatalogCode="+ru.kitCatalogCode);
							    ru.boxCatalogCode=rc.boxCatalogCode; Logger.info ("boxCatalogCode="+ru.boxCatalogCode);
							    ru.reagentCatalogCode=rc.code; Logger.info ("reagentCatalogCode"+ru.reagentCatalogCode);
							    
							    ru.boxCode=boxCodeMap.get(rc.boxCatalogCode);  Logger.info ("boxCode="+ru.boxCode);
							    ru.code=fileReagentCode+"_" ;   Logger.info ("code="+ru.code); // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
							    String fileReagentWeight = cols[15].trim();
							    if ( !fileReagentWeight.equals("") ){
							    	ru.description=fileReagentWeight;	Logger.info ("description="+ ru.description);
							    }
								
							    experiment.reagents.add(ru); // a faire uniquement si pas d'erreur !!!!!!!!!!!

							} else {
								 Logger.info ("ERROR line "+ n +": barcode "+fileReagentCode+" must end with -"+fileReagentName); 
							}
						} 
						//else {Logger.info (fileReagentName+ " PAS TROUVE DANS HASH REAGENT..."); }// DEBUG
					} // else = ligne position|nom du reactif...	
				}
			}
		
			n++;
		} //end while

		reader.close();
		Logger.info ("END READING FILE");	
		
		if (contextValidation.hasErrors()){ 
			return experiment;
		}
		
		// ne positionner les valeurs que s'il n'y a pas d'erreur a la vérification precedente...
		if (!contextValidation.hasErrors()) {
			Logger.info ("SETTING REAGENTS...TODO...");	
			
			
		}
		
		return experiment;
	} // end import 
	
}
