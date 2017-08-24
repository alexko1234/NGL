package services.instance.container;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSizingCNS extends UpdateContainerImportCNS {

	public UpdateSizingCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSizing", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateContainer("pl_SizingToNGL @updated=1",contextError,"tube","sizing");

	}


}