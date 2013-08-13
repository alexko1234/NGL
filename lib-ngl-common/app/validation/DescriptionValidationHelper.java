package validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.PropertyValue;
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
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;

public class DescriptionValidationHelper {
	
	public static void validationProtocol(String protocolCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation.errors, protocolCode, "protocolCode", Protocol.find);
	}

	public static void validationInstrumentUsedTypeCode(
			String instrumentUsedTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, instrumentUsedTypeCode, "instrumentUsedTypeCode", InstrumentUsedType.find);		
	}
	
	public static void validationExperimentTypeCode(
			String experimentTypeCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, experimentTypeCode, "experimentTypeCode", ExperimentType.find);		
	}

	public static void validationExperimentType(
			String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			ConstraintsHelper.validateProperties(contextValidation.errors, properties, exType.getPropertiesDefinitionDefaultLevel(), contextValidation.rootKeyName,true);
		}
	}
	
	public static void validationExperimentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "categoryCode", ExperimentCategory.find,false);
	}

	public static void validationInstrumentCode(String code,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, code, "instrument.code", Instrument.find);
		
	}

	public static void validationInstrumentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "instrument.categoryCode", InstrumentCategory.find);
	}

	public static void validationProcessTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode,"typeCode", ProcessType.find);

	}

	public static void validationProjectCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "categoryCode", ProjectCategory.find);

	}

	public static void validationReagentTypeCode(String reagentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, reagentTypeCode, "reagentTypeCode", ReagentType.find);		
	}

	public static void validationSampleCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "categoryCode", SampleCategory.find,false);

	}

	public static void validationContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	public static void validationContainerSupportCategoryCode(
			String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, categoryCode, "categoryCode", ContainerSupportCategory.find,false);

	}

	public static void validationSampleTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode",SampleType.find,false);
	}

	public static void validationProject(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProjectType projectType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode", ProjectType.find,true);
		if(projectType!=null){
		ConstraintsHelper.validateProperties(contextValidation, properties, projectType.getPropertiesDefinitionDefaultLevel());
		}
		
	}

	public static void validationProcess(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProcessType processType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode", ProcessType.find,true);
		if(processType!=null){
			ConstraintsHelper.validateProperties(contextValidation, properties, processType.getPropertiesDefinitionDefaultLevel());
		}
		
	}
	
	public static void validationInstrumentUsed(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode", InstrumentUsedType.find,true);
		if(instrumentUsedType!=null){
			ConstraintsHelper.validateProperties(contextValidation, properties, instrumentUsedType.getPropertiesDefinitionDefaultLevel());
		}
		
	}

	public static void validationSampleType(String typeCode,
			String importTypeCode, Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		List<PropertyDefinition> proDefinitions=new ArrayList<PropertyDefinition>();

		SampleType sampleType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, typeCode, "typeCode", SampleType.find,true);
		ImportType importType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, importTypeCode,"importTypeCode", ImportType.find,true);

		proDefinitions.addAll(sampleType.getPropertiesDefinitionDefaultLevel());
		proDefinitions.addAll(importType.getPropertiesDefinitionSampleLevel());
		
		ConstraintsHelper.validateProperties(contextValidation,properties, proDefinitions);

	}
	
	public static void validationStateCode(String stateCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation.errors, stateCode,"stateCode", State.find);
	}

	public static void validationResolutionCode(String resolutionCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation.errors, resolutionCode,"resolutionCode", Resolution.find);		
	}
	
}
