package validation.common.instance;

import static validation.utils.ValidationHelper.required;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.resolutions.instance.ResolutionConfigurations;
import models.laboratory.sample.instance.Sample;
import models.laboratory.valuation.instance.ValuationCriteria;
import models.utils.InstanceConstants;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;

import org.drools.runtime.StatefulKnowledgeSession;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import rules.services.RulesException;
import rules.services.RulesServices;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

import com.typesafe.config.ConfigFactory;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class CommonValidationHelper {
	private static final String nameRules="validations";

	public static final String FIELD_CODE = "code";
	public static final String FIELD_TYPE_CODE = "typeCode";
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
	 * @param sampleCode
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
	 * @param sampleCode
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
		if(contextValidation.getContextObjects().containsKey(FIELD_TYPE_CODE)){
			String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
			validateStateCode(typeCode, stateCode, contextValidation);
		}else{
			validateRequiredDescriptionCode(contextValidation, stateCode,"code", models.laboratory.common.description.State.find);
		}
	}
	
	
	public static void validateState(String typeCode, State state, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, state, "state")){
			contextValidation.putObject(FIELD_TYPE_CODE, typeCode);
			contextValidation.addKeyToRootKeyName("state");
			state.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("state");
			contextValidation.removeObject(FIELD_TYPE_CODE);
		}		
	}
	
	public static void validateStateCode(String typeCode, String stateCode, ContextValidation contextValidation){
		try{
			if(required(contextValidation, stateCode, "code")){
				if(!models.laboratory.common.description.State.find.isCodeExistForTypeCode(stateCode, typeCode)){
					contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, stateCode);
				}
			}
		}catch(DAOException e){
			throw new RuntimeException(e);
		}
		
	}
	
	public static void validateResolutionCodes(List<String> resoCodes,ContextValidation contextValidation){
			String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
			validateResolutionCodes(typeCode, resoCodes, contextValidation);
	}
	
	public static void validateResolutionCodes(String typeCode, List<String> resoCodes, ContextValidation contextValidation){
		if(null != resoCodes){
			int i = 0;
			for(String resoCode: resoCodes){
				
				List<String> typeCodes = new ArrayList<String>();
				typeCodes.add(typeCode);
				
				if (! MongoDBDAO.checkObjectExist(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class, DBQuery.and(DBQuery.is("resolutions.code", resoCode), DBQuery.in("typeCodes", typeCodes)))) {
					contextValidation.addErrors("resolutionCodes["+i+"]", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, resoCode);
				}
				i++;
			}
		}
	}
	
	public static void validateCriteriaCode(String criteriaCode, ContextValidation contextValidation) {
			String typeCode = getObjectFromContext(FIELD_TYPE_CODE, String.class, contextValidation);
			validateCriteriaCode(typeCode, criteriaCode, contextValidation);		
	}

	public static void validateCriteriaCode(String typeCode, String criteriaCode, ContextValidation contextValidation) {
		if (null != criteriaCode) {
			Query q = DBQuery.and(DBQuery.is("code", criteriaCode), DBQuery.in("typeCodes", typeCode));
			if(!MongoDBDAO.checkObjectExist(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class, q)){
				contextValidation.addErrors("criteriaCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, criteriaCode);
			}
		}		
	}
	
	public static void validateValuation(String typeCode, Valuation valuation, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, valuation, "valuation")){
			contextValidation.putObject(FIELD_TYPE_CODE, typeCode);
			contextValidation.addKeyToRootKeyName("valuation");
			valuation.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("valuation");
			contextValidation.removeObject(FIELD_TYPE_CODE);
		}		
	}
	
	@SuppressWarnings("unchecked")
	protected static <T> T getObjectFromContext(String key, Class<T> clazz, ContextValidation contextValidation) {
		T o = (T) contextValidation.getObject(key);
		if(null == o){
			throw new IllegalArgumentException(key+" from contextValidation is null");
		}
		return o;
	}


	public static void validateProjectCodes(List<String> projectCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}
	
	public static void validateProjectCode(String projectCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, projectCode,"projectCode",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}


	public static void validateSampleCodes(List<String> sampleCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, sampleCodes,"sampleCodes",Sample.class,InstanceConstants.SAMPLE_COLL_NAME,false);
	}
	
	public static void validateSampleCode(String sampleCode, String projectCode, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, sampleCode, "sampleCode")) {
			Sample sample =  MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,  sampleCode);
			if (sample == null)  {
				contextValidation.addErrors("sampleCode", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, sampleCode);
			}
			else {
				if ((sample.projectCodes == null) || (!sample.projectCodes.contains(projectCode))) { 
					contextValidation.addErrors("projectCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, projectCode);
				}
			}
		}
	}


	public static void validateContainerCode(String containerCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, "containerCode", Container.class,InstanceConstants.CONTAINER_COLL_NAME);
	}
	
	public static void validationContainerSupportCode (String containerSupportCode, ContextValidation contextValidation) {		
		if (ValidationHelper.required(contextValidation, containerSupportCode, "containerSupportCode")) {
			if (! MongoDBDAO.checkObjectExist(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,  DBQuery.is("code", containerSupportCode))) {
				contextValidation.addErrors("containerSupportCode", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, containerSupportCode);
			}
		}		 
	}
	
	
	
	public static void validateCategoryCode(String categoryCode, ContextValidation contextValidation){
		try{
			if(required(contextValidation, categoryCode, "code")){
				if(!models.laboratory.common.description.ResolutionCategory.find.isCodeExist(categoryCode)){
					contextValidation.addErrors("resolutions.categoryCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, categoryCode);
				}
			}
		}catch(DAOException e){
			throw new RuntimeException(e);
		}
		
	}
	
	public static void validateRules(List<Object> objects,ContextValidation contextValidation){
		
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.addAll(objects);
		ContextValidation validationRules=new ContextValidation();
		facts.add(validationRules);
	
		RulesServices rulesServices = new RulesServices();
		StatefulKnowledgeSession kSession;
		List<Object> factsAfterRules;
		try {
			kSession = rulesServices.getKnowledgeBase().newStatefulKnowledgeSession();
			factsAfterRules = rulesServices.callRules(ConfigFactory.load().getString("rules.key"), nameRules, facts, kSession);
			kSession.dispose();
		} catch (RulesException e) {
			throw new RuntimeException();
		}
		
		for(Object obj:factsAfterRules){
			if(ContextValidation.class.isInstance(obj)){
				contextValidation.errors.putAll(((ContextValidation) obj).errors);
			}
		}

	}
	
	public static void validateRules(Object object,ContextValidation contextValidation){
		List<Object> list=new ArrayList<Object>();
		list.add(object);
		validateRules(list,contextValidation);
	}

}
