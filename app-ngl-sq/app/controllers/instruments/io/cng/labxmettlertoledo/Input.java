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

     ???????  fichier original recu en UTF-16 Litle Endian
   */
	
/*   CODE COPIE SUR SPECTRAMAX..... A ADAPTER */

	@Override
	public Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
		Logger.info ("LABXMETTLERTOLEDO INPUT !!!!");	
		
		
		// hashMap  pour stocker les concentrations fichier 
		Map<String,SpectramaxData> dataMap = new HashMap<String,SpectramaxData>(0);
		
		// charset detection (N. Wiart)
		byte[] ibuf = pfv.value;
		String charset = "UTF-8"; //par defaut, convient aussi pour de l'ASCII pur
		
		// si le fichier commence par les 2 bytes ff/fe  alors le fichier est encodé en UTF-16 little endian
		if (ibuf.length >= 2 && (0xff & ibuf[0]) == 0xff && (ibuf[1] & 0xff) == 0xfe) {
			charset = "UTF-16LE";
		}
		
		InputStream is = new ByteArrayInputStream(ibuf);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		int n = 0;
		boolean lastwell=false;
		String line="";
		
		// String unit="";  ne marche pas car le compilateur reclame un objet final...utiliser StringBuilder (N Wiart)
		StringBuilder unit = new StringBuilder();
		
		// code pour trouver la bonne unité si jamais celle ci est variable dans le fichier!!!
		//if ( fields[?????].matches("(.*)Conc.(.*)")){
		//	unit.append("ng/µL");
		//} else {
		//	unit.append("nM");
		//}
		
		unit.append("ng/µL");
		
		while (((line = reader.readLine()) != null) && !lastwell ){	 
			 // attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!
			 String[] cols = line.replace (",", ".").split("\t");

			// verifier la ligne d'entete (3 eme ligne du fichier)
			if (n == 2) {
				if ( ! cols[1].equals("Wells") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Wells");
					return experiment;
				}
				if ( ! cols[7].equals("Moyenne") ) {
					contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Moyenne");
					return experiment;
				}
			}
			
			// commencer le traitement en sautant les 3 premieres lignes
			if (n > 2 ) {
				// ligne vide trouvée=fin des data intéressantes
				if ( cols[0].equals("")){
					lastwell=true;
					continue;
				} else {
				    // description d'une ligne de donnees:A10_	A10	216.58	21.19	5.000	5.000	105.949	105.949
				    if (( cols.length  != 8 )) {
					    contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.linefields.unexpected",n );
					    n++;
					    continue; // ne pas sortir permet de verifier le fichier
				    } else {
				        String pos96=cols[1];
				        // verifier que c'est une position 96 valide ???
				        if ( !InputHelper.isPlatePosition(contextValidation, pos96 , 96, n)){
					          n++;
					         continue; // ne pas sortir permet de verifier le fichier
				        } else {
				             // Logger.info ("conc moyenne="+cols[7]);
				             double conc=Double.parseDouble(cols[7]);
				             // si la valeur trouv2ée est négative ????

				             SpectramaxData data=new SpectramaxData(conc);
				             dataMap.put(pos96, data);
				        }
				    }
		        } 
			}
		
			n++;
		} //end while

		reader.close();
		
		if (contextValidation.hasErrors()){ 
			return experiment;
		}
		
		// Verifier que tous les puits de l'experience ont des données dans le fichier  ???
		/* 
		if (!contextValidation.hasErrors()) {
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					String icupos=InputHelper.getCodePosition(icu.code);

				    if (!dataMap.containsKey(icupos) ){
						contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.concentration.missing",icupos);
					} 
				});
		}
		*/
		
		// ne positionner les valeurs que s'il n'y a pas d'erreur a la vérification precedente...
		if (!contextValidation.hasErrors()) {
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
		}
		
		return experiment;
	} // end import 
	
	// pas de size lue par le Spectramax...
	public class SpectramaxData {
		private double concentration;

		public SpectramaxData ( double conc) {
			concentration=conc;
		}
	}
}
