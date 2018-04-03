package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import models.sra.submit.common.instance.Study;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.StudyDAO;

public class StudyAPI extends GenericAPI<StudyDAO, Study> {
	
	@Inject
	public StudyAPI(StudyDAO dao) {
		super(dao);
	}

//	private final StudyDAO dao;
//	
//	@Inject
//	public StudyAPI (StudyDAO studyDAO) {
//		this.dao = studyDAO;
//	}

	public Study dao_getObject(String studyCode) {
		return dao.getObject(studyCode);
	}
	
	public Study dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void dao_saveObject(Study study) {
		dao.saveObject(study);
	}

	public void dao_deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
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
	public Study create(Study input, String currentUser)
			throws APIValidationException, APIException {
		return null;
	}

	@Override
	public Study update(Study input, String currentUser) throws APIException,
			APIValidationException {
		return null;
	}

	@Override
	public Study update(Study input, String currentUser, List<String> fields)
			throws APIException, APIValidationException {
		return null;
	}
	
}
