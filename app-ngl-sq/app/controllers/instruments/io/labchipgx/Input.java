package controllers.instruments.io.labchipgx;

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
	
   /* Description du fichier a traiter: TXT CSV généré parlabchipGX (NGL-979:25/04/2016 ne plus gerer la taille)
	*Well Label,Region[200-2000] Conc. (ng/ul)
	*A01,3.7401558465
	*A02...
	*   attention les valeurs [200-2000]  sont variables ne pas les prendre en compte pour la verification
	*   d'un entete correct
	*/
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		// hashMap  pour stocker les concentrations du fichier (NGL-979:25/04/2016 ne plus gerer la taille)
		Map<String,LabChipData> dataMap = new HashMap<String,LabChipData>(0);
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		int n = 0;
		String line;
		
		while ((line = reader.readLine()) != null) {	 
			/// attention si le fichier vient d'une machine avec LOCALE = FR les décimaux utilisent la virgule!!!????
			String[] fields = InputHelper.parseCSVLine (line);
			
			// verifier la premiere ligne d'entete
			if ((n == 0) && ( ! line.matches("Well Label(.*)") ) ){
				contextValidation.addErrors("Erreurs fichier","experiments.msg.import.header-label.missing","1", "Well Label");
				return experiment;
			}
			// commencer le traitement en sautant la 1ere ligne d'entete
			if (n > 0 ) {
				// description d'une ligne de donnees: (NGL-979:25/04/2016 ne plus gerer la taille)
				//A01,3.7401558465,551.4705882353,comment ( comments est optionnel)
				if ( fields.length  != 2 ) {
					contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.linefields.unexpected",n );
					n++;
					continue;
				}
			
				String pos384=fields[0];
				// verifier que c'est une position 384 valide ???
				if ( !InputHelper.isPlatePosition(contextValidation, pos384 , 384, n)){
					n++;
					continue;
				} else {
				  // position deja sur 3 caracteres pour le LabChipGX!!!
				  //String pos0384=InputHelper.add02pos(pos384);
				
				  // Attention en CSV les decimaux sont sous forme xxxx,yy si le fichier vient d'un machine avec LOCALE=FR...
				  //Logger.info ("conc="+fields[1]+" size="+fields[2]);
				  double concentration=Double.parseDouble(fields[1].replace(",","."));
				  LabChipData data=new LabChipData(concentration);
				
				  dataMap.put(pos384, data);
				}
		    } 
			n++;
		}

		reader.close();
		
		if (contextValidation.hasErrors()){ 
			return experiment;
		}
		
		// Verifier que tous les puits de l'experience ont des données dans le fichier => GA 18/04/2016 vu avec Julie pas utile
		/* 
		if (!contextValidation.hasErrors()) {
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					String icupos=InputHelper.getCodePosition(icu.code);
					// ajouter un "0" pour pouvoir comparer...
				    String icupos0=InputHelper.add02pos(icupos);

				    if (!dataMap.containsKey(icupos0) ){
						contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.concentration.missing",icupos0);
					} 
				});
		}
		*/
		// ne positionner les valeurs que s'il n'y a pas d'erreur a la verification precedente...
		if (!contextValidation.hasErrors()) {
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					String icupos=InputHelper.getCodePosition(icu.code);
					// ajouter un "0" pour pouvoir comparer...
				    String icupos0=InputHelper.add02pos(icupos);
						
					PropertySingleValue concentration1 = getPSV(icu, "concentration1");
					if(dataMap.containsKey(icupos0)){
						concentration1.value = dataMap.get(icupos0).concentration;
						concentration1.unit = "ng/µl";
					}
										
				});
		}
		
		return experiment;
	}
	
	/*NGL-979:25/04/2016: ne plus stocker la taille */
	public class LabChipData {
		private double concentration;
		
		public LabChipData ( double conc) {
			concentration=conc;
			
		}
	}

}
