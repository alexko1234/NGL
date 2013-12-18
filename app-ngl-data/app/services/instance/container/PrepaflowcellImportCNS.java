package services.instance.container;

import java.sql.SQLException;

import scala.concurrent.duration.FiniteDuration;

import models.utils.dao.DAOException;

import com.mongodb.MongoException;

public class PrepaflowcellImportCNS extends ContainerImportCNS {
	
	public PrepaflowcellImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Prepaflowcell CNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException {
		createContainers(contextError,"pl_PrepaflowcellToNGL","lane","F",null,"pl_BanquesolexaUneLane @nom_lane=?");
	}
	

}
