package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class SolutionStockImportCNS extends ContainerImportCNS {

	@Inject
	public SolutionStockImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Container Solution stock CNS", durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_MaterielmanipToNGL @emnco=14 ","tube","IW-P","solution-stock","pl_ContentFromContainer @matmanom=?");
	}
}
