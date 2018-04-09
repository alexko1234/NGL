package controllers.instruments.io.utils;

import java.util.function.Supplier;

import controllers.instruments.io.common.novaseq.OutputImplementationSwitch;
import fr.cea.ig.play.IGGlobals;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

public abstract class AbstractOutput {
	
	public static String impl(Supplier<String> oldImplementation, Supplier<String> newImplementation) {
		OutputImplementationSwitch s = IGGlobals.instanceOf(OutputImplementationSwitch.class);
		return s.get(oldImplementation, newImplementation);
	}
	
	public abstract File generateFile(Experiment experiment, ContextValidation contextValidation) throws Exception;
	
}
