package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Constants;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import workflows.container.ContainerWorkflows;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

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