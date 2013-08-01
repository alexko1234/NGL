package validation;

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
import models.laboratory.reagent.description.ReagentType;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.InstanceConstants;
import models.utils.Model;
import models.utils.Model.Finder;
import validation.utils.BusinessValidationHelper;
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

	public static void validationReagentInstanceCode(
			String reagentInstanceCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation.errors, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
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
	
}
