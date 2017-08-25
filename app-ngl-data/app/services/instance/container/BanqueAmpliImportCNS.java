package services.instance.container;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class BanqueAmpliImportCNS extends ContainerImportCNS {

	public BanqueAmpliImportCNS( FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration) {
		super("Container Banque Amplifie", durationFromStart, durationFromNextIteration);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		createContainers(contextError,"pl_MaterielmanipToNGL @emnco=18 ","tube","IW-P","pcr-amplification-and-purification","pl_ContentFromContainer @matmanom=?");

	}

}
