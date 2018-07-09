package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

@Singleton
public class ResolutionConfigurationDAO {

	private final GenericMongoDAO<ResolutionConfiguration> gdao;
	
	@Inject
	public ResolutionConfigurationDAO() {
		gdao = new GenericMongoDAO<>(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class);
	}
	
	public Iterable<ResolutionConfiguration> all() throws DAOException {
		return gdao.all();
	}
	
}
