package experiments.rules;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import controllers.experiments.api.Experiments;
import experiments.ExperimentTestHelper;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.utils.instance.ExperimentHelper;
import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;

public class PoolTubeRulesTest extends AbstractTests {

	protected static ALogger logger=Logger.of("PoolTubeRulesTest");
	
	@Test
	public void validateExperimentPoolTube() {
		
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("pool-tube");
		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isFalse();		
	}
	
	@Test
	public void validateTransfertTubeCalculations() {
		Experiment exp = ExperimentTestHelper.getFakeExperiment();
		exp.state.code = "IP";
		exp.typeCode="pool-tube";
		ManytoOneContainer atomicTransfert = ExperimentTestHelper.getManytoOneContainer();
		
		ContainerUsed containerIn1 = ExperimentTestHelper.getContainerUsed("containerUsedIn1");
		containerIn1.percentage = 20.0;
		containerIn1.experimentProperties = null;
		
		ContainerUsed containerIn2 = ExperimentTestHelper.getContainerUsed("containerUsedIn2");
		containerIn2.percentage = 20.0;
		containerIn2.experimentProperties = null;
		
		ContainerUsed containerIn3 = ExperimentTestHelper.getContainerUsed("containerUsedIn3");
		containerIn3.percentage = 20.0;
		containerIn3.experimentProperties = null;
		
		ContainerUsed containerIn4 = ExperimentTestHelper.getContainerUsed("containerUsedIn4");
		containerIn4.percentage = 20.0;
		containerIn4.experimentProperties = null;
		
		ContainerUsed containerIn5 = ExperimentTestHelper.getContainerUsed("containerUsedIn5");
		containerIn5.percentage = 20.0;
		containerIn5.experimentProperties = null;
		
		ContainerUsed containerOut1 = ExperimentTestHelper.getContainerUsed("containerUsedOut1");
		containerOut1.volume = new PropertySingleValue(new Double(40.0));
		
		atomicTransfert.inputContainerUseds.add(containerIn1);
		atomicTransfert.inputContainerUseds.add(containerIn2);
		atomicTransfert.inputContainerUseds.add(containerIn3);
		atomicTransfert.inputContainerUseds.add(containerIn4);
		atomicTransfert.inputContainerUseds.add(containerIn5);
		atomicTransfert.outputContainerUsed = containerOut1;
		
		exp.atomicTransfertMethods.put(0, atomicTransfert);
		
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);

		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);

		ExperimentHelper.doCalculations(exp,Experiments.calculationsRules);
		
		ManytoOneContainer atomicTransfertResult = (ManytoOneContainer)exp.atomicTransfertMethods.get(0);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("inputVolume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("inputVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("inputVolume").value).isEqualTo(new Double(8.0));
		
		assertThat(atomicTransfertResult.inputContainerUseds.get(1).experimentProperties.get("inputVolume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(1).experimentProperties.get("inputVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(1).experimentProperties.get("inputVolume").value).isEqualTo(new Double(8.0));
		
		assertThat(atomicTransfertResult.inputContainerUseds.get(2).experimentProperties.get("inputVolume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(2).experimentProperties.get("inputVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(2).experimentProperties.get("inputVolume").value).isEqualTo(new Double(8.0));
		
		
		assertThat(atomicTransfertResult.outputContainerUsed.volume).isNotNull();
		assertThat(atomicTransfertResult.outputContainerUsed.volume.value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.outputContainerUsed.volume.value).isEqualTo(new Double(40.0));
		
		
	}
	
}
