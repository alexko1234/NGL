package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class TubeImportCNS extends ContainerImportCNS {

	public TubeImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Tube CNS", durationFromStart, durationFromNextIteration);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_TubeToNGL ","tube","IW-P",null,null);
			//contextError.setUpdateMode();
	//		updateSampleFromTara();
	}
}
