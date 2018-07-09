package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class SizingImportCNS extends ContainerImportCNS {


	@Inject
	public SizingImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Container Sizing CNS", durationFromStart, durationFromNextIteration, ctx);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createContainers(contextError,"pl_MaterielmanipToNGL @emnco=16 ","tube","IW-P","sizing","pl_ContentFromContainer @matmanom=?");
	}
}