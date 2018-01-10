package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.NGLContext;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSolutionStockCNS extends UpdateContainerImportCNS {

	@Inject
	public UpdateSolutionStockCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("UpdateSolutionStock", durationFromStart, durationFromNextIteration, ctx);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateContainer("pl_SolutionStockToNGL @updated=1",contextError,"tube","solution-stock");

	}


}
