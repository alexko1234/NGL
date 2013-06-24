package validation.utils;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import play.data.validation.ValidationError;

import com.mongodb.MongoException;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * Helper to validate MongoDB Object Used before insert or update a MongoDB
 * object
 * 
 * @author galbini
 * 
 */
public class BusinessValidationHelper {
	private static final String ERROR_NOTUNIQUE = "error.codenotunique";
	public static final String FIELD_TRACE_INFORMATION = "traceInformation";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_TYPE_CODE = "typeCode";
	public static final String FIELD_SUPPORT_CODE = "containerSupportCode";

	/**
	 * Validate the code of a mongodb object
	 * Check is code is not null and unique
	 * 
	 * @param errors
	 * @param dbObject
	 * @param collectionName
	 */
	public static void validateCode(Map<String, List<ValidationError>> errors,
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

	/** 
	 * Validate if mongodb or SGBD code type exists
	 * 
	 * @param errors
	 * @param typeCode
	 * @param Type class
	 * 
	 */
	public static <T> T validationType(
			Map<String, List<ValidationError>> errors, String typeCode,Class<T> type) {
		if(required(errors, typeCode, "typeCode")){
			 return new HelperObjects<T>().getObject(type, typeCode,errors);
		}
		return null;		
	}
	
	
	/** 
	 * Validate if mongodb or SGBD list codes from a type exist
	 * 
	 * @param errors
	 * @param typeCodes list
	 * @param Type class
	 * 
	 */
	public static <T> void  validationReferences(
			Map<String, List<ValidationError>> errors,List<String> list,Class<T> type) {		
		if(required(errors, list, "typeCode")){
				new HelperObjects<T>().getObjects(type, list,errors);
		}
	}
	
	

		
}
