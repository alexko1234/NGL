package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.sra.submit.common.instance.AbstractStudy;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.AbstractStudyDAO;

public class AbstractStudyAPI extends GenericAPI<AbstractStudyDAO, AbstractStudy> {
	
	@Inject
	public AbstractStudyAPI(AbstractStudyDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

//	private final AbstractStudyDAO dao;
//	
//	@Inject
//	public AbstractStudyAPI (AbstractStudyDAO abstractStudyDAO) {
//		this.dao = abstractStudyDAO;
//	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public AbstractStudy dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public AbstractStudy dao_getObject(String studyCode) {
		return dao.getObject(studyCode);
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
	public AbstractStudy create(AbstractStudy input, String currentUser)
			throws APIValidationException, APIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractStudy update(AbstractStudy input, String currentUser)
			throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractStudy update(AbstractStudy input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void dao_saveObject(AbstractStudy absStudyElt) {
		dao.saveObject(absStudyElt);
	}
}
