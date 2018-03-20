package fr.cea.ig.ngl.dao.samples;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jongo.MongoCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;

@Singleton
public class SamplesAPI {

	private static final play.Logger.ALogger logger = play.Logger.of(SamplesAPI.class);
	
	public static final List<String> AUTHORIZED_UPDATE_FIELDS = Arrays.asList("comments");
	
	public static final List<String> DEFAULT_KEYS = Arrays.asList("code",
															     "typeCode",
															     "categoryCode",
															     "projectCodes",
															     "referenceCollab",
															     "properties",
															     "valuation",
															     "taxonCode",
															     "ncbiScientificName",
															     "comments",
															     "traceInformation");
	
	private final SamplesDAO dao;
	
	@Inject
	public SamplesAPI(SamplesDAO dao) {
		this.dao = dao;
	}

	public Sample get(String code) throws APIException {
		try {
			Sample sample = dao.findByCode(code);
			logger.debug("Sample with code " + code);
			return sample;
		} catch (DAOException e) {
			throw new APIException("Sample with code " + code + " does not exist", e.getCause());
		}
	} 
	
	public boolean isObjectExist(String code) {
		return dao.isObjectExist(code);
	}
	
	public Sample getObject(String code, BasicDBObject keys) {
		return dao.getObject(code, keys);
	}
	
	public List<Sample> list(Query query, String orderBy, Sort orderSense) {
		return dao.mongoDBFinder(query, orderBy, orderSense).toList();
	}
	
	public List<Sample> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return dao.mongoDBFinder(query, orderBy, orderSense, limit, keys).toList();
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return MongoStreamer.streamUDT(dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys));
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, limit, keys));
	}
	
	
	public Source<ByteString, ?> streamForReporting(String reportingQuery){
		return MongoStreamer.streamUDT(reportingData(reportingQuery));
	}
	
	public Integer countForReporting(String reportingQuery){
		return reportingData(reportingQuery).count();
	}
	
	public MongoCursor<Sample> reportingData(String reportingQuery) {
		return dao.findByQuery(reportingQuery);
	}
	
	public Sample create(Sample input, String currentUser) throws APIValidationException, APIException {
		ContextValidation ctxVal = new ContextValidation(currentUser);
		if (input._id == null) { 
			input.traceInformation = new TraceInformation();
			input.traceInformation.creationStamp(ctxVal, currentUser);
		} else {
			throw new APIException("create method does not update existing objects"); 
		}
		ctxVal.setCreationMode();
		SampleHelper.executeRules(input, "sampleCreation");
		input.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			return dao.saveObject(input);
		} else {
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		}
	}
	
	/**
	 * @param code
	 * @param input
	 * @param currentUser
	 * @param fields
	 * @return
	 * @throws APIException if the code doesn't correspond to a sample 
	 * @throws APIValidationException
	 */
	public Sample update(String code, Sample input, String currentUser, List<String> fields) throws APIException, APIValidationException {
		// throw an exception if no object is found
		Sample sampleInDb = get(code);
		ContextValidation ctxVal = new ContextValidation(currentUser);
		ctxVal.setUpdateMode();
		validateAuthorizedUpdateFields(ctxVal, fields);
		validateIfFieldsAreDefined(ctxVal, fields, input);
		if (!ctxVal.hasErrors()) {
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			TraceInformation ti = sampleInDb.traceInformation;
			ti.modificationStamp(ctxVal, currentUser);
			if(fields.contains("valuation")){
				input.valuation.user = currentUser;
				input.valuation.date = new Date();
			}
			if (!ctxVal.hasErrors()) {
				dao.updateObject(DBQuery.and(DBQuery.is("code", code)), dao.getBuilder(input, fields).set("traceInformation", ti));
				if(fields.contains("code") && input.code != null){
					code = input.code;
				}
				return dao.findByCode(code);
			} else {
				throw new APIValidationException("Invalid fields", ctxVal.getErrors());
			}
		} else {
			throw new APIValidationException("Invalid Sample object", ctxVal.getErrors());
		}
	}

	public Sample update(String code, Sample input, String currentUser) throws APIException, APIValidationException {
		// throw an exception if no object is found
		get(code);
		ContextValidation ctxVal = new ContextValidation(currentUser);
		if (code.equals(input.code)) {
			if(input.traceInformation != null){
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else{
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao.updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid Sample object", ctxVal.getErrors());
			}
		} else {
			throw new APIException("Sample codes are not the same");
		}
	}

	/**
	 * Validate authorized field for specific update field
	 * @param ctxVal
	 * @param fields
	 * @param authorizedUpdateFields
	 */
	protected void validateAuthorizedUpdateFields(ContextValidation ctxVal, List<String> fields) {
		for (String field: fields) {
			if (!AUTHORIZED_UPDATE_FIELDS.contains(field)) {
				ctxVal.addErrors("fields", "error.valuenotauthorized", field);
			}
		}				
	}
	
	/**
	 * Validate if the field is present 
	 * @param ctxVal
	 * @param fields
	 * @param filledForm
	 */
	protected void validateIfFieldsAreDefined(ContextValidation ctxVal, List<String> fields, Sample s) {
		for(String field: fields){
			try {
				if(s.getClass().getField(field).get(s) == null){
					ctxVal.addErrors(field, "error.notdefined");
				}
			} catch(Exception e){
				Logger.error(e.getMessage());
			}
		}	
		
	}
	
	public void delete(String code) {
		this.dao.deleteObject(code);
	}
	
}
