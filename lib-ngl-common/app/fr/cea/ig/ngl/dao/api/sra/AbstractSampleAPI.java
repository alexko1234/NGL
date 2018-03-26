package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.common.instance.AbstractSample;
import fr.cea.ig.ngl.dao.sra.AbstractSampleDAO;

public class AbstractSampleAPI {
	
	private final AbstractSampleDAO dao;
	
	@Inject
	public AbstractSampleAPI (AbstractSampleDAO abstractSampleDAO) {
		this.dao = abstractSampleDAO;
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public AbstractSample findOne(Query q) {
		return dao.findOne(q);		
	}

	public AbstractSample getByCode(String sampleCode) {
		return dao.findOne(DBQuery.is("code", sampleCode));
	}

	public void save(AbstractSample sampleElt) {
		dao.saveObject(sampleElt);
	}

}
