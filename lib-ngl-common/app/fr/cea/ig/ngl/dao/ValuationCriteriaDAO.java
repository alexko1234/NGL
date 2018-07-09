package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.laboratory.valuation.instance.ValuationCriteria;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

@Singleton
public class ValuationCriteriaDAO {

	private final GenericMongoDAO<ValuationCriteria> gdao;
	
	// Not needed, placeholder
	@Inject
	public ValuationCriteriaDAO() {
		gdao = new GenericMongoDAO<>(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class);
	}
	
	public Iterable<ValuationCriteria> all() throws DAOException {
		// return MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList();
		return gdao.all();
	}

}
