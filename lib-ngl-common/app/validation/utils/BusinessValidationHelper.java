package validation.utils;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
import play.Logger;
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
	private static final String ERROR_NOTEXIST = "error.codenotexist";
	public static final String FIELD_TRACE_INFORMATION = "traceInformation";
	public static final String FIELD_CODE = "code";
	public static final String FIELD_TYPE_CODE = "typeCode";
	public static final String FIELD_SUPPORT_CODE = "containerSupportCode";


	/**
	 * Validate if code is unique in MongoDB collection
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return boolean
	 */
	
	public static <T extends DBObject> boolean validateUniqueInstanceCode(Map<String, List<ValidationError>> errors,
			String code, Class<T> type, String collectionName){
		
		if(null!=code && MongoDBDAO.checkObjectExistByCode(collectionName, type, code)){
			addErrors(errors, FIELD_CODE, ERROR_NOTUNIQUE, code);
			return false;
		}else if (code!=null){
			return false;
		}	
		
		return true;
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
	
	public static <T extends DBObject> boolean validateUniqueFieldValue(Map<String, List<ValidationError>> errors,
			String key, String keyValue, Class<T> type, String collectionName){
		
		if(null!=keyValue && MongoDBDAO.checkObjectExist(collectionName, type, key, keyValue)){
			addErrors(errors, key, ERROR_NOTUNIQUE, keyValue);
			return false;
		}else if (keyValue!=null){
			return false;
		}	
		
		return true;
	}
	
	
	public static <T> void validateRequiredDescriptionCode(Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find) {
		 validateRequiredDescriptionCode(errors, code, key, find,false);
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
	public static <T> T validateRequiredDescriptionCode(Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find, boolean returnObject) {
		T o = null;
		if(required(errors, code, key)){
			o = validateExistDescriptionCode(errors, code, key, find, returnObject);
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
			Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find) {
		 validateExistDescriptionCode(errors, code, key, find, false);
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
			Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find, boolean returnObject) {
		T o = null;
		try {
			if(null != code && returnObject){
				o = find.findByCode(code);
				if(o == null){
					addErrors(errors, key, ERROR_NOTEXIST, code);
				}
			}else if(null != code ){
				if( !find.isCodeExist(code))
				addErrors(errors, key, ERROR_NOTEXIST, code);
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return o;
	}

	public static <T extends DBObject> void validateRequiredInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName) {
		if(required(errors, code, key)){
			validateExistInstanceCode(errors, code, key, type,collectionName);
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
	public static <T extends DBObject> T validateRequiredInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		T o = null;
		if(required(errors, code, key)){
			o = validateExistInstanceCode(errors, code, key, type,collectionName, returnObject);
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
	public static <T extends DBObject> List<T> validateRequiredInstanceCodes(Map<String, List<ValidationError>> errors,
			List<String> codes, String key, Class<T> type, String collectionName, boolean returnObject) {

		List<T> l = null;
		
		if(required(errors, codes, key)){
			l=validateExistInstanceCodes(errors, codes, key, type, collectionName, returnObject);
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
	public static <T extends DBObject> List<T> validateExistInstanceCodes(Map<String, List<ValidationError>> errors,
			List<String> codes, String key, Class<T> type, String collectionName, boolean returnObject) {
		List<T> l = null;
		if(null != codes && codes.size() > 0){
			l = (returnObject)?new ArrayList<T>():null;

			for(String code: codes){
				T o =validateExistInstanceCode(errors, code, key, type, collectionName, returnObject) ;
				if(returnObject){
					l.add(o);
				}
			}			
		}
		return l;
	}

	
	
	public static <T extends DBObject> void validateExistInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName) {
		validateExistInstanceCode(errors, code, key, type, collectionName, false);
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
	public static <T extends DBObject> T validateExistInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		T o = null;

		if(null != code && returnObject){
			o =  MongoDBDAO.findByCode(collectionName, type, code);
			if(o == null){
				addErrors(errors, key, ERROR_NOTEXIST, code);
			}
		}else if(null != code && !MongoDBDAO.checkObjectExistByCode(collectionName, type, code)){
			addErrors(errors, key, ERROR_NOTEXIST, code);
		}

		return o;
	}	
}
