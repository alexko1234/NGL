package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import models.sra.submit.common.instance.ExternalSample;
import fr.cea.ig.ngl.dao.sra.ExternalSampleDAO;

import org.mongojack.DBQuery.Query;


public class ExternalSampleAPI {
	
	private final ExternalSampleDAO dao;
	
	@Inject
	public ExternalSampleAPI (ExternalSampleDAO externalSampleDAO) {
		this.dao = externalSampleDAO;
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public ExternalSample findOne(Query q) {
		return dao.findOne(q);		
	}

	public void deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
		
	}
	

}
