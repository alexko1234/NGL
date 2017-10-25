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
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.parameter.index.Index;
import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;

public class Input extends AbstractInput {
	
   /* 25/10/2017 NGL-1326
    * Description du fichier a importer: fichier CSV generé par le Logiciel LabX de Mettler Toledo connecté a une balance XPE4002S
    * ";"  delim
    * verifier que le nom du fichier convient ? ou se baser sur la ligne 5 ???

     ???????  fichier original recu en UTF-16 Litle Endian
   */

	///// voir spectramax.input

	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
		Logger.info ("LABXMETTLERTOLEDO INPUT !!!!");	
		
		// hashMap  pour stocker les concentrations fichier 
		//VIEUX Map<String,SpectramaxData> dataMap = new HashMap<String,SpectramaxData>(0);
		
		
		// charset detection (N. Wiart)
		byte[] ibuf = pfv.value;
		//String charset = "UTF-8"; //par defaut, convient aussi pour de l'ASCII pur
		String charset = "ISO-8859-15";
		
		// si le fichier commence par les 2 bytes ff/fe  alors le fichier est encodé en UTF-16 little endian
		if (ibuf.length >= 2 && (0xff & ibuf[0]) == 0xff && (ibuf[1] & 0xff) == 0xfe) {
			charset = "UTF-16LE";
		}
		
		InputStream is = new ByteArrayInputStream(ibuf);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		int n = 0;
		boolean lastResult=false;
		String line="";
		
// TRIMER  TOUT..........TODO
		
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
			
			if ( n == 5){
				if ( ! cols[0].trim().equals("HiSeq X Flow Cell") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "HiSeq X Flow Cell");
					return experiment;
				} else {
					// verifier le code flow cell
					String flowcellId=cols[8];
					Logger.info ("flowcellId="+flowcellId);
					
					 if ( ! experiment.instrumentProperties.get("containerSupportCode").value.equals(flowcellId))  {
		        		 contextValidation.addErrors("Erreurs fichier", "Le barcode flowcell ligne 5 ("+flowcellId+") ne correspond pas à celui de l'expérience");
						 return experiment;
					}
				}
			}
			
			// commencer le traitement en sautant les 9 premieres lignes
			if (n > 9 ) {
				// ligne "Résultats principaux" =fin des data intéressantes
				if ( cols[0].trim().matches("Résultats principaux(.*)")){
					lastResult=true;
					////////////break;
				} else {
					Logger.info ("processing ligne "+ n);
					
					// separer l'import enntre les 2 experiences prep-fc-ordered / illumina-depot ???
					// ou pourrait-on tou charger pour prep-fc-ordered ????
					//if ( experiment.typeCode.equals("prepa-fc-ordered") ){
					//	Logger.info ("PREPA...");
					//} else if ( experiment.typeCode.equals("illumina-depot") ){
					//	Logger.info ("DEPOT...");
					//}
					
					if ( cols[0].equals("") && ! cols[1].trim().equals("Position") ){
						//reactifs en positions 1-->XX
						String reagent=cols[2].trim();
						Logger.info ("verifier si :"+reagent+" est un reactif d'une boite associé a "+ experiment.typeCode);

						// verifier que le code barre se termine par -<REACTIF>
						String reagentCode = cols[5].trim();
						String reagentWeight = cols[15].trim();
						if (reagentCode.matches("(.*)-"+reagent)) {
							Logger.info ("code correct=> stocker code :"+ reagentCode + " et son poids:"+ reagentWeight );
						} else {
							 Logger.info ("code "+reagentCode+" ne se termine pas par -"+reagent+" =======> erreur"); 
						}
						
					} else {
						String itemName[]=cols[0].trim().split("LOT");	
						if (itemName.length != 2){
							//ignorer sans erreur ???
							n++;
							continue;
						} else {
							String itemCode=cols[8].trim();
							Logger.info ("verifier si :"+itemName[1]+" est une boite ou un reactif associé a "+ experiment.typeCode);
							Logger.info ("si oui stocker code :"+  itemCode);
							Logger.info ("si non=> erreur");
						}
	
					}	
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
			
			/*
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					String icupos=InputHelper.getCodePosition(icu.code);				
					PropertySingleValue concentration1 = getPSV(icu, "concentration1");
					if(dataMap.containsKey(icupos)){
						concentration1.value = dataMap.get(icupos).concentration;
						// concentration1.unit = unit; ne marche pas si unit n'est pas "final"
						concentration1.unit = unit.toString();
					}
									
				});
			*/
		}
		
		return experiment;
	} // end import 
	
}
