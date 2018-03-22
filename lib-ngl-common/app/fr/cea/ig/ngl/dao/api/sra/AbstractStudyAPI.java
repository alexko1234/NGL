package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.common.instance.AbstractStudy;
import fr.cea.ig.ngl.dao.sra.AbstractStudyDAO;

public class AbstractStudyAPI {
	
	private final AbstractStudyDAO dao;
	
	@Inject
	public AbstractStudyAPI (AbstractStudyDAO abstractStudyDAO) {
		this.dao = abstractStudyDAO;
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public AbstractStudy findOne(Query q) {
		return dao.findOne(q);		
	}

	public AbstractStudy getByCode(String studyCode) {
		return dao.findOne(DBQuery.is("code", studyCode));
	}

}
