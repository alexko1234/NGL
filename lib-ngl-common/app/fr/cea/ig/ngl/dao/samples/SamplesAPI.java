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
import fr.cea.ig.ngl.dao.api.GenericAPI;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.SampleHelper;
import play.Logger;
import play.data.validation.ValidationError;
import validation.ContextValidation;

@Singleton
public class SamplesAPI extends GenericAPI<SamplesDAO, Sample> {

	private static final play.Logger.ALogger logger = play.Logger.of(SamplesAPI.class);
	
	/**
	 * These fields could be updated for Samples
	 */
	public static final List<String> AUTHORIZED_UPDATE_FIELDS = Arrays.asList("comments");
	
	/**
	 * Default key of Sample
	 */
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
	
	//private final SamplesDAO dao;
	
	@Inject
	public SamplesAPI(SamplesDAO dao) {
		//this.dao = dao;
		super(dao);
	}
	
	
	/* (non-Javadoc)
	 * @see fr.cea.ig.ngl.dao.api.GenericAPI#create(fr.cea.ig.DBObject, java.lang.String)
	 */
	@Override
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
			return dao().saveObject(input);
		} else {
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		}
	}
	
	/**
	 * define only some fields to update (not the entire object) <br>
	 * list of editable field list is defined in {@link #AUTHORIZED_UPDATE_FIELDS}  constant
	 * @see SamplesAPI#AUTHORIZED_UPDATE_FIELDS
	 * @param code
	 * @param input
	 * @param currentUser
	 * @param fields
	 * @return
	 * @throws APIException if the code doesn't correspond to a sample 
	 * @throws APIValidationException
	 */
	@Override
	public Sample update(Sample input, String currentUser, List<String> fields) throws APIException, APIValidationException {
		Sample sampleInDb = get(input.code);
		if(sampleInDb == null) {
			throw new APIException("Sample with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			ctxVal.setUpdateMode();
			ctxVal = checkAuthorizedUpdateFields(ctxVal, AUTHORIZED_UPDATE_FIELDS, fields);
			ctxVal = checkIfFieldsAreDefined(ctxVal, fields, input);
			if (!ctxVal.hasErrors()) {
				input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
				TraceInformation ti = sampleInDb.traceInformation;
				ti.modificationStamp(ctxVal, currentUser);
				if(fields.contains("valuation")){
					input.valuation.user = currentUser;
					input.valuation.date = new Date();
				}
				if (!ctxVal.hasErrors()) {
					dao().updateObject(DBQuery.and(DBQuery.is("code", input.code)), dao().getBuilder(input, fields).set("traceInformation", ti));
					return get(input.code);
				} else {
					throw new APIValidationException("Invalid fields", ctxVal.getErrors());
				}
			} else {
				throw new APIValidationException("Invalid Sample object", ctxVal.getErrors());
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.cea.ig.ngl.dao.api.GenericAPI#update(fr.cea.ig.DBObject, java.lang.String)
	 */
	@Override
	public Sample update(Sample input, String currentUser) throws APIException, APIValidationException {
		Sample sampleInDb = get(input.code);
		if(sampleInDb == null) {
			throw new APIException("Sample with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			if(input.traceInformation != null){
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else{
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao().updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid Sample object", ctxVal.getErrors());
			}
		}
	}	
}
