package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import models.sra.submit.common.instance.ExternalStudy;
import fr.cea.ig.ngl.dao.sra.ExternalStudyDAO;

import org.mongojack.DBQuery.Query;


public class ExternalStudyAPI {
	
	private final ExternalStudyDAO dao;
	
	@Inject
	public ExternalStudyAPI (ExternalStudyDAO externalStudyDAO) {
		this.dao = externalStudyDAO;
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public ExternalStudy findOne(Query q) {
		return dao.findOne(q);		
	}

	public void deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
		
	}
	

}
