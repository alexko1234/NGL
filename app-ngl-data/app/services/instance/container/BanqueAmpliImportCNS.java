package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

import com.mongodb.MongoException;

public class BanqueAmpliImportCNS extends ContainerImportCNS {

	public BanqueAmpliImportCNS( FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration) {
		super("Container Banque Amplifie", durationFromStart, durationFromNextIteration);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		createContainers(contextError,"pl_BanqueAmpliToNGL ","tube","IW-P","amplification","pl_ContentFromContainer @matmanom=?");

	}

}
