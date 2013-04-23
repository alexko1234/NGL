package validation;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;
import static validation.utils.ConstraintsHelper.validateProperties;
import static validation.utils.ConstraintsHelper.validateTraceInformation;
import static validation.utils.ConstraintsHelper.addErrors;


import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

import models.laboratory.run.instance.Run;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;

import play.Logger;
import play.data.validation.ValidationError;

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
	
	
	/**
	 * Validate the code of a mongodb object
	 * Check is code is not null and unique
	 * 
	 * @param errors
	 * @param dbObject
	 * @param collectionName
	 */
	private static void validateCode(Map<String, List<ValidationError>> errors,
			DBObject dbObject, String collectionName, Class<?> type) {
		// validation of unique code
		if(required(errors, dbObject.code, "code")){
			try {
				DBObject o = (DBObject)MongoDBDAO.findByCode(collectionName, type, dbObject.code);
				if(null != o && !o._id.equals(dbObject._id)){
					addErrors(errors, FIELD_CODE, ERROR_NOTUNIQUE, dbObject.code);
				}
							
			} catch (MongoException e) {
				addErrors(errors, FIELD_CODE, ERROR_NOTUNIQUE, dbObject.code);
			}
		}		
	}
	
	public static void validateProcess(Map<String, List<ValidationError>> errors, Process process,  String collectionName, String rootKeyName){
		if(process == null){
			throw new IllegalArgumentException("process is null");
		}
		
		validateCode(errors, process, collectionName, Process.class);
		validateTraceInformation(errors, process.traceInformation, process._id);
		
		if(required(errors, process.containerInputCode, "containerInputCode")){
			//Check if the container exist in mongoDB
			Container container = MongoDBDAO.findByCode(CONTAINER_COLL_NAME, Container.class, process.containerInputCode);
			if(container == null){
				addErrors(errors,process.containerInputCode, getKey(rootKeyName,"containerInputCode"));
			} else {
				if(!container.stateCode.equals("IWP") && !container.stateCode.equals("N")){
					addErrors(errors,process.containerInputCode, getKey(rootKeyName,"containerNotIWP"));
				}
			}
		}
		
		required(errors, process.stateCode, "stateCode");
		required(errors, process.projectCode, "projectCode");
		required(errors, process.sampleCode, "sampleCode");
		required(errors, process.typeCode, "typeCode");
		
		ProcessType processType = process.getProcessType();
		
		if(processType != null && processType.propertiesDefinitions!=null){
			validateProperties(errors, process.properties, process.getProcessType().propertiesDefinitions, getKey(rootKeyName,"nullPropertiesDefinitions"));
		}
	}
}
