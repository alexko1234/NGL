package controllers.instruments.io.cng.labxmettlertoledo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.description.ReagentCatalog;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import play.Logger;
import validation.ContextValidation;
import controllers.instruments.io.utils.AbstractInput;
import fr.cea.ig.MongoDBDAO;

public class Input extends AbstractInput {
	
   /* 25/10/2017 NGL-1326
    * Description du fichier à importer: fichier CSV ";"  delimité generé par le Logiciel LabX de Mettler Toledo 
    * LabX fait lui meme des controles, donc certains cas ne doivent pas etre gérés ici: codes barres manquants ou mal formés...
    * 
    * 22/11/2017 NGL-1710 modifications... 
    * le fichier Mettler V2 n'est pas pret. Repartir du fichier V1 ( 2 lignes LOT et RGT pour les boîtes et "réactifs" non pesés )
    * => on ne peut plus créer les RU apres le traitement d'une ligne, il faut les stocker en attente dans des Hash et les creer tous a la fin
    * Structuration du code pour le rendre plus lisible et modulaire
    * Attention le flag "active" pour les reagents est toujours a true dans la base mongo, NE PAS L'UTILISER
    * 
    * 09/01/2018 algo V3
    *            ajout Exception et try/catch
    */
	
	private class CatalogException extends Exception{
		public CatalogException(String message) { super(message); }
	}

	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {		
		
		//-1-------- Récupérer la liste des kits actifs / boîtes actives / reactifs actifs 
		//Logger.debug ("--GET CATALOGS INFO --");
		
		// currCatalog est le catalog pour le type d'experience en cours, otherCatalog est le catalogue pour l'autre experience egalement traitee dans le fichier
		// si type experience en cours = "prepa-fc-ordered" alors l'autre est "illumina-depot" (et vice versa)
		ExperimentCatalog currCatalog=null;
		ExperimentCatalog otherCatalog=null;
		String otherExperimentTypeCode=null;
		
		try {
			if ( experiment.typeCode.equals("prepa-fc-ordered") ) {
				otherExperimentTypeCode="illumina-depot";

				currCatalog= new ExperimentCatalog ("prepa-fc-ordered"); 
				otherCatalog= new ExperimentCatalog (otherExperimentTypeCode);
		
			} else {
				// POUR TEST erreur catalogue: otherExperimentTypeCode="prepa-fc-orderedddd";
				otherExperimentTypeCode="prepa-fc-ordered";
			
				currCatalog= new ExperimentCatalog ("illumina-depot");
				otherCatalog= new ExperimentCatalog (otherExperimentTypeCode);
			}
		} catch (CatalogException e) {
			contextValidation.addErrors("Erreurs Catalogue",e.getMessage());
			return experiment;
		}
		
		
		//-2-------- Parsing
		Logger.debug ("-- START PARSING --");
		
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
		
		// HashMap pour les reagentUsed valides au cours du parsing
		Map<String,ReagentUsed> parsedReagentsMap = new HashMap<String,ReagentUsed>(0);
		
		// 30/11/2017 creer un hashMap distinct pour les box permet de ne pas creer les box a la fin
		Map<String,ReagentUsed> parsedBoxesMap = new HashMap<String,ReagentUsed>(0);
				
		int n = 0;
		boolean lastResult=false;
		String line="";	
		
		while (((line = reader.readLine()) != null) && !lastResult ){	 
		
			// attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!
			String[] cols = line.replace (",", ".").split(";");
			
			if (n == 0) {	
				if (!firstLineCorrect(cols, contextValidation) ){
					break;
				}
			} else if (n == 5) {
				if (!fifthLineCorrect(cols, experiment, contextValidation) ){
					break;
				}
			} else if (n > 5 ) {
				// ligne "Résultats principaux..." = fin des données a parser
				if ( cols[0].trim().matches("Résultats principaux(.*)")){
					lastResult=true;
				} else {
					if ( !cols[0].trim().equals("") ){
						// section boîtes
						// 30/11/2017 ajouter Map pour les box
						processBoxSectionLine(n, cols, contextValidation, currCatalog, otherCatalog, parsedReagentsMap, parsedBoxesMap ) ;
					} else if ( !cols[1].trim().equals("Position") ){
						//section resultats
						processPositionSectionLine(n, cols, contextValidation, currCatalog, otherCatalog, parsedReagentsMap, parsedBoxesMap);
					}
				}
			}
			n++;
		}//end while
		
		reader.close();
		//Logger.debug ("-- END PARSING --");
			
		//-3---------- creer les reagents used parsés
		if ( ! contextValidation.hasErrors() ) {
			//Logger.debug ("-- CREATION DES REAGENTS --");

			// 30/11/2017 avec 2 map distincts on peut ne creer que les reagent et pas les box !!
			// 01/12/2017 oui mais certaines boites particulieres doivent etre crees qd meme (la boite de la flow cell , et la boite du Manifold )
			for (Map.Entry<String, ReagentUsed> pair : parsedBoxesMap.entrySet())
            {
				//Logger.debug("parsedBOX... :"+ pair.getKey());
				if ( (pair.getKey().equals("HiSeq X Flow Cell")) || (pair.getKey().equals("HiSeq 3000/4000 PE Flow Cell"))|| (pair.getKey().matches("(.*)Manifold(.*)")) ){
					Logger.debug("creation boite: "+ pair.getKey());
					experiment.reagents.add(pair.getValue());
				}
            }
			
			for (Map.Entry<String, ReagentUsed> pair : parsedReagentsMap.entrySet())
            {
                Logger.debug("creation reagent: "+ pair.getKey());
                experiment.reagents.add(pair.getValue());
            }
		}
		
		return experiment;
	}
	
	private static boolean firstLineCorrect( String[] cols, ContextValidation contextValidation) {
		//Logger.debug (">>>firstLineCorrect:"+ cols[0]);
		
		// verifier que c'est bien un fichier CSV ;delimited...
		
		//Logger.info ("ligne "+n+" nbre colonne="+cols.length);
		/* marche pas....length=2 pour un fichier EXcel mais =1 pour un fichier CSV !!!!!!!
		if (cols.length != 15) {
			// pas un fichier CSV ?????
			contextValidation.addErrors("Erreurs fichier","Le fichier ne semble pas être au format CSV / ;");
			Logger.info ("col 0="+cols[0]);
			return false; // si ce n'est pas le bon type de fichier la suite va sortir des erreurs incomprehensibles...terminer		
		}
		*/
			
		//  verifier que c'est bien un fichier LabX pour sequençage
		if ( !cols[0].trim().equals("Compte rendu de séquençage") ) {
			contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Compte rendu de séquençage");
			return false ; // si ce n'est pas le bon type de fichier la suite va sortir des erreurs incomprehensibles...terminer
		}
		
		return true;
	}
	
	private static boolean fifthLineCorrect( String[] cols, Experiment experiment, ContextValidation contextValidation) {
		//Logger.debug (">>>fifthLineCorrect:" + cols[0]);
		
		// en 6eme ligne (n=5) il y actuellement le nom d'un kit "de Flow Cell" qui permet de vérifier que le fichier correspond au type d'expérience
		//HARDCODED
		if ( cols[0].trim().equals("HiSeq X Flow Cell") || cols[0].trim().equals("HiSeq 3000/4000 PE Flow Cell")) {
			// verifier le code flow cell
			String flowcellId=cols[8].trim();
			//Logger.info ("flowcellId="+flowcellId);

			if ( experiment.typeCode.equals("prepa-fc-ordered") ) {	
				// barcode FC dans propriete d'instrument (ou container out c'est pareil)
				if ( !experiment.instrumentProperties.get("containerSupportCode").value.equals(flowcellId))  {
				    contextValidation.addErrors("Erreurs fichier", "ligne 6 : Le code flowcell ("+flowcellId+") ne correspond pas à celui de l'expérience.");
					return false;
				}
			} else {
				// ("illumina-depot").equals.experiment.typeCode
				// barcode FC dans container in inputContainerSupportcodes
				if ( ! experiment.inputContainerSupportCodes.contains(flowcellId) )  {
				    contextValidation.addErrors("Erreurs fichier", "ligne 6 : Le code flowcell ("+flowcellId+") ne correspond pas à celui de l'expérience.");
					return false;
				}
			}		               
			
			return true;
			
		} else {	
			contextValidation.addErrors("Erreurs fichier","ligne 6 :'"+ cols[0].trim() + "': type de fichier LabX non pris en charge.");
			return false;
		} 
	}
	
	// 30/11/2017 ajouter hashMap pour box
	private static void processBoxSectionLine( int n, String[] cols, ContextValidation contextValidation, ExperimentCatalog currCatalog, ExperimentCatalog otherCatalog, Map<String,ReagentUsed> parsedReagentsMap, Map<String,ReagentUsed> parsedBoxesMap) {
		
		// section "boîtes" : colonne 0 contient un label, contient des boîtes ET des reactifs !!!!
		// 07/11/2017:  pour la flowcell => LOT; pour le manifold => SN; pour le reste =>RGT
		// 23/11/2017: en V1 on a LOT *ET* RGT  => on va trouver 2 lignes pour chaque item !!! il faut concaténer les valeurs trouvées pour la création du reagent used
		//Logger.debug ("processing ligne "+ n +" section boîtes");
		
		String item[]= null;
		String fileItemName=null;
		String fileItemCode=null;
		
		if      ( cols[0].trim().matches("LOT (.*)") ){ item=cols[0].trim().split("LOT"); }
		else if ( cols[0].trim().matches("SN (.*)")  ){ item=cols[0].trim().split("SN");  }
		else if ( cols[0].trim().matches("RGT (.*)") ){ item=cols[0].trim().split("RGT"); }
		
		if ( null == item){
			// ignorer (ligne Code Barre ou ligne Résultat ou imprevue...)
			//Logger.debug ("skip line ("+ n +") :"+ cols[0].trim() );
			return;	
		} 
		
		fileItemName=item[1].trim();
		fileItemCode=cols[8].trim();
			
		//-1- chercher si existe dans TOUT le catalogue !!
		//Logger.debug ("ligne "+ n +" chercher :" +fileItemName + " dans TOUT le catalogue");	
		
		// Pas trouvé comment chercher sur "name" uniquement, il faut 2 requetes, sinon pb de class
		List<ReagentCatalog> matchListReagent = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, (DBQuery.is("category", "Reagent").and(DBQuery.is("name",fileItemName)))).toList();
		List<BoxCatalog> matchListBox = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, (DBQuery.is("category", "Box").and(DBQuery.is("name", fileItemName)))).toList();
		
		if (matchListReagent.size() == 0 && matchListBox.size() == 0 ) {
			contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)  +": '"+ fileItemName+ "': n'existe pas dans le catalogue (ni boîte, ni réactif).");
			return ; 
		} 
		//29/11/2017 Julie estime qu'on ne doit pas trouver une boîte ou un reactif plusieurs fois dans le catalogue !!
		
		/* 09/01/2018 suppression unicité reagent dans tous le catalogue; ce qui compte c'est l'unicité dans les boites declarees pour l'experience...
		if ( matchListReagent.size() > 1 ) {
			contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)  +":'"+ fileItemName+ "': réactif trouvé plusieurs fois dans le catalogue.");
			return ; 	
		} */
		
		/* 09/01/2018 suppression unicité reagent dans tous le catalogue, ce qui compte ce sont les boites des kits actifs=> traité au niveau new ExperimentCatalog
		if ( matchListBox.size() > 1 )  {
			contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)  +":'"+ fileItemName+ "': boîte trouvée plusieurs fois dans le catalogue.");
			return ; 
		}
		*/
		
		/* 09/01/2018quelle importance ?? en plus test inexact par exemple 2 boite de meme nom et un reactif de meme nom 
		if (matchListReagent.size() == 1 && matchListBox.size() == 1 ) {
			// l'utilisateur a créé une boite et un reactif de meme nom ???????
			contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)  +":'"+ fileItemName+ "':  boîte et réactif de même nom.");
			return ;
		}
		*/
		

		if ( matchListBox.size() > 0 ) {  // c'est une boîte;  09/01/2018 chger le test en > 0 au lieu de ==1 (voir ci dessus)
			//-1- chercher dans current boxMap
			if ( currCatalog.boxMap.containsKey(fileItemName) ){	
				//Logger.debug (fileItemName+ ": DANS CURR BOX HASHMAP...");
				
				BoxCatalog currBc= currCatalog.boxMap.get(fileItemName); 
				// construire un reagentUsed s'il n'est pas deja dans le hash, le completer sinon... necessaire en V1 car il y a 2 lignes par boite/reactif 
				ReagentUsed ru=null;
				if ( ! parsedBoxesMap.containsKey(fileItemName) ){
					ru=new ReagentUsed();  
					ru.kitCatalogCode=currBc.kitCatalogCode;  
					ru.boxCatalogCode=currBc.code; 
					ru.boxCode=fileItemCode+"_" ;  // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
					
					// rafraichir la liste des reactifs succeptibles d'etre trouvés grace a cette nouvelle boite d'apres son code
					Logger.debug("mise a jour des reactifs de la boite :"+ fileItemName + "("+ currBc.code + ")" );
					updateExperimentCatalog(currBc.code, currCatalog, contextValidation );
					
				} else {
					Logger.debug ("boite deja connue; mise a jour du code de boite:"+ fileItemName + "("+ currBc.code + ")" );
					ru= parsedBoxesMap.get(fileItemName);
					ru.boxCode=ru.boxCode+fileItemCode+"_" ;
				}

				parsedBoxesMap.put(fileItemName, ru);
				
				//!! utiliser le boxCode mis a jour !! pour ses reagents plus tard...
				//Logger.debug(" ajouter boite :'" +fileItemName+ "'("+ currBc.code+ ") dans currCatalog boxCodeMap");
				currCatalog.boxCodeMap.put(currBc.code,ru.boxCode);
				
				   //DEBUG
				   //Logger.debug(">>BOX:kitCatalogCode="+ru.kitCatalogCode);
				   //Logger.debug(">>BOX:boxCatalogCode="+ru.boxCatalogCode);
				   //Logger.debug(">>BOX:boxCode="+ ru.boxCode); 
			} 
			//-2- chercher dans other BoxMap
			else if ( otherCatalog.boxMap.containsKey(fileItemName) ){
				//Logger.debug (fileItemName+ ": BOITE DANS OTHER BOX HASHMAP...");
				// rien a faire, sera traité lors de l'autre import....
			}
			else
			{
				//    1- soit boite inactive soit dans kit inactif  soit pas relié aux 2 expériences impliquees...
				// ou 2- la boite n'a pas ete trouvee dans le fichier donc updateExperimentCatalog n'a pas pu faire son travail
				//   comment distinguer le cas 2 ???????
				
				contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1) +": '"+ fileItemName+ "': boîte du réactif inactive ou est dans un kit inactif ou non reliée aux type d'expériences attendus");
				return ; 
			}	
		} else if ( matchListReagent.size() > 0 ) { // c'est un réactif; 09/01/2018 chger le test en > 0 au lieu de ==1
			//-1- chercher dans current ReagentMap
			if ( currCatalog.reagentMap.containsKey(fileItemName) ){	
				Logger.debug (fileItemName+ ": REACTIF DANS CURR REAGENT HASH...");
				
				ReagentCatalog currRc= currCatalog.reagentMap.get(fileItemName);	
				if (null == currCatalog.boxCodeMap.get(currRc.boxCatalogCode)){
					Logger.debug ("chercher boite "+ currRc.boxCatalogCode +" dans currCatalog boxCodeMap");
					contextValidation.addErrors("Erreurs fichier", "ligne "+ (n+1) +": "+ fileItemName +"':  boîte du réactif manquante dans le fichier");
					return;
				} 
				
				// construire un reagentUsed s'il n'est pas deja dans le hash, le completer sinon... necessaire en V1 car il y a 2 lignes par boite/reactif 
				ReagentUsed ru=null;
				if (! parsedReagentsMap.containsKey(fileItemName) ){	
					ru=new ReagentUsed();  
					ru.kitCatalogCode=currRc.kitCatalogCode; 
					ru.boxCatalogCode=currRc.boxCatalogCode; 
					ru.reagentCatalogCode=currRc.code; 
					ru.boxCode=currCatalog.boxCodeMap.get(currRc.boxCatalogCode); 
					ru.code=fileItemCode+"_" ; // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
					// ru.description=....     // pas de description dans cette section du fichier...	
					
				} else {
					//Logger.debug ("mise a jour code de réactif");
					ru= parsedReagentsMap.get(fileItemName);
					ru.code=ru.code +fileItemCode+"_" ;
				}
				parsedReagentsMap.put(fileItemName, ru);
				
					//DEBUG	
					//Logger.debug(">>REAG:kitCatalogCode="+ru.kitCatalogCode);
					//Logger.debug(">>REAG:boxCatalogCode="+ru.boxCatalogCode);
					//Logger.debug(">>REAG:reagentCatalogCode="+ru.reagentCatalogCode);
					//Logger.debug(">>REAG:boxCode="+ru.boxCode); 
					//Logger.debug(">>REAG:code="+ ru.code); 
			}
			//-2- chercher dans other ReagentMap	
			else if ( otherCatalog.reagentMap.containsKey(fileItemName) ){
				Logger.debug (fileItemName+ ": REACTIF DANS OTHER REAGENT HASHMAP..."); 
				// rien a faire, sera traité lors de l'autre import....
			} 
			else
			{
				//Logger.debug (fileItemName+ ": NI DANS CURRENT HASHMAP NI DANS OTHER REAGENT HASHMAP..."); 
				//    1- soit boite inactive soit dans kit inactif  soit pas relié aux 2 expériences impliquees...
				// ou 2- la boite n'a pas ete trouvee dans le fichier donc updateExperimentCatalog n'a pas pu faire son travail
				//   comment distinguer le cas 2 ???????

				contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1) +": '"+ fileItemName+ "': boîte du réactif inactive ou dans un kit inactif ou non relié aux types expériences attendus");
				return; 
			}
		}	
		
		return;
	}
		
	private static void processPositionSectionLine( int n, String[] cols,  ContextValidation contextValidation, ExperimentCatalog currCatalog, ExperimentCatalog otherCatalog, Map<String,ReagentUsed> parsedReagentsMap, Map<String,ReagentUsed> parsedBoxesMap ) {

		Logger.debug ("processing ligne "+ n +" section  position...");		
		// reactifs en positions 1-->N
		// dans cette section ne doivent se trouver QUE des reactifs (pas de boîtes ni de kits)	=> chercher dans reagentMap
		
		String fileReagentName=cols[2].trim();
		
		//-1- chercher si existe dans TOUT le catalogue !!	
		//Logger.debug ("ligne "+ n +" chercher :"+fileReagentName + " dans TOUT le catalogue");	
		
		// ici on peut ajouter le fitre type dans la requete (evite l'erreur: Class models.laboratory.reagent.description.BoxCatalog not subtype of [simple type, class models.laboratory.reagent.description.ReagentCatalog]
		// s'il y a autre chose (kit ou boite) de meme nom !!!
		List<ReagentCatalog>  testList= MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, DBQuery.is("category", "Reagent").and(DBQuery.is("name", fileReagentName))).toList();

		if (testList.size() == 0 ) {
			contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)+ ": '"+ fileReagentName+ "': ce réactif n'existe pas dans le catalogue.");
			return;
		// 08/01/2018 suppression unicité dans le catalogue car on le rechche dans les boites trouvees
		//} 
		//else if ( testList.size() > 1 ) {
		//	contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1)  +":'"+ fileReagentName+ "': réactif trouvé plusieurs fois dans le catalogue.");
		//	return ;
		} else {
			Logger.debug (fileReagentName + "existe...");
			
			//-1- chercher dans current reagentMap
			if (currCatalog.reagentMap.containsKey(fileReagentName) ){	
				//Logger.debug (fileReagentName+": REACTIF DANS CURRENT REAGENT HASHMAP...");
				
				ReagentCatalog currRc= currCatalog.reagentMap.get(fileReagentName);	
				
				//Logger.debug ("chercher boite "+ currRc.boxCatalogCode +" dans currcatalog boxCodemap");
				// NE SORT PAS car ...currCatalog.boxCodeMap est rempli des le depart 
				if ( null == currCatalog.boxCodeMap.get(currRc.boxCatalogCode)){
					//la boîte de ce reactif n'est pas présente dans la section de déclaration des boîtes ??
				    contextValidation.addErrors("Erreurs fichier", "ligne "+ (n+1) +": '"+ fileReagentName+ "': boîte du réactif manquante dans le fichier.");
				    return;
				}
					
				String fileReagentCode = cols[5].trim();
				String position= cols[1].trim();
				
				// construire un reagent 
				// dans cette section on PEUT trouver le même réactif a plusieurs lignes (plusieurs positions), c'est normal !! => ne pas concatener les infos
				// par contre la cle du hash doit etre nom/position
				ReagentUsed ru=new ReagentUsed();  

				ru.kitCatalogCode=currRc.kitCatalogCode; 
				ru.boxCatalogCode=currRc.boxCatalogCode;
				ru.reagentCatalogCode=currRc.code; 
				ru.boxCode=currCatalog.boxCodeMap.get(currRc.boxCatalogCode);  
				// TEST ajouter la position dans code ?? a garder ou pas voir avec Julie==> NON
				//ru.code="Position-"+position+"_"+fileReagentCode+"_" ;    // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
				ru.code=fileReagentCode+"_";    // !!!! les codes doivent se terminer par "_" pour etre filtrables par la suite
				
				String fileInputReagentWeight = cols[6].trim();
				String fileOutputReagentWeight = cols[11].trim();
				String fileDiffReagentWeight = cols[15].trim();
				// meme si a priori la recherche n'est pas possible actuellement sur le champs description, utiliser aussi "_" pour separer les items
				ru.description=fileInputReagentWeight +"_"+ fileOutputReagentWeight +"_"+ fileDiffReagentWeight;	
				
				parsedReagentsMap.put(fileReagentName+"/"+position, ru);
					
					//DEBUG
					//Logger.debug (">>REAG2:kitCatalogCode="+ru.kitCatalogCode);
					//Logger.debug (">>REAG2:boxCatalogCode="+ru.boxCatalogCode);
					//Logger.debug (">>REAG2:reagentCatalogCode"+ru.reagentCatalogCode);
					//Logger.debug (">>REAG2:boxCode="+ru.boxCode);
					//Logger.debug (">>REAG2:code="+ru.code);
					//Logger.debug (">>REAG2:description="+ ru.description);
			} 
			//-2- chercher dans other ReagentMap
			else if ( otherCatalog.reagentMap.containsKey(fileReagentName) ){
				//Logger.debug (fileReagentName+ ": REACTIF DANS OTHER REAGENT HASHMAP..."); 
				//rien a faire sera traité dans prochain import
			}
			else {
				//Logger.debug (fileReagentName+ ": NI DANS CURRENT HASHMAP NI DANS OTHER REAGENT HASHMAP..."); 
				//    1- existe mais soit pas boite inactive soit dans un kit inactif soit pas relié aux 2 expériences impliquees...
				// ou 2- la boite n'a pas ete trouvee dans le fichier donc updateExperimentCatalog n'a pas pu faire son travail
				//  comment distinguer le cas 2 ????
				
				contextValidation.addErrors("Erreurs fichier","ligne "+ (n+1) +": '"+ fileReagentName+"': boîte du réactif inactive ou dans un kit inactif ou non relié aux types expériences attendus.");
				return;
			}
		} 
	}
	

	private class ExperimentCatalog {
		
		// attributs	
		private Map<String,BoxCatalog> boxMap = new HashMap<String,BoxCatalog>(0);
		private Map<String,ReagentCatalog> reagentMap = new HashMap<String,ReagentCatalog>(0);
		private Map<String,String> boxCodeMap = new HashMap<String,String>(0); // hashmap pour recuperer le code d'une box d'apres son nom..
		
		//constructeur 
		public ExperimentCatalog (String experimentTypeCode) throws CatalogException {
				getCatalogInfoExperiment (experimentTypeCode, boxMap, reagentMap, boxCodeMap);
		}
		
		// methodes
		private void  getCatalogInfoExperiment(String experimentTypeCode, Map<String,BoxCatalog> boxMap,Map<String,ReagentCatalog> reagentMap, Map<String,String> boxCodeMap ) throws CatalogException {
			//Logger.debug (" -- getCatalogInfoExperiment for "+experimentTypeCode+ "--");
			
			List<KitCatalog> kitList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, 
					DBQuery.is("category", "Kit").and(DBQuery.is("active", true)).and(DBQuery.in("experimentTypeCodes", experimentTypeCode))).toList();
			
			if ( kitList.isEmpty() ) {	
				//Logger.debug ("PAS DE KIT ASSOCIE !!!");	
				throw new CatalogException("Aucun kit actif pour l'expérience "+ experimentTypeCode);
			} else {
				for(KitCatalog kit:  kitList){
					//Logger.debug ("KIT: '"+kit.name + "'("+ kit.code+")");
					
					// trouver les boîtes de chacun des kits actifs
					List<BoxCatalog> kitBoxList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, 
							DBQuery.is("category", "Box").and(DBQuery.is("active", true)).and(DBQuery.is("kitCatalogCode", kit.code))).toList();
					
					for(BoxCatalog box:  kitBoxList){
						//Logger.debug (" boîte ACTIVE:'"+ box.name + "'("+box.code+")");
						// 09/01/2018 verifier que la meme boîte n'existe pas deja dans un autre  kit actif !!!!
						if (boxMap.containsKey(box.name) ){
							//Logger.debug ("BOITE EN DOUBLON :"+ box.name + " dans boxMap !!");
							throw new CatalogException("Kit '"+ kit.name +"' :une boîte active de même nom '"+ box.name +"' existe déjà dans un autre kit actif pour ce type d'expérience");
						}	 
						//Logger.debug ("  ...ajoutee dans boxMap");
						boxMap.put(box.name, box);		
					}
			     }
		    }	
		}
	} // fin private class ExperimentCatalog
	
	
	// ajouter les reactifs d'une boite qui a ete validée
	private static void updateExperimentCatalog (String boxCatalogCode, ExperimentCatalog currCatalog, ContextValidation contextValidation)  {
		Logger.debug ("   update reagentMap de: "+ boxCatalogCode);

		//trouver la liste des reagent correspondant a box... les ajouter dans currCatalog.reagentMap
		// !! le flag "active" des reactifs est toujours "true" inutile de l'utiliser !!!
		List<ReagentCatalog>  boxReagentList = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, 
        		 DBQuery.is("category", "Reagent").and(DBQuery.is("active", true)).and(DBQuery.is("boxCatalogCode", boxCatalogCode))).toList();
		
		for(ReagentCatalog reagent: boxReagentList){
			//Logger.debug ("reactif "+reagent.name + "'("+reagent.code+ ").....");
			
			if ( ! currCatalog.reagentMap.containsKey(reagent.name) ){
				//Logger.debug ("   ....REACTIF ACTIF AJOUTE");
				currCatalog.reagentMap.put(reagent.name, reagent);
			} else {
				// le meme reactif existe deja dans une autre boite active !!
				//Logger.debug ("   ....REACTIF EN DOUBLON");
				contextValidation.addErrors("Erreurs catalogue","le réactif '"+ reagent.name+ "' existe dans plusieurs boîtes actives pour ce type d'expérience");	
			}
		}
		return;
	}
}