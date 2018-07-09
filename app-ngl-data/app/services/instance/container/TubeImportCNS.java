package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class TubeImportCNS extends ContainerImportCNS {

	@Inject
	public TubeImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Container Tube CNS", durationFromStart, durationFromNextIteration, ctx);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_TubeToNGL ","tube","IW-P",null,null);
			//contextError.setUpdateMode();
	//		updateSampleFromTara();
	}
}
