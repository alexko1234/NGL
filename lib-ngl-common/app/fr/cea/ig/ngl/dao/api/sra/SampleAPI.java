package fr.cea.ig.ngl.dao.api.sra;

import javax.inject.Inject;

import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.SampleDAO;

public class SampleAPI extends GenericAPI<SampleDAO, Sample>{
	
	private final SampleDAO dao;
	
	@Inject
	public SampleAPI (SampleDAO sampleDAO) {
		this.dao = sampleDAO;
	}

	public Sample getByCode(String sampleCode) {
		return dao.findOne(DBQuery.is("code", sampleCode));
	}
	
	public Sample findOne(Query q) {
		return dao.findOne(q);		
	}

	public void update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void save(Sample sample) {
		dao.saveObject(sample);
	}

	public void deleteByCode(String sampleCode) {
		dao.deleteByCode(sampleCode);
	}
	
}
