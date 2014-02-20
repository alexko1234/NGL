package lims.services;


import java.util.List;

import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;

/**
 * Common interface to extract data from CNS or CNG Lims
 * @author galbini
 *
 */
public interface ILimsRunServices {

	List<Instrument> getInstruments();
	
	Experiment getExperiments(Experiment experiment);
	
	ContainerSupport getContainerSupport(String supportCode);

	
	
}
