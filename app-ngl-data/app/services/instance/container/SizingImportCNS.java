package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class SizingImportCNS extends ContainerImportCNS {


	public SizingImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Sizing CNS", durationFromStart, durationFromNextIteration);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createContainers(contextError,"pl_sinzingToNGL ","tube","IW-P","sizing","pl_ContentFromContainer @matmanom=?");
	}
}