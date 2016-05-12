package controllers.instruments.io.miseqqcmode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import play.Logger;
import validation.ContextValidation;
import au.com.bytecode.opencsv.CSVReader;
import controllers.instruments.io.utils.AbstractInput;

public class Input extends AbstractInput {
	
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		
		List<String[]> all = reader.readAll();
		Map<String, String[]> allMap = new HashMap<String, String[]>();
		
		all.forEach(array -> {
			Logger.debug(Arrays.asList(array).toString());
			allMap.put(array[2], array);
		});
		reader.close();
		
		experiment.atomicTransfertMethods.forEach(atm ->{
			InputContainerUsed icu = atm.inputContainerUseds.get(0);
			if(allMap.containsKey(icu.code)){
				String[] data = allMap.get(icu.code);
				
				PropertySingleValue clusterDensity = getPSV(icu, "clusterDensity");
				clusterDensity.value = Integer.parseInt(data[3]);
				
				PropertySingleValue measuredInsertSize = getPSV(icu, "measuredInsertSize");
				measuredInsertSize.value = Integer.parseInt(data[8]);
			}			
		});
				
		return experiment;
	}
	
	
}
