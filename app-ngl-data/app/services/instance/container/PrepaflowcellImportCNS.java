package services.instance.container;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class PrepaflowcellImportCNS extends ContainerImportCNS {
	
	public PrepaflowcellImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Prepaflowcell CNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException {
		createContainers(contextError,"pl_PrepaflowcellToNGL","lane","F","prepa-flowcell","pl_BanquesolexaUneLane @nom_lane=?");
	}
	

}
