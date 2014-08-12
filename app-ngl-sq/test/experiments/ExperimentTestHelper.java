package experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.instance.InstrumentUsed;

public class ExperimentTestHelper {
	public final static String EXP_CODE = "TESTYANNEXP";
	
	public static Experiment getFakeExperiment(){
		Experiment exp = new Experiment(EXP_CODE);
		exp.state = new State("N","ngsrg");
		exp.atomicTransfertMethods = new HashMap<Integer, AtomicTransfertMethod>();
		exp.instrument = new InstrumentUsed();
		exp.experimentProperties = new HashMap<String, PropertyValue>();
		exp.instrumentProperties = new HashMap<String, PropertyValue>();
		
		return exp;
		
	}
	
	public static ManytoOneContainer getManytoOneContainer(){
		ManytoOneContainer atomicTransfertMethod = new ManytoOneContainer();
		atomicTransfertMethod.inputContainerUseds = new ArrayList<ContainerUsed>();
		
		return atomicTransfertMethod;
	}
	
	public static ContainerUsed getContainerUsed(String code){
		ContainerUsed containerUsed = new ContainerUsed(code);
		containerUsed.experimentProperties =  new HashMap<String, PropertyValue>();
		containerUsed.instrumentProperties =  new HashMap<String, PropertyValue>();
		
		return containerUsed;
	}
}
