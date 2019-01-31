package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.migration.NGLContext;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class BanqueAmpliImportCNS extends ContainerImportCNS {

	@Inject
	public BanqueAmpliImportCNS( FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Container Banque Amplifie", durationFromStart, durationFromNextIteration, ctx);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		createContainers(contextError,"pl_MaterielmanipToNGL @emnco=18 ","tube","IW-P","pcr-amplification-and-purification","pl_ContentFromContainer @matmanom=?");

	}

}
