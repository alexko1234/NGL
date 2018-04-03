package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import models.sra.submit.sra.instance.Experiment;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.ExperimentDAO;

public class ExperimentAPI extends GenericAPI<ExperimentDAO, Experiment> {

	@Inject
	public ExperimentAPI(ExperimentDAO dao) {
		super(dao);
	}

//	private final ExperimentDAO dao;
//	
//	@Inject
//	public ExperimentAPI (ExperimentDAO experimentDAO) {
//		this.dao = experimentDAO;
//	}

	public Experiment dao_getObject(String experimentCode) {
		return dao.getObject(experimentCode);
	}
	
	public Experiment dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void dao_saveObject(Experiment experiment) {
		dao.saveObject(experiment);
	}

	public void dao_deleteByCode(String experimentCode) {
		dao.deleteByCode(experimentCode);
	}
	
	/*-------------------------------------------------------------------------------------------------*/

	@Override
	protected List<String> authorizedUpdateFields() {
		return null;
	}

	@Override
	protected List<String> defaultKeys() {
		return null;
	}

	@Override
	public Experiment update(Experiment input, String currentUser)
			throws APIException, APIValidationException {
		return null;
	}

	@Override
	public Experiment create(Experiment input, String currentUser)
			throws APIValidationException, APIException {
		return null;
	}

	@Override
	public Experiment update(Experiment input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		return null;
	}
	
}
