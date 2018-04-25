package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.migration.NGLContext;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class PrepaflowcellImportCNS extends ContainerImportCNS {
	
	@Inject
	public PrepaflowcellImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Container Prepaflowcell CNS", durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException {
		createContainers(contextError,"pl_PrepaflowcellToNGL","lane","F","prepa-flowcell","pl_BanquesolexaUneLane @nom_lane=?");
	}
	

}
