package services.instance.run;

import java.sql.SQLException;

import javax.inject.Inject;

import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import services.instance.container.ContainerImportCNS;

import com.mongodb.MongoException;

import fr.cea.ig.play.NGLContext;

public class RunExtImportCNS extends AbstractImportDataCNS{

	@Inject
	public RunExtImportCNS(FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("RunExterieurCNS",durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		ContainerImportCNS.createContainers(contextError,"pl_PrepaflowcellExtToNGL","lane","F","prepa-flowcell",null);
		// RunImportCNS.createRuns("pl_RunExtToNGL",contextError);
		ctx.injector().instanceOf(RunImportCNS.class).createRuns("pl_RunExtToNGL",contextError);
	}
	
}
