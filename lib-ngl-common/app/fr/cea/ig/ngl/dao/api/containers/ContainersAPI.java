package fr.cea.ig.ngl.dao.api.containers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceHelpers;
import play.Logger.ALogger;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;

public class ContainersAPI extends GenericAPI<ContainersDAO, Container> {

	private static final ALogger logger = play.Logger.of(ContainersAPI.class); 
	private static final List<String> AUTHORIZED_UPDATE_FIELDS = Arrays.asList("valuation",
																			   "state",
																			   "comments",
																			   "volume",
																			   "quantity",
																			   "size",
																			   "concentration");
	private static final List<String> DEFAULT_KEYS = Arrays.asList("code",
																   "importTypeCode",
																   "categoryCode",
																   "state",
																   "valuation",
																   "traceInformation",
																   "properties",
																   "comments",
																   "support",
																   "contents",
																   "volume",
																   "concentration",
																   "quantity",
																   "size",
																   "projectCodes",
																   "sampleCodes",
																   "fromTransformationTypeCodes",
																   "processTypeCodes");
	
	@Inject
	public ContainersAPI(ContainersDAO dao) {
		super(dao);
	}

	@Override
	protected List<String> authorizedUpdateFields() {
		return AUTHORIZED_UPDATE_FIELDS;
	}

	@Override
	protected List<String> defaultKeys() {
		return DEFAULT_KEYS;
	}

	@Override
	public Container create(Container input, String currentUser) throws APIValidationException, APIException {
		ContextValidation ctxVal = new ContextValidation(currentUser); 
		if (input._id == null) { 
			input.traceInformation = new TraceInformation();
			input.traceInformation.creationStamp(ctxVal, currentUser);
			
			if(null == input.state){
				input.state = new State();
			}
			input.state.code = "N";
			input.state.user = currentUser;
			input.state.date = new Date();		
			
		} else {
			throw new APIException("create method does not update existing objects"); 
		}
		ctxVal.setCreationMode();
		input.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			return dao.saveObject(input);
		} else {
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		}
	}

	@Override
	public Container update(Container input, String currentUser) throws APIException, APIValidationException {
		Container containerInDb = get(input.code);
		if(containerInDb == null) {
			throw new APIException("Container with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			if(input.traceInformation != null){
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else{
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			cleanProperties(input);
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao.updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid Container object", ctxVal.getErrors());
			}
		}
	}

	@Override
	public Container update(Container input, String currentUser, List<String> fields)
			throws APIException, APIValidationException {
		Container containerInDb = get(input.code);
		if(containerInDb == null) {
			throw new APIException("Container with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			ctxVal.setUpdateMode();
			checkAuthorizedUpdateFields(ctxVal, fields);
			checkIfFieldsAreDefined(ctxVal, fields, input);
			if (!ctxVal.hasErrors()) {
				input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
				TraceInformation ti = containerInDb.traceInformation;
				ti.modificationStamp(ctxVal, currentUser);
				if(fields.contains("valuation")){
					input.valuation.user = currentUser;
					input.valuation.date = new Date();
				}
				
				if (fields.contains("volume"))        ContainerValidationHelper.validateVolume(input.volume, ctxVal);					
				if (fields.contains("quantity"))	  ContainerValidationHelper.validateQuantity(input.quantity, ctxVal);
				if (fields.contains("size"))          ContainerValidationHelper.validateSize(input.size, ctxVal);
				if (fields.contains("concentration")) ContainerValidationHelper.validateConcentration(input.concentration, ctxVal);					

				
				if (!ctxVal.hasErrors()) {
					dao.updateObject(DBQuery.and(DBQuery.is("code", input.code)), dao.getBuilder(input, fields).set("traceInformation", ti));
					return get(input.code);
				} else {
					throw new APIValidationException("Invalid fields", ctxVal.getErrors());
				}
			} else {
				throw new APIValidationException("Invalid Container object", ctxVal.getErrors());
			}
		}
	}
	
	public void updateState(String code, State state, String currentUser) throws APIException {
		Container containerInDb = get(code);
		if(containerInDb == null) {
			throw new APIException("Container with code " + code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
		}
	}
	
	private void cleanProperties(Container input) {
		if(input.volume != null && input.volume.value == null) input.volume = null;
		if(input.concentration != null && input.concentration.value == null) input.concentration = null;
		if(input.size != null && input.size.value == null) input.size = null;
		if(input.quantity != null && input.quantity.value == null) input.quantity = null;
	}
	
	protected void updateStorageCode(String containerSupportCode, String storageCode, TraceInformation ti) {
		List<String> fields = Arrays.asList("support.storageCode");
		Container container = new Container();
		container.support = new LocationOnContainerSupport();
		container.support.storageCode = storageCode;
		dao.updateObject(DBQuery.and(DBQuery.is("support.code", containerSupportCode)), dao.getBuilder(container, fields).set("traceInformation", ti));
	}
}
