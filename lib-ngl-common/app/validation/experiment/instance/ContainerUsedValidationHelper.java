package validation.experiment.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import static models.utils.InstanceConstants.*;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import static validation.utils.ValidationConstants.*;
import validation.utils.ValidationHelper;

public class ContainerUsedValidationHelper extends CommonValidationHelper {

	public static void compareInputContainerWithContainer(InputContainerUsed inputContainer,
			Container container, ContextValidation contextValidation) {
		
		if(!inputContainer.categoryCode.equals(container.categoryCode)){
			contextValidation.addErrors("categoryCode", "error.validationexp.inputContainer.categoryCode.notequals", inputContainer.code);
		}
		
		if(inputContainer.contents.size() != container.contents.size()){
			contextValidation.addErrors("categoryCode", "error.validationexp.inputContainer.contents.sizenotequals", inputContainer.code);
		}		
		//TODO improve comparison
	}
	
	public static void validateExperimentProperties(Map<String,PropertyValue> properties, Level.CODE level, ContextValidation contextValidation) {
		String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		
		ExperimentType exType= ExperimentType.find.findByCode(typeCode);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("experimentProperties");
			List<PropertyDefinition> propertyDefinitions=exType.getPropertyDefinitionByLevel(level);
			if("N".equals(stateCode)){
				ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false, false);				
			}else{
				ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false);	
			}
			contextValidation.removeKeyFromRootKeyName("experimentProperties");
		}
	}
	
	public static void validateInstrumentProperties(Map<String,PropertyValue> properties,  Level.CODE level, ContextValidation contextValidation) {
		InstrumentUsed instrument = getObjectFromContext(FIELD_INST_USED, InstrumentUsed.class, contextValidation);
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		
		InstrumentUsedType instType=InstrumentUsedType.find.findByCode(instrument.typeCode);
		if(instType!=null){
			contextValidation.addKeyToRootKeyName("instrumentProperties");
			List<PropertyDefinition> propertyDefinitions=instType.getPropertyDefinitionByLevel(level);
			if("N".equals(stateCode)){
				ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false, false);				
			}else{
				ValidationHelper.validateProperties(contextValidation, properties, propertyDefinitions, false);
				
			}
			contextValidation.removeKeyFromRootKeyName("instrumentProperties");
		}
	}

	public static void validatePercentage(Double percentage, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, percentage, "percentage")){
			if(percentage.doubleValue() > 100 && percentage.doubleValue() <= 0){
				contextValidation.addErrors("percentage", "error.validationexp.inputContainer.percentage");
			}
		}
		
	}

	public static void validateVolume(PropertyValue volume, ContextValidation contextValidation) {
		if(volume!=null && volume.value!=null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();		
			PropertyDefinition pd = new PropertyDefinition();			
			pd.code = "volume";
			pd.valueType = Double.class.getName();
			pd.propertyValueType = PropertyValue.singleType;
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			volume.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
	}


	public static void validateConcentration(PropertyValue concentration, ContextValidation contextValidation) {
		if(concentration!=null && concentration.value!=null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();
			PropertyDefinition pd = new PropertyDefinition();
			pd.code = "concentration";
			pd.valueType = Double.class.getName();
			pd.propertyValueType = PropertyValue.singleType;
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			concentration.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
		
	}
	
	public static void validateQuantity(PropertyValue quantity,	ContextValidation contextValidation) {
		if(quantity != null && quantity.value != null){
			Collection<PropertyDefinition> pdefs = new ArrayList<>();
			PropertyDefinition pd = new PropertyDefinition();
			pd.code = "quantity";
			pd.valueType = Double.class.getName();
			pd.propertyValueType = PropertyValue.singleType;
			pdefs.add(pd);
			contextValidation.putObject("propertyDefinitions", pdefs);
			quantity.validate(contextValidation);
			contextValidation.removeObject("propertyDefinitions");
		}
		
	}

	public static void validateOutputContainerCode(String code,	ContextValidation contextValidation) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(("N".equals(stateCode) && null != code) || "IP".equals(stateCode)){
			if(ValidationHelper.required(contextValidation, code, "code")){
				if(validateUniqueInstanceCode(contextValidation, code, Container.class, CONTAINER_COLL_NAME)){
					validateContainerNotUsedInOtherExperiment(contextValidation, code);
				}
				
			}
		}else if("F".equals(stateCode)){
			validateRequiredInstanceCode(code, "code",  Container.class, CONTAINER_COLL_NAME,contextValidation);			
		}
	}
	
	private static void validateContainerNotUsedInOtherExperiment(ContextValidation contextValidation, String containerCode) {
		Experiment exp = getObjectFromContext(FIELD_EXPERIMENT, Experiment.class, contextValidation);
		if(MongoDBDAO.checkObjectExist(EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.notEquals("code", exp.code).in("outputContainerCodes", containerCode))){
			contextValidation.addErrors("atomicTransfertMethods.outputContainerUseds.code", "error.validationexp.container.alreadyused",containerCode);
		}
		
	}
	
	private static void validateSupportContainerNotUsedInOtherExperiment(ContextValidation contextValidation, String supportContainerCode) {
		Experiment exp = getObjectFromContext(FIELD_EXPERIMENT, Experiment.class, contextValidation);
		if(MongoDBDAO.checkObjectExist(EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.notEquals("code", exp.code)
				.in("atomicTransfertMethods.outputContainerUseds.locationOnContainerSupport.code", supportContainerCode))){
			contextValidation.addErrors("code", "error.validationexp.support.alreadyused",supportContainerCode);
		}
		
	}

	public static void validateLocationOnSupportOnContainer(LocationOnContainerSupport locationOnContainerSupport, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, locationOnContainerSupport, "locationOnContainerSupport")){
			
			contextValidation.addKeyToRootKeyName("locationOnContainerSupport");
			InstrumentUsed instrument = getObjectFromContext(FIELD_INST_USED, InstrumentUsed.class, contextValidation);
			if(!instrument.outContainerSupportCategoryCode.equals(locationOnContainerSupport.categoryCode)){
				contextValidation.addErrors("categoryCode", ERROR_VALUENOTAUTHORIZED_MSG, locationOnContainerSupport.categoryCode);
			}
			
			String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
			if(("N".equals(stateCode) && null != locationOnContainerSupport.code) || "IP".equals(stateCode)){
				if(ValidationHelper.required(contextValidation, locationOnContainerSupport.code, "code")){
					if(validateUniqueInstanceCode(contextValidation, locationOnContainerSupport.code, ContainerSupport.class, CONTAINER_SUPPORT_COLL_NAME)){
						validateSupportContainerNotUsedInOtherExperiment(contextValidation, locationOnContainerSupport.code);
					}
					
				}
			}else if("F".equals(stateCode)){
				validateRequiredInstanceCode(locationOnContainerSupport.code, "code",  ContainerSupport.class, CONTAINER_SUPPORT_COLL_NAME,contextValidation);					
			}
			
			contextValidation.removeKeyFromRootKeyName("locationOnContainerSupport");
		
		}
		
	}
	
	
	public static void validateInputContainerCategoryCode(String categoryCode, ContextValidation contextValidation) {
		InstrumentUsed instrument = getObjectFromContext(FIELD_INST_USED, InstrumentUsed.class, contextValidation);
		if(ValidationHelper.required(contextValidation, categoryCode, "categoryCode") && null != instrument.inContainerSupportCategoryCode){
			ContainerCategory outputContainerCategory = ContainerCategory.find.findByContainerSupportCategoryCode(instrument.inContainerSupportCategoryCode);
			if(!categoryCode.equals(outputContainerCategory.code)){
				contextValidation.addErrors("categoryCode", ERROR_VALUENOTAUTHORIZED_MSG, categoryCode);
			}
		}
		
	}
	
	public static void validateOutputContainerCategoryCode(String categoryCode, ContextValidation contextValidation) {
		InstrumentUsed instrument = getObjectFromContext(FIELD_INST_USED, InstrumentUsed.class, contextValidation);
		if(ValidationHelper.required(contextValidation, categoryCode, "categoryCode") && null != instrument.outContainerSupportCategoryCode){
			ContainerCategory outputContainerCategory = ContainerCategory.find.findByContainerSupportCategoryCode(instrument.outContainerSupportCategoryCode);
			if(!categoryCode.equals(outputContainerCategory.code)){
				contextValidation.addErrors("categoryCode", ERROR_VALUENOTAUTHORIZED_MSG, categoryCode);
			}
		}
		
	}

	public static void validateOutputContents(List<Content> contents, ContextValidation contextValidation) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if("N".equals(stateCode) && null != contents){
			int i = 0;
			for(Content content: contents){
				contextValidation.addKeyToRootKeyName("contents["+i+"]");
				content.validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("contents["+i+++"]");
			}
		}else if(!"N".equals(stateCode)){
			if(ValidationHelper.required(contextValidation, contents, "contents")){
				int i = 0;
				for(Content content: contents){
					contextValidation.addKeyToRootKeyName("contents["+i+"]");
					content.validate(contextValidation);
					contextValidation.removeKeyFromRootKeyName("contents["+i+++"]");
				}
			}
		}
		
	}


	
	
}
