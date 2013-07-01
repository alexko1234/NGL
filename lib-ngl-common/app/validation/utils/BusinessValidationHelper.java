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
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
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
			Map<String, List<ValidationError>> errors, String typeCode, Class<T> type) {
		if(required(errors, typeCode, "typeCode")){
			 return new HelperObjects<T>().getObject(type, typeCode);
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
				new HelperObjects<T>().getObjects(type, list);
		}
	}

	
	public static <T> T validateRequiredDescriptionCode(Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find) {
		return validateRequiredDescriptionCode(errors, code, key, find, false);
	}
	
	/**
	 * Validate i a description code is not null and exist in description DB
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return
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
	 * @return
	 */
	public static <T> T validateExistDescriptionCode(
			Map<String, List<ValidationError>> errors, String code, String key,
			Finder<T> find) {
		return validateExistDescriptionCode(errors, code, key, find, false);
	}
	
	/***
	 * Validate if a code in a description table exist
	 * @param errors
	 * @param code
	 * @param key
	 * @param find
	 * @param returnObject
	 * @return
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
			}else if(null != code && !find.isCodeExist(code)){
				addErrors(errors, key, ERROR_NOTEXIST, code);
			}
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
		return o;
	}

	public static <T> T validateRequiredInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName) {
		T o = null;
		if(required(errors, code, key)){
			o = validateExistInstanceCode(errors, code, key, type,collectionName, false);
		}
		return o;	
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
	public static <T> T validateRequiredInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		T o = null;
		if(required(errors, code, key)){
			o = validateExistInstanceCode(errors, code, key, type,collectionName, returnObject);
		}
		return o;	
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
	public static <T> List<T> validateExistInstanceCodes(Map<String, List<ValidationError>> errors,
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
	public static <T> T validateExistInstanceCode(Map<String, List<ValidationError>> errors,
			String code, String key, Class<T> type, String collectionName, boolean returnObject) {
		T o = null;
		if(null != code && returnObject){
			o = MongoDBDAO.findByCode(collectionName, type, code);
			if(o == null){
				addErrors(errors, key, ERROR_NOTEXIST, code);
			}
		}
		/* TODO
		}else if(null != code && !MongoDBDAO.isCodeExist(collectionName, type, code)){
			addErrors(errors, key, ERROR_NOTEXIST, code);
		}
		*/
		return o;
	}	
}
