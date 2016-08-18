package controllers.instruments.io.cns.labchipgx;

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
			
		
		String plateCodeInExp = experiment.inputContainerSupportCodes.iterator().next();
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		
		List<String[]> all = reader.readAll();
		Map<String, String[]> allMap = new HashMap<String, String[]>();
		
		all.forEach(array -> {
			allMap.put(plateCodeInExp+"_"+array[0], array);
		});
		reader.close();
		
		experiment.atomicTransfertMethods.forEach(atm ->{
			InputContainerUsed icu = atm.inputContainerUseds.get(0);
			if(allMap.containsKey(icu.code)){
				String[] data = allMap.get(icu.code);
				
				PropertySingleValue measuredSize = getPSV(icu, "measuredSize");
				measuredSize.value = Math.round(Double.valueOf(data[1]));
				measuredSize.unit="pb";
			}			
		});
				
		return experiment;
	}
	
	
}
