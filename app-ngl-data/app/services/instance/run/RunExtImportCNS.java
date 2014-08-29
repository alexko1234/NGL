package services.instance.run;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import services.instance.container.ContainerImportCNS;

import com.mongodb.MongoException;

public class RunExtImportCNS extends AbstractImportDataCNS{

	public RunExtImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("RunExterieurCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		ContainerImportCNS.createContainers(contextError,"pl_PrepaflowcellExtToNGL","lane","F","prepa-flowcell",null);
		RunImportCNS.createRuns("pl_RunExtToNGL",contextError);

	}
}
