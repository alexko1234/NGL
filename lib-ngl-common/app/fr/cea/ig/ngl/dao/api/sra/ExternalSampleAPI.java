package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery.Query;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.ExternalSampleDAO;
import models.sra.submit.common.instance.ExternalSample;


public class ExternalSampleAPI extends GenericAPI<ExternalSampleDAO, ExternalSample> {
	
//	private final ExternalSampleDAO dao;
//	
//	@Inject
//	public ExternalSampleAPI (ExternalSampleDAO externalSampleDAO) {
//		this.dao = externalSampleDAO;
//	}

	@Inject
	public ExternalSampleAPI(ExternalSampleDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public ExternalSample dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_deleteByCode(String studyCode) {
		dao.deleteByCode(studyCode);
	}

	public ExternalSample dao_getObject(String sampleCode) {
		return dao.getObject(sampleCode);
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
	public ExternalSample create(ExternalSample input, String currentUser)
			throws APIValidationException, APIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalSample update(ExternalSample input, String currentUser)
			throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExternalSample update(ExternalSample input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}	

}
