package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import models.Constants;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import workflows.container.ContainerWorkflows;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

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
