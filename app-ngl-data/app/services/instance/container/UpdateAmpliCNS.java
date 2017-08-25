package services.instance.container;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateAmpliCNS extends UpdateContainerImportCNS {

	public UpdateAmpliCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateAmpli", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateContainer("pl_BanqueAmpliToNGL @updated=1",contextError,"tube","amplification");

	}

}