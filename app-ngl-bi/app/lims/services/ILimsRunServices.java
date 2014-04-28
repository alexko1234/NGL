package lims.services;


import java.util.List;

import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;

/**
 * Common interface to extract data from CNS or CNG Lims
 * @author galbini
 *
 */
public interface ILimsRunServices {

	List<Instrument> getInstruments();
	
	Experiment getExperiments(Experiment experiment);
	
	ContainerSupport getContainerSupport(String supportCode);

	void valuationRun(Run run);
	
	void valuationReadSet(ReadSet readSet, boolean firstTime);
	
}
