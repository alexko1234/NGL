package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import models.sra.submit.common.instance.Study;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import fr.cea.ig.ngl.dao.sra.StudyDAO;

public class StudyAPI {
	
	private final StudyDAO dao;
	
	@Inject
	public StudyAPI (StudyDAO studyDAO) {
		this.dao = studyDAO;
	}

	public Study getByCode(String studyCode) {
		return dao.findOne(DBQuery.is("code", studyCode));
	}
	
	public Study findOne(Query q) {
		return dao.findOne(q);		
	}

	public void update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void save(Study study) {
		dao.saveObject(study);
	}

	public void deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
	}
	
}
