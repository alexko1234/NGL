package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.common.instance.AbstractSample;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.AbstractSampleDAO;

public class AbstractSampleAPI extends GenericAPI<AbstractSampleDAO, AbstractSample> {
	
//	private final AbstractSampleDAO dao;
//	
//	@Inject
//	public AbstractSampleAPI (AbstractSampleDAO abstractSampleDAO) {
//		this.dao = abstractSampleDAO;
//	}

	@Inject
	public AbstractSampleAPI(AbstractSampleDAO dao) {
		super(dao);
	}
	
	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public AbstractSample dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public AbstractSample dao_getObject(String sampleCode) {
		return dao.getObject(sampleCode);
	}

	public void dao_saveObject(AbstractSample sampleElt) {
		dao.saveObject(sampleElt);
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
	public AbstractSample create(AbstractSample input, String currentUser)
			throws APIValidationException, APIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractSample update(AbstractSample input, String currentUser)
			throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractSample update(AbstractSample input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

}
