package services.instance.container;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSolutionStockCNS extends UpdateContainerImportCNS {

	public UpdateSolutionStockCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSolutionStock", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateContainer("pl_SolutionStockToNGL @updated=1",contextError,"tube","solution-stock");

	}


}
