package validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.PropertyValue;
import java.util.List;

import java.util.List;
import java.util.Map;

import play.data.validation.ValidationError;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.reagent.description.ReagentType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import validation.utils.BusinessValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import validation.utils.ValidationHelper;

public class DescriptionValidationHelper {
	
	public static void validationProtocol(String protocolCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, protocolCode, "protocolCode", Protocol.find);
	}

	public static void validationInstrumentUsedTypeCode(
			String instrumentUsedTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, instrumentUsedTypeCode, "instrumentUsedTypeCode", InstrumentUsedType.find);		
	}
	
	public static void validationExperimentTypeCode(
			String experimentTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, experimentTypeCode, "experimentTypeCode", ExperimentType.find);		
	}

	public static void validationExperimentType(
			String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertiesDefinitionDefaultLevel(), true);
		}
	}
	
	public static void validationExperimentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ExperimentCategory.find,false);
	}

	public static void validationInstrumentCode(String code,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, code, "instrumentUsed.code", Instrument.find);
		
	}

	public static void validationInstrumentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "instrumentUsed.categoryCode", InstrumentCategory.find);
	}

	public static void validationProcessTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, typeCode,"processTypeCode", ProcessType.find);

	}

	public static void validationProjectCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ProjectCategory.find);

	}

	public static void validationReagentTypeCode(String reagentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, reagentTypeCode, "reagentTypeCode", ReagentType.find);		
	}

	public static void validationSampleCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", SampleCategory.find,false);

	}

	public static void validationContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	public static void validationContainerSupportCategoryCode(
			String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerSupportCategory.find,false);

	}

	public static void validationSampleTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode",SampleType.find,false);
	}

	public static void validationRunTypeCode(String typeCode,
			ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, typeCode, "typeCode")){
			if(!RunPropertyDefinitionHelper.getRunTypeCodes().contains(typeCode)){
				contextValidation.addErrors("typeCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, typeCode);
			}
			//TODO interroge la base de données
			
		}
		
	}

	public static void validationContainerSupportCode(
			String containerSupportCode, ContextValidation contextValidation) {		
		if(ValidationHelper.required(contextValidation, containerSupportCode, "containerSupportCode")){
			//TODO add controles si le container existe dans mongo db
			//BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, this.containerSupportCode, "containerSupportCode", ContainerSupportCode.find, false);
		}		 
	}
	

	public static void validationProject(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProjectType projectType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ProjectType.find,true);
		if(projectType!=null){
		ValidationHelper.validateProperties(contextValidation, properties, projectType.getPropertiesDefinitionDefaultLevel());
		}
		
	}

	public static void validationProcess(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProcessType processType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ProcessType.find,true);
		if(processType!=null){
			ValidationHelper.validateProperties(contextValidation, properties, processType.getPropertiesDefinitionDefaultLevel());
		}
		
	}
	
	public static void validationSampleType(String typeCode,
			String importTypeCode, Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		List<PropertyDefinition> proDefinitions=new ArrayList<PropertyDefinition>();

		SampleType sampleType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", SampleType.find,true);
		ImportType importType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, importTypeCode,"importTypeCode", ImportType.find,true);

		proDefinitions.addAll(sampleType.getPropertiesDefinitionDefaultLevel());
		proDefinitions.addAll(importType.getPropertiesDefinitionSampleLevel());
		
		ValidationHelper.validateProperties(contextValidation,properties, proDefinitions);

	}
	
	
	
	public static void validationStateCode(String stateCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, stateCode,"stateCode", State.find);
	}

	public static void validationResolutionCode(String resolutionCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, resolutionCode,"resolutionCode", Resolution.find);		
	}

	public static void validationReadSetTypeCode(String typeCode,
			ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, typeCode, "typeCode")){
			if(!"default-readset".equals(typeCode)){
				contextValidation.addErrors("typeCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, typeCode);
			}		
			
		}
		
	}
	
}
