package validation.common.instance;

import static validation.utils.ValidationHelper.required;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.TraceInformation;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;

import validation.ContextValidation;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class CommonValidationHelper {
	public static final String FIELD_CODE = "code";
	
	/**
	 * Validate if code is unique in MongoDB collection
	 * Unique code is validate if key "_id" not in map contextObjects or if value of key "_id" is null else no code validation
	 * @param contextValidatin
	 * @param code
	 * @param type
	 * @return collctionName
	 */

	public static <T extends DBObject> boolean validateUniqueInstanceCode(ContextValidation contextValidation,
			String code, Class<T> type, String collectionName){
	
		if (null != code) {
			if (MongoDBDAO.checkObjectExistByCode(collectionName, type, code)) {
				contextValidation.addErrors(FIELD_CODE,	ValidationConstants.ERROR_CODE_NOTUNIQUE_MSG, code);
				return false;
			} else {
				return true;
			}
		} else {
			throw new IllegalArgumentException("code is null");
		}
		
		
	}
	
	
	/**
	 * Validate if field value is unique in MongoDB collection
	 * @param errors
	 * @param key : field name
	 * @param keyValue : field value
	 * @param type : type DBObject
	 * @param collectionName : Mongo collection name
	 * @param returnObject
	 * @return boolean
	 */
	
	public static <T extends DBObject> boolean validateUniqueFieldValue(ContextValidation contextValidation,
			String key, String keyValue, Class<T> type, String collectionName){
		if(null != key && null != keyValue){
			if(MongoDBDAO.checkObjectExist(collectionName, type, key, keyValue)){
				contextValidation.addErrors(key, ValidationConstants.ERROR_NOTUNIQUE_MSG, keyValue);
				return false;
			}else {
				return true;
			}
		} else {
			throw new IllegalArgumentException(key+" is null");
		}

	}
	
	
	public static <T> void validateRequiredDescriptionCode(ContextValidation contextValidation, String code, String key,
			Finder<T> find) {
		 validateRequiredDescriptionCode(contextValidation, code, key, find,false);
	}

	/**
	 * Validate i a description code is not null and exist in description DB
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return object de T or null if returnObject is false
	 */
	public static <T> T validateRequiredDescriptionCode(ContextValidation contextValidation, String code, String key,
			Finder<T> find, boolean returnObject) {
		T o = null;
		if(required(contextValidation, code, key)){
			o = validateExistDescriptionCode(contextValidation, code, key, find, returnObject);
		}
		return o;		
	}


	/***
	 * Validate if a code in a description table exist
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return void
	 */
	public static <T> void validateExistDescriptionCode(
			ContextValidation contextValidation, String code, String key,
			Finder<T> find) {
		 validateExistDescriptionCode(contextValidation, code, key, find, false);
	}

	/***
	 * Validate if a code in a description table exist
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return object de T or null if returnObject is false
	 */
	public static <T> T validateExistDescriptionCode(
			ContextValidation contextValidation, String code, String key,
			Finder<T> find, boolean returnObject) {
		T o = null;
		try {
			if(code != "" && null != code && returnObject){
				o = find.findByCode(code);
				if(o == null){
					contextValidation.addErrors(key, ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
				}
			}else if(code != "" && null != code && !find.isCodeExist(code)){
				contextValidation.addErrors(key, ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return o;
	}

	
	
	public static <T extends DBObject> void validateRequiredInstanceCode(String code, String key, Class<T> type, String collectionName,ContextValidation contextValidation) {
		if(required(contextValidation, code, key)){
			validateExistInstanceCode(contextValidation, code, key, type,collectionName);
		}
	}

	/**
	 * Validate if code is not null and exist
	 * @param errors
	 * @param code
	 * @param key
	 * @param type
	 * @param collectionName
	 * @param returnObject
	 * @return
	 */
	public static <T extends DBObject> T validateRequiredInstanceCode(ContextValidation contextValidation,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		T o = null;
		if(required(contextValidation, code, key)){
			o = validateExistInstanceCode(contextValidation, code, key, type,collectionName, returnObject);
		}
		return o;	
	}


	/**
	 * Validate if list is not null and code exist
	 * @param errors
	 * @param code
	 * @param key
	 * @param type
	 * @param collectionName
	 * @param returnObject
	 * @return
	 */
	public static <T extends DBObject> List<T> validateRequiredInstanceCodes(ContextValidation contextValidation,
			List<String> codes, String key, Class<T> type, String collectionName, boolean returnObject) {

		List<T> l = null;
		
		if(required(contextValidation, codes, key)){
			l = validateExistInstanceCodes(contextValidation, codes, key, type, collectionName, returnObject);
		}
		return l;
		
	}
	
	
	
	/**
	 * Validate a code of a MongoDB Collection
	 * @param errors
	 * @param code
	 * @param key
	 * @param type
	 * @param collectionName
	 * @param returnObject
	 * @return
	 */
	public static <T extends DBObject> List<T> validateExistInstanceCodes(ContextValidation contextValidation,
			List<String> codes, String key, Class<T> type, String collectionName, boolean returnObject) {
		List<T> l = null;
		if(null != codes && codes.size() > 0){
			l = (returnObject)?new ArrayList<T>():null;

			for(String code: codes){
				T o =validateExistInstanceCode(contextValidation, code, key, type, collectionName, returnObject) ;
				if(returnObject){
					l.add(o);
				}
			}			
		}
		return l;
	}

	
	
	public static <T extends DBObject> void validateExistInstanceCode(ContextValidation contextValidation,
			String code, String key, Class<T> type, String collectionName) {
		validateExistInstanceCode(contextValidation, code, key, type, collectionName, false);
	}
	
	/**
	 * Validate a code of a MongoDB Collection
	 * @param errors
	 * @param code
	 * @param key
	 * @param type
	 * @param collectionName
	 * @param returnObject
	 * @return
	 */
	public static <T extends DBObject> T validateExistInstanceCode(ContextValidation contextValidation,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		if(null != code && null != key){
			T o = null;
			if(returnObject){
				o =  MongoDBDAO.findByCode(collectionName, type, code);
				if(o == null){
					contextValidation.addErrors(key, ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
				}
			}else if(!MongoDBDAO.checkObjectExistByCode(collectionName, type, code)){
				contextValidation.addErrors( key, ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
			}

			return o;
		}else{
			throw new IllegalArgumentException("key or code is null : "+key+"/"+code);
		}
	}	
	
	
	/**
	 * Validate a code of a MongoDB Collection
	 * @param errors
	 * @param code
	 * @param type
	 * @param collectionName
	 * @return
	 */
	public static <T extends DBObject> void validateExistInstanceCode(ContextValidation contextValidation,
			String code, Class<T> type, String collectionName) {
		validateExistInstanceCode(contextValidation, code, type, collectionName, false);
	}
	
	/**
	 * Validate a code of a MongoDB Collection
	 * @param errors
	 * @param code
	 * @param type
	 * @param collectionName
	 * @param returnObject
	 * @return
	 */
	public static <T extends DBObject> T validateExistInstanceCode(ContextValidation contextValidation,
			String code, Class<T> type, String collectionName, boolean returnObject) {
		return validateExistInstanceCode(contextValidation, code, FIELD_CODE, type, collectionName, returnObject);
		
	}
	
	/**
	 * Validate the id of dbObject
	 * @param dbObject
	 * @param contextValidation
	 */
	public static void validateId(DBObject dbObject, ContextValidation contextValidation) {
		if(contextValidation.isUpdateMode()){
    		ValidationHelper.required(contextValidation, dbObject._id, "_id");
    	}else if(contextValidation.isCreationMode() && null != dbObject._id){
    		contextValidation.addErrors("_id", ValidationConstants.ERROR_ID_NOTNULL_MSG);
    	}
	}
	/**
	 * Validate the code of an dbObject. the code is the NGL identifier
	 * @param dbObject
	 * @param collectionName
	 * @param contextValidation
	 */
	public static void validateCode(DBObject dbObject, String collectionName, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, dbObject.code, "code")){
		    if (contextValidation.isCreationMode()) {
				validateUniqueInstanceCode(contextValidation, dbObject.code, dbObject.getClass(), collectionName);		
			}else if(contextValidation.isUpdateMode()){
				validateExistInstanceCode(contextValidation, dbObject.code, dbObject.getClass(), collectionName);
			}
		}
		
	}

	public static void validateTraceInformation(TraceInformation traceInformation, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, traceInformation, "traceInformation")){
			contextValidation.addKeyToRootKeyName("traceInformation");
			traceInformation.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("traceInformation");
		}		
	}
	
	public static void validateStateCode(String stateCode,ContextValidation contextValidation){
		validateRequiredDescriptionCode(contextValidation, stateCode,"stateCode", State.find);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromContext(String key, Class<T> clazz, ContextValidation contextValidation) {
		T o = (T) contextValidation.getObject(key);
		if(null == o){
			throw new RuntimeException(clazz.getName()+" form contextValidation is null");
		}
		return o;
	}
}
