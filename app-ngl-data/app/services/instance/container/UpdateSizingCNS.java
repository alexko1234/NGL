package services.instance.container;

import java.sql.SQLException;

import javax.inject.Inject;

import com.mongodb.MongoException;

import fr.cea.ig.play.NGLContext;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSizingCNS extends UpdateContainerImportCNS {

	@Inject
	public UpdateSizingCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("UpdateSizing", durationFromStart, durationFromNextIteration, ctx);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateContainer("pl_SizingToNGL @updated=1",contextError,"tube","sizing");

	}


}