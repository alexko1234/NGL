package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;
import javax.inject.Inject;
import models.sra.submit.common.instance.Sample;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.SampleDAO;

public class SampleAPI extends GenericAPI<SampleDAO, Sample> {

	@Inject
	public SampleAPI(SampleDAO dao) {
		super(dao);
	}

//	private final SampleDAO dao;
//	
//	@Inject
//	public SampleAPI (SampleDAO sampleDAO) {
//		this.dao = sampleDAO;
//	}

	public Sample dao_getObject(String sampleCode) {
		return dao.getObject(sampleCode);
	}
	
	public Sample dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void dao_saveObject(Sample sample) {
		dao.saveObject(sample);
	}

	public void dao_deleteByCode(String sampleCode) {
		dao.deleteByCode(sampleCode);
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

	// sauve dans la base si validation ok
	@Override
	public Sample create(Sample input, String currentUser)
			throws APIValidationException, APIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sample update(Sample input, String currentUser) throws APIException,
			APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sample update(Sample input, String currentUser, List<String> fields)
			throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
