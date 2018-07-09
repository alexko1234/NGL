package fr.cea.ig.ngl.dao.api.sra;

import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate.Builder;

import fr.cea.ig.MongoDBResult;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import fr.cea.ig.ngl.dao.sra.SubmissionDAO;
import models.sra.submit.common.instance.Submission;


public class SubmissionAPI extends GenericAPI<SubmissionDAO, Submission> {

	@Inject
	public SubmissionAPI(SubmissionDAO dao) {
		super(dao);
	}

//	private final SubmissionDAO dao;
//	
//	@Inject
//	public SubmissionAPI (SubmissionDAO submissionDAO) {
//		this.dao = submissionDAO;
//	}

	public Submission dao_getObject(String submissionCode) {
		return dao.getObject(submissionCode);
	}
	
	public Submission dao_findOne(Query q) {
		return dao.findOne(q);		
	}

	public void dao_update(Query query, Builder set) {
		dao.update(query, set);
	}

	public boolean dao_checkObjectExist(String key, String keyValue) {
		return dao.checkObjectExist(key, keyValue);
	}

	public void dao_saveObject(Submission submission) {
		dao.saveObject(submission);
	}

	public void dao_deleteByCode(String submissionCode) {
		dao.deleteByCode(submissionCode);
	}
	
	public  MongoDBResult<Submission> dao_find(Query q) {
		return dao.dao_find(q);		
	}
	
	/*-------------------------------------------------------------------------------------------------*/

	@Override
	protected List<String> authorizedUpdateFields() {
		return null;
	}

	@Override
	protected List<String> defaultKeys() {
		return null;
	}

	@Override
	public Submission update(Submission input, String currentUser)
			throws APIException, APIValidationException {
		return null;
	}

	@Override
	public Submission create(Submission input, String currentUser)
			throws APIValidationException, APIException {
		return null;
	}

	@Override
	public Submission update(Submission input, String currentUser,
			List<String> fields) throws APIException, APIValidationException {
		return null;
	}

	public List<Submission> find(Query q) {
		return dao.findAsList(q);
	}

}
