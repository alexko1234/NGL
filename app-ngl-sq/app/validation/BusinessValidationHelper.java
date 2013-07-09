package validation;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;
import static validation.utils.ConstraintsHelper.validateProperties;
import static validation.utils.ConstraintsHelper.validateTraceInformation;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.validation.ValidationError;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Helper to validate MongoDB Object Used before insert or update a MongoDB
 * object
 * 
 * @author ydeshayes
 * 
 */
public class BusinessValidationHelper {
	private static final String ERROR_NOTUNIQUE = "error.codenotunique";
	public static final String FIELD_CODE = "code";
	
	private static final String CONTAINER_COLL_NAME = "Container";
	
	
	
	public static void validateProcess(Map<String, List<ValidationError>> errors, Process process,  String collectionName, String rootKeyName){
		if(process == null){
			throw new IllegalArgumentException("Process is null");
		}
		
		if(process._id == null){
			validation.utils.BusinessValidationHelper.validateUniqueInstanceCode(errors, process.code, Process.class,InstanceConstants.CONTAINER_COLL_NAME);
		}
		
		validateTraceInformation(errors, process.traceInformation, process._id);
		
		if(process._id == null && required(errors, process.containerInputCode, "containerInputCode")){
			//Check if the container exist in mongoDB
			Container container = MongoDBDAO.findByCode(CONTAINER_COLL_NAME, Container.class, process.containerInputCode);
			if(container == null){
				addErrors(errors,process.containerInputCode, getKey(rootKeyName,"containerInputCode"));
			} else {
				if(!container.stateCode.equals("IWP") && !container.stateCode.equals("N")){
					addErrors(errors,process.containerInputCode, getKey(rootKeyName,"containerNotIWPOrN"));
				}
			}
		}
		
		required(errors, process.stateCode, "stateCode");
		required(errors, process.projectCode, "projectCode");
		required(errors, process.sampleCode, "sampleCode");
		required(errors, process.typeCode, "typeCode");
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, process.typeCode,"typeCode", ProcessType.find);
		
		ProcessType processType = process.getProcessType();
		if(processType != null && processType.propertiesDefinitions != null && !processType.propertiesDefinitions.isEmpty()){
			validateProperties(errors, process.properties, process.getProcessType().propertiesDefinitions, getKey(rootKeyName,"nullPropertiesDefinitions"));
		}
	}
	
	public static void validateExperiment(Map<String, List<ValidationError>> errors, Experiment experiment,  String collectionName, String rootKeyName){
		if(experiment == null){
			throw new IllegalArgumentException("Experiment is null");
		}
		
		if(experiment._id == null){
			validation.utils.BusinessValidationHelper.validateUniqueInstanceCode(errors, experiment.code, Experiment.class, collectionName);
		}
		
		validateTraceInformation(errors, experiment.traceInformation, experiment._id);
		
		required(errors, experiment.stateCode, "stateCode"); 
		
		if(experiment.stateCode.equals("N")) {
			required(errors, experiment.typeCode, "typeCode");
		} else if(experiment.stateCode.equals("IP")) {
			required(errors, experiment.typeCode, "typeCode"); 
			required(errors, experiment.resolutionCode, "resolutionCode");
			required(errors, experiment.protocolCode, "protocolCode");
			required(errors, experiment.instrument, "instrument");
		} else if(experiment.stateCode.equals("F")) {
			required(errors, experiment.typeCode, "typeCode"); 
			required(errors, experiment.resolutionCode, "resolutionCode");
			required(errors, experiment.protocolCode, "protocolCode");
			required(errors, experiment.instrument, "instrument");
			required(errors, experiment.listInputOutputContainers, "InputOutputContainer");
				
			validateProperties(errors, experiment.experimentProperties, experiment.getExperimentType().propertiesDefinitions, getKey(rootKeyName,"nullPropertiesDefinitions"));
			
		}else{
			addErrors(errors,experiment.stateCode, getKey(rootKeyName,"InvalidExperimentStateCode"));
		}	
		
		validation.utils.BusinessValidationHelper.validateRequiredDescriptionCode(errors, experiment.typeCode, "typeCode", ExperimentType.find);
	}
}
