package fr.cea.ig.ngl.dao.samples;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import fr.cea.ig.ngl.dao.GenericMongoDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.modules.jongo.MongoDBPlugin;

@Singleton
public class SamplesDAO extends GenericMongoDAO<Sample> {
	@Inject
	public SamplesDAO() {
		super(InstanceConstants.SAMPLE_COLL_NAME, Sample.class);
	}


	@Override
	public Sample findByCode(String code) throws DAOException {
		return super.findOne(DBQuery.is("code", code));
	}
	
	
}
