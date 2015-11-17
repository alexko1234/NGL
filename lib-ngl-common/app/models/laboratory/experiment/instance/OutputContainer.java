package models.laboratory.experiment.instance;

import java.util.Map;
import java.util.Set;








import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.InputContainerValidationHelper;

public class OutputContainer implements IValidation{
	
	public String code;
	public String categoryCode;
	public Set<Content> contents;
	public LocationOnContainerSupport locationOnContainerSupport;
	
	public PropertySingleValue volume;        //rename to volume
	public PropertySingleValue concentration; //rename to concentration
	public PropertySingleValue quantity; 	 //rename to quantity
	
	public Map<String,PropertyValue> experimentProperties;
	public Map<String,PropertyValue> instrumentProperties;
	
	
	@Override
	public void validate(ContextValidation contextValidation) {
		
		InputContainerValidationHelper.validateVolume(contextValidation, volume);
		InputContainerValidationHelper.validateConcentration(contextValidation, concentration);
		InputContainerValidationHelper.validateQuantity(contextValidation, quantity);
		
		InputContainerValidationHelper.validateExperimentProperties(contextValidation, experimentProperties, Level.CODE.ContainerIn);
		InputContainerValidationHelper.validateInstrumentProperties(contextValidation, instrumentProperties, Level.CODE.ContainerIn);
	}

	
}
