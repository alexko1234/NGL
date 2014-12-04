package services.instance.experiment;

import java.sql.SQLException;
import java.util.List;

import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;

public class ExperimentImportCNG extends AbstractImportDataCNG{
	
	public ExperimentImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ExperimentImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {		
		Logger.debug("start loading");
		
		loadExperimentDepots();		
		//updateExperimentDepots();
		
		Logger.debug("end loading");			
	}
	
	
	
	public void loadExperimentDepots() throws SQLException, DAOException {
		Logger.debug("start loading experiments of type 'depôt'");
		
		List<Experiment> experiments = limsServices.findAllIlluminaDepotExperimentToCreate(contextError);
		List<Experiment> exps=InstanceHelpers.save(InstanceConstants.EXPERIMENT_COLL_NAME, experiments, contextError, true);
			
		limsServices.updateLimsDepotExperiment(exps, contextError, "creation");
	}

}
