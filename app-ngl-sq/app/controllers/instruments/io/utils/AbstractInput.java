package controllers.instruments.io.utils;


import java.util.HashMap;

// import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import services.io.ExcelHelper;
import validation.ContextValidation;

public abstract class AbstractInput extends ExcelHelper {
	
	public abstract Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception;
	
	protected PropertySingleValue getPSV(InputContainerUsed icu, String code) {
		PropertySingleValue psv;
		if (icu.experimentProperties == null)
			icu.experimentProperties = new HashMap<>(); //<String,PropertyValue>(0);	
		if (!icu.experimentProperties.containsKey(code)) {
			psv = new PropertySingleValue();
			icu.experimentProperties.put(code, psv);
		} else {
			psv = (PropertySingleValue)icu.experimentProperties.get(code);
		}
		return psv;
	}
	
}
