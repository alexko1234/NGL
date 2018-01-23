package fr.cea.ig.ngl.daoapi;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.ngl.dao.CodeLabelDAO;
import models.laboratory.common.description.CodeLabel;
import models.utils.dao.DAOException;

@Singleton
public class CodeLabelAPI {
	
	private final CodeLabelDAO dao;
	
	@Inject
	public CodeLabelAPI(CodeLabelDAO dao) {
		this.dao = dao;
	}
	
	public Iterable<CodeLabel> all() throws DAOException, APIException {
		return dao.all();
	}
	
}
