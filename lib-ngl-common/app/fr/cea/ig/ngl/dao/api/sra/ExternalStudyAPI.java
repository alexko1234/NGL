package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import models.sra.submit.common.instance.ExternalStudy;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.ExternalStudyDAO;

import org.mongojack.DBQuery.Query;


public class ExternalStudyAPI extends GenericAPI<ExternalStudyDAO, ExternalStudy> {
	
	@Inject
	public ExternalStudyAPI(ExternalStudyDAO dao) {
		super(dao);
	}

//	private final ExternalStudyDAO dao;
//	
//	@Inject
//	public ExternalStudyAPI (ExternalStudyDAO externalStudyDAO) {
//		this.dao = externalStudyDAO;
//	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public ExternalStudy dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
		
	}
	
	/*-------------------------------------------------------------------------------------------------*/

	@Override
	protected List<String> authorizedUpdateFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> defaultKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalStudy create(ExternalStudy input, String currentUser)
			throws APIValidationException, APIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalStudy update(ExternalStudy input, String currentUser)
			throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalStudy update(ExternalStudy input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
