package models.utils.instance;

import static fr.cea.ig.play.IGGlobals.configuration;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.ManyToOneContainer;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.utils.InstanceHelpers;
// import play.Play;
import rules.services.RulesServices6;

public class ExperimentHelper extends InstanceHelpers {

	public static List<InputContainerUsed> getAllInputContainers(Experiment expFromDB) {
		List<InputContainerUsed> containersUSed = new ArrayList<>(); // <InputContainerUsed>();
		if (expFromDB.atomicTransfertMethods != null) {
			for (int i=0;i<expFromDB.atomicTransfertMethods.size();i++) {
				if (expFromDB.atomicTransfertMethods.get(i) != null && expFromDB.atomicTransfertMethods.get(i).inputContainerUseds.size() > 0) {
					containersUSed.addAll(expFromDB.atomicTransfertMethods.get(i).inputContainerUseds);
				}
			}
		}
		return containersUSed;
	}
	
	public static void doCalculations(Experiment exp,String rulesName) {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(exp);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++) {
			AtomicTransfertMethod atomic = exp.atomicTransfertMethods.get(i);
			facts.add(atomic);
		}
//		List<Object> factsAfterRules = RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rulesName, facts);
		List<Object> factsAfterRules = RulesServices6.getInstance().callRulesWithGettingFacts(configuration().getString("rules.key"), rulesName, facts);
		for(Object obj:factsAfterRules) {
//			if (ManyToOneContainer.class.isInstance(obj)) {
			if (obj instanceof ManyToOneContainer) {
//				exp.atomicTransfertMethods.remove((ManyToOneContainer)obj);
				exp.atomicTransfertMethods.remove(obj);
				exp.atomicTransfertMethods.add((ManyToOneContainer) obj);
			}
		}	
	}

}
