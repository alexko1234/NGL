package fr.cea.ig.ngl.dao.containers;

import java.util.ArrayList;
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
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.StorageHistory;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.container.ContSupportWorkflows;

public class ContainerSupportsAPI extends GenericAPI<ContainerSupportsDAO, ContainerSupport> {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainerSupportsAPI.class);
	
	private final static List<String> AUTHORIZED_UPDATE_FIELDS = Arrays.asList("storageCode");
	
	private final ContainersAPI containerApi;
	private final ContSupportWorkflows workflows;
	
	@Inject
	public ContainerSupportsAPI(ContainerSupportsDAO dao, ContainersAPI containerApi, ContSupportWorkflows workflows) {
		super(dao);
		this.containerApi = containerApi;
		this.workflows = workflows;
	}

	@Override
	protected List<String> authorizedUpdateFields() {
		return AUTHORIZED_UPDATE_FIELDS;
	}

	@Override
	protected List<String> defaultKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerSupport create(ContainerSupport input, String currentUser)
			throws APIValidationException, APIException {
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
	public ContainerSupport update(ContainerSupport input, String currentUser)
			throws APIException, APIValidationException {
		ContainerSupport supportInDb = get(input.code);
		if(supportInDb == null) {
			throw new APIException("ContainerSupport with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			if(input.traceInformation != null){
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else{
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao.updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid ContainerSupport object", ctxVal.getErrors());
			}
		}
	}

	@Override
	public ContainerSupport update(ContainerSupport input, String currentUser, List<String> fields)
			throws APIException, APIValidationException {
		ContainerSupport supportInDb = get(input.code);
		if(supportInDb == null) {
			throw new APIException("ContainerSupport with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			ctxVal.setUpdateMode();
			checkAuthorizedUpdateFields(ctxVal, fields);
			checkIfFieldsAreDefined(ctxVal, fields, input);
			if(!ctxVal.hasErrors()) {
				TraceInformation ti = supportInDb.traceInformation;
				if(ti != null){
					ti.modificationStamp(ctxVal, currentUser);
				} else{
					logger.error("traceInformation is null !!");
				}
				dao.updateObject(DBQuery.and(DBQuery.is("code", input.code)), dao.getBuilder(input, fields).set("traceInformation", ti));
				if(fields.contains("storageCode")) {
					containerApi.updateStorageCode(input.code, input.storageCode, ti);
					updateStorages(supportInDb, input, currentUser);
				}
				return get(input.code);
			} else {
				throw new APIValidationException("Invalid ContainerSupport object", ctxVal.getErrors());
			}
		}
	}
	
	public ContainerSupport updateState(String code, State state, String currentUser) throws APIException, APIValidationException {
//		logger.debug("ContainerSupportAPI updateState currentUser " + currentUser);
		ContainerSupport containerSupportInDb = get(code);
		if(containerSupportInDb == null) {
			throw new APIException("Container support with code " + code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser);
			ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "controllers");
			ctxVal.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_STATE, Boolean.TRUE);		
			workflows.setState(ctxVal, containerSupportInDb, state);
			if (!ctxVal.hasErrors()) {
				return get(code);
			} else {
				throw new APIValidationException("Invalid state modification", ctxVal.getErrors());
			}
		}
	} 
	
	
	private void updateStorages(ContainerSupport dbSupport, ContainerSupport formSupport, String currentUser) {
		if (dbSupport.storages == null) {
			dbSupport.storages = new ArrayList<>();
			if (dbSupport.storageCode != null) {
				StorageHistory sh = getStorageHistory(dbSupport.storageCode, dbSupport.storages.size(), currentUser);
				dbSupport.storages.add(sh);
			}
		}
		StorageHistory sh = getStorageHistory(formSupport.storageCode, dbSupport.storages.size(), currentUser);
		dbSupport.storages.add(sh);
		dao.updateObject(DBQuery.and(DBQuery.is("code", dbSupport.code)), dao.getBuilder(dbSupport, Arrays.asList("storages")));
//		MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, 
//				DBQuery.and(DBQuery.is("code", dbSupport.code)), 
//				DBUpdate.set("storages", dbSupport.storages));
	}

	private StorageHistory getStorageHistory(String storageCode, Integer index, String currentUser) {
		StorageHistory sh = new StorageHistory();
		sh.code  = storageCode;
		sh.date  = new Date();
		sh.user  = currentUser;
		sh.index = index;
		return sh;
	}
}
