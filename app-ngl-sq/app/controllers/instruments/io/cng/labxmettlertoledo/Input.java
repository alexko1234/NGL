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
    * Description du fichier a importer: fichier CSV ";"  delimité generé par le Logiciel LabX de Mettler Toledo 
    * LabX fait lui meme des controles, donc certains cas ne doivent pas etre gérés ici: codes barres manquants ou mal formés...
    */

	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {		
		

		//-1- Avant de commencer le parsing du fichier récupérer la liste des kits actifs / boites actives / reagents actifs pour le type d'expérience
		Logger.debug ("--GET CATALOG INFO ("+experiment.typeCode+")--");
		
		List<KitCatalog> experimentKitList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, 
				DBQuery.is("category", "Kit").and(DBQuery.is("active", true)).and(DBQuery.in("experimentTypeCodes", experiment.typeCode))).toList();

		// hashmaps pour les boites et les reactifs
		Map<String,BoxCatalog> boxMap = new HashMap<String,BoxCatalog>(0);
		Map<String,ReagentCatalog> reagentMap = new HashMap<String,ReagentCatalog>(0);
		// hashmap pour retrouver le code d'une box d'apres son nom..
		Map<String,String> boxCodeMap = new HashMap<String,String>(0);
		
		if ( experimentKitList.isEmpty() ) {
			contextValidation.addErrors("Erreur catalogue","Aucun kit actif n'est défini pour ce type d'expérience");
			return experiment;
		} else {
			for(KitCatalog kit:  experimentKitList){
				//Logger.debug ("KIT TROUVE : '"+kit.name + "'("+ kit.code+")");
				// trouver les boites de chacun des kits
				List<BoxCatalog> kitBoxList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, 
						DBQuery.is("category", "Box").and(DBQuery.is("active", true)).and(DBQuery.is("kitCatalogCode", kit.code))).toList();
				
				for(BoxCatalog box:  kitBoxList){
					//Logger.debug (" >BOITE ACTIVE TROUVEE :'"+ box.name + "'("+box.code+")");
					boxMap.put(box.name, box);
					
					// trouverles reactifs de chaque boite
					List<ReagentCatalog>  boxReagentList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, 
                     		 DBQuery.is("category", "Reagent").and(DBQuery.is("active", true)).and(DBQuery.is("boxCatalogCode", box.code))).toList();
					
					for(ReagentCatalog reagent:  boxReagentList){
						//Logger.debug ("  >>REACTIF ACTIF TROUVE :'"+reagent.name + "'("+reagent.code+ ")");
						/*!! si le meme reactif existe dans 2 boites pb !!=> la cle serait reagent.name/reagent.boxCatalogCode
						     reagentMap.put(reagent.name+"/"+reagent.boxCatalogCode , reagent);
						     mais ensuite lors de la recherche d'1 reagent ca complique. 
						     CE CAS NE DOIT PAS EXISTER d'apres la prod (17/11/2017)
						*/
						reagentMap.put(reagent.name , reagent);
					}
				}
			}
		}
		 
		//-2- Parsing
		Logger.debug ("--START PARSING--");
		
		byte[] ibuf = pfv.value;
		InputStream is = new ByteArrayInputStream(ibuf);
		
		// le fichier CSV sorti par le logiciel Labx est en ISO-8859 ( valeur retournee par la commande "file" Linux)
		String charset = "ISO-8859-15";
		
		// charset detection (N. Wiart)
		// String charset = "UTF-8"; //par defaut, convient aussi pour de l'ASCII pur
		// si le fichier commence par les 2 bytes ff/fe  alors le fichier est encodé en UTF-16 little endian
		// if (ibuf.length >= 2 && (0xff & ibuf[0]) == 0xff && (ibuf[1] & 0xff) == 0xfe) {
		//	charset = "UTF-16LE";
		// }

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		
		int nbReagentsFound=0;
		int n = 0;
		boolean lastResult=false;
		String line="";	
		
		while (((line = reader.readLine()) != null) && !lastResult ){	 
			
			// attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!
			String[] cols = line.replace (",", ".").split(";");
			

			// verifier la 1 ere ligne : doit etre "Compte rendu de séquençage"
			if (n == 0) {
				// verifier que c'est bien un fichier CSV ;delimited...
				//Logger.info ("ligne "+n+" nbre colonne="+cols.length);
				/* marche pas....length=2 pour un fichier EXcel mais =1 pour un fichier CSV !!!!!!!
				  if (cols.length != 15) {

					// pas un fichier CSV ?????
					contextValidation.addErrors("Erreurs fichier","Le fichier ne semble pas être au format CSV / ;");
					Logger.info ("col 0="+cols[0]);
					break; // si ce n'est pas le bon type de fichier la suite va sortir des erreurs incomprehensibles...terminer		
				}
				*/
				
				//  verifier que c'est bien un fichier LabX
				if ( !cols[0].trim().equals("Compte rendu de séquençage") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Compte rendu de séquençage");
					break; // si ce n'est pas le bon type de fichier la suite va sortir des erreurs incomprehensibles...terminer
				}
			}
			
			// en 6eme ligne (n=5) il y actuellement le nom d'un kit "principal" qui permet de verifier que le fichier correspond au type d'expérience
			if ( n == 5){
				if ( cols[0].trim().equals("HiSeq X Flow Cell")  ||  cols[0].trim().equals("HiSeq 3000/4000 PE Flow Cell")) {
					// verifier le code flow cell
					String flowcellId=cols[8].trim();
					//Logger.info ("flowcellId="+flowcellId);

					if ( experiment.typeCode.equals("prepa-fc-ordered") || experiment.typeCode.equals("prepa-flowcell") ) {
						// barcode FC dans propriete d'instrument ( ou container out)
						if ( !experiment.instrumentProperties.get("containerSupportCode").value.equals(flowcellId))  {
						    contextValidation.addErrors("Erreurs fichier", "ligne 6 : Le code flowcell ("+flowcellId+") ne correspond pas à celui de l'expérience.");
							break; // terminer
						}
					}
					
					if ( experiment.typeCode.equals("illumina-depot") ) {
						//barcode FC dans container ininputContainerSupportcodes
						if ( ! experiment.inputContainerSupportCodes.contains(flowcellId) )  {
						    contextValidation.addErrors("Erreurs fichier", "ligne 6 : Le code flowcell ("+flowcellId+") ne correspond pas à celui de l'expérience.=="+ experiment.inputContainerSupportCodes);
							break; // terminer
						}
					}		
				} else {	
					contextValidation.addErrors("Erreurs fichier","ligne 6 :'"+ cols[0].trim() + "': type de fichier LabX non pris en charge.");
					break; // terminer
				} 
			}
			
			if (n > 5 ) {
				// ligne "Résultats principaux..." = fin des données a parser
				if ( cols[0].trim().matches("Résultats principaux(.*)")){
					lastResult=true;
				} 

				// premiere section colonne 0 contient un label 
				// 07/11/2017:  pour la flowcell => LOT; pour le manifold => SN; pour le reste =>RGT
				if ( !cols[0].trim().equals("") ){
					//Logger.info ("processing ligne "+ n +" section  boites..)...");
					
					String item[]= null;
					if      ( cols[0].trim().matches("LOT (.*)") ){ item=cols[0].trim().split("LOT"); }
					else if ( cols[0].trim().matches("SN (.*)")  ){ item=cols[0].trim().split("SN");  }
					else if ( cols[0].trim().matches("RGT (.*)") ){ item=cols[0].trim().split("RGT"); }
					
					if ( null == item){
						// ignorer (ligne Code Barre ou ligne Résultat ou ...)
						//Logger.info ("skip line ("+ n +") :"+ cols[0].trim() );
						n++;
						continue;
					} else {
						String fileItemName=item[1].trim();
						String fileItemCode=cols[8].trim();
						
						// Logger.info ("ligne "+ n +" chercher si :"+fileItemName+" est une boite OU un reactif actifs");		
						//-1- chercher dans boxMap
						if ( boxMap.containsKey(fileItemName) ){	
							Logger.debug (fileItemName+ ": TROUVE DANS HASH BOX");
							BoxCatalog bc= boxMap.get(fileItemName);
							boxCodeMap.put(bc.code, fileItemCode+"_"); // stocker le barcode de boite pour ses reagents plus tard...
							
							// construire un reagentUsed et l'ajouter a l'experiment
							ReagentUsed ru=new ReagentUsed();  
							ru.kitCatalogCode=bc.kitCatalogCode;  
							ru.boxCatalogCode=bc.code; 
							ru.boxCode=fileItemCode+"_" ;  // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite;
			  
						    experiment.reagents.add(ru); 
						    nbReagentsFound++;
						    
							//DEBUG
						    /*
							Logger.info("kitCatalogCode="+ru.kitCatalogCode);
							Logger.info("boxCatalogCode="+ru.boxCatalogCode);
							Logger.info("boxCode="+ ru.boxCode); 
							*/
						} 
						//-2- chercher dans mapReagent
						else if ( reagentMap.containsKey(fileItemName) ){	
							Logger.debug (fileItemName+ ": TROUVE DANS HASH REAGENT");
							ReagentCatalog rc= reagentMap.get(fileItemName);
							
							if (null == boxCodeMap.get(rc.boxCatalogCode)){
							   /* la boite de ce reactif n'est pas presente dans la section de déclaration des boites !!!
							    *  => mauvaise declaration du catalog
							    *  => peut aussi arriver si un reactif apparait dans 2 boites actives: CE CAS NE DOIT PAS EXISTER d'apres la prod (17/11/2017)
							    */	
								contextValidation.addErrors("Erreurs fichier", "ligne "+ (n+1) +": boîte du réactif '"+ fileItemName+ "' non trouvée dans le fichier OU réactif trouvé dans plus d'une boîte.");
							} 
							// construire un reagentUsed et l'ajouter a l'experiment
							ReagentUsed ru=new ReagentUsed();  
							ru.kitCatalogCode=rc.kitCatalogCode; 
						    ru.boxCatalogCode=rc.boxCatalogCode; 
							ru.reagentCatalogCode=rc.code; 
							ru.boxCode=boxCodeMap.get(rc.boxCatalogCode); 
							ru.code=fileItemCode+"_" ; // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
							// ru.description=....     // pas de description dans cette section du fichier...	
							
							experiment.reagents.add(ru); 
							nbReagentsFound++;
							
							//DEBUG
							/*
							Logger.info("kitCatalogCode="+ru.kitCatalogCode);
							Logger.info("boxCatalogCode="+ru.boxCatalogCode);
							Logger.info("reagentCatalogCode="+ru.reagentCatalogCode);
							Logger.info("boxCode="+ru.boxCode); 
							Logger.info("code="+ ru.code); 
							*/
						} else { 
							Logger.debug ("line ("+ n +"): item <"+fileItemName+ ">PAS TROUVE DANS HASHREAGENT NI HASHBOX ...pour type experience en cours");
						}
					}
				} else {
					// changement de section...
					if ( !cols[1].trim().equals("Position") ){
						//Logger.info ("processing ligne "+ n +" section  position...");		
						// reactifs en positions 1-->N
						String fileReagentName=cols[2].trim();
						
						// dans cette section ne doivent se trouver QUE des reactifs (pas de boites ni de kits)	=> chercher dans reagentMap
						// Logger.info ("ligne "+ n+ " chercher si :<"+fileReagentName+"> est un reactif actif");
						if (reagentMap.containsKey(fileReagentName) ){	
							Logger.debug (fileReagentName+": TROUVE DANS HASH REAGENT");
							ReagentCatalog rc= reagentMap.get(fileReagentName);
							
							if ( null == boxCodeMap.get(rc.boxCatalogCode)){
							  /* la boite de ce reactif n'est pas presente dans la section de déclaration des boites !!!
							   *  => mauvaise declaration du catalog
							   *  => peut aussi arriver si un reactif apparait dans 2 boites actives: CE CAS NE DOIT PAS EXISTER d'apres la prod (17/11/2017)
							   */
							  //Logger.info("ERROR line ("+ n +") reagent box of '"+fileReagentName+"' not declared in file.(looking for:"+rc.boxCatalogCode+")");
						      contextValidation.addErrors("Erreurs fichier", "ligne "+ (n+1) +": boîte du réactif '"+ fileReagentName+ "' non trouvée dans le fichier OU réactif trouvé dans plus d'une boîte.");
							}
							
							String fileReagentCode = cols[5].trim();
							//construire un reagent et l'ajouter a l'experiment
							ReagentUsed ru=new ReagentUsed();  

							ru.kitCatalogCode=rc.kitCatalogCode; 
							ru.boxCatalogCode=rc.boxCatalogCode; 
							ru.reagentCatalogCode=rc.code;    
							ru.boxCode=boxCodeMap.get(rc.boxCatalogCode);  
							ru.code=fileReagentCode+"_" ;    // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
							    
							String fileInputReagentWeight = cols[6].trim();
							String fileOutputReagentWeight = cols[11].trim();
							String fileDiffReagentWeight = cols[15].trim();
							// meme si a priori la recherche n'est pas possible actuellement sur le champs description, utiliser aussi "_" pour separer les items
							ru.description=fileInputReagentWeight +"_"+ fileOutputReagentWeight +"_"+ fileDiffReagentWeight;	
							
							experiment.reagents.add(ru);  
							nbReagentsFound++;
							
							//DEBUG
							/*
							Logger.info ("kitCatalogCode="+ru.kitCatalogCode);
							Logger.info ("boxCatalogCode="+ru.boxCatalogCode);
							Logger.info ("reagentCatalogCode"+ru.reagentCatalogCode);
							Logger.info ("boxCode="+ru.boxCode);
							Logger.info ("code="+ru.code);
							Logger.info ("description="+ ru.description);
							*/
						} 
						/*else {
							Logger.info ("DEBUG line ("+ n +"): <"+fileReagentName+ "> PAS TROUVE DANS HASH REAGENT...de ce type d'experience"); 
						}
						*/
					} 
					// else = ligne position|nom du reactif...	
				}
			}
		
			n++;
		} //end while

		reader.close();
		Logger.debug ("--END PARSING--");
		
		// Dans quel cas ca peut sortir ? Mauvais fichier LabX selectionné
		if (nbReagentsFound==0 ) {
			contextValidation.addErrors("Erreurs fichier","Aucun réactif trouvé dans le fichier pour ce type d'expérience.");
		}
		
		return experiment;
	}
}