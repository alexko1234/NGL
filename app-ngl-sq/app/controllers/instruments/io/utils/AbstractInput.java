package controllers.instruments.io.utils;


import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

public abstract class AbstractInput {
	
	public abstract Experiment importFile(Experiment experiment, PropertyFileValue pfv, ContextValidation contextValidation) throws Exception;
}
