package validation.container.instance;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;

import play.modules.mongodb.jackson.MongoDB;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;
import models.laboratory.container.instance.ContainerSupport;

public class ContainerValidationHelper extends CommonValidationHelper{

	public static void validateContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	public static void validateProcessTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, typeCode,"processTypeCode", ProcessType.find);

	}

	public static void validateExperimentTypeCodes(
			List<String> experimentTypeCodes, ContextValidation contextValidation) {
		if(experimentTypeCodes!=null){
			for(String s: experimentTypeCodes){
				BusinessValidationHelper.validateExistDescriptionCode(contextValidation, s, "experimentTypeCode", ExperimentType.find);
			}
		}
	}

	public static void validateExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	public static void validateContents(List<Content> contents,
			ContextValidation contextValidation) {
		String rootKeyName=null;
		if(ValidationHelper.required(contextValidation, contents, "contents")){

			for(int i=0; i<contents.size();i++){
				rootKeyName="content"+"["+i+"]";
				contextValidation.addKeyToRootKeyName(rootKeyName);
				contents.get(i).validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName(rootKeyName);
			}
		}

	}

	public static void validateContainerSupport(LocationOnContainerSupport support,
			ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("containersupport");
		if(ValidationHelper.required(contextValidation, support, "containersupport")) {
			support.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("containersupport");
	}
	
	
	public static ContainerSupport createSupport(LocationOnContainerSupport containerSupport, List<String> newProjectCodes, List<String> newSampleCodes, PropertyValue tagCategory) {
		ContainerSupport s = null;
		if (containerSupport != null && containerSupport.supportCode != null) {

			s = new ContainerSupport();
			String user = "ngsrg"; //default value 

			s.code = containerSupport.supportCode;	
			s.categoryCode = containerSupport.categoryCode;
			
			s.state = new State(); 
			s.state.code = "A"; // default value
			s.state.user = user;
			s.state.date = new Date();
			
			s.traceInformation = new TraceInformation(); 
			s.traceInformation.setTraceInformation(user);
			s.valuation = new Valuation();
			
			//TODO: a verifier
			s.valuation.valid = TBoolean.UNSET;

			//s.projectCodes = InstanceHelpers.addCodesList(newProjectCodes, s.projectCodes);
			//s.sampleCodes = InstanceHelpers.addCodesList(newSampleCodes, s.sampleCodes);
			
			s.projectCodes = newProjectCodes;
			s.sampleCodes = newSampleCodes;		
			
			s.properties = new HashMap<String, PropertyValue>(); 
			s.properties.put("tagCategory", tagCategory);
					
		}
		return s;
	}
	


}
