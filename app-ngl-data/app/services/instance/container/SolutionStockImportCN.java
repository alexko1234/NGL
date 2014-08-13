package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class SolutionStockImportCN extends ContainerImportCNS {

	public SolutionStockImportCN(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Solution stock CNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_SolutionStockToNGL ","tube","IW-P","solution-stock","pl_ContentFromContainer @matmanom=?");
	}
}
