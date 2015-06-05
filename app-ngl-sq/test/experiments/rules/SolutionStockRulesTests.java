package experiments.rules;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import controllers.experiments.api.Experiments;
import experiments.ExperimentTestHelper;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.utils.instance.ExperimentHelper;
import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;

public class SolutionStockRulesTests extends AbstractTests{
	
	protected static ALogger logger=Logger.of("SolutionStockRulesTests");
	
	@Test
	public void validateExperimentSolutionStock() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("solution-stock");
		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isFalse();

	}
	
	@Test
	public void validateTransfertTubeCalculations() {
		Experiment exp = ExperimentTestHelper.getFakeExperiment();
		exp.state.code = "IP";
		exp.typeCode="solution-stock";
		OneToOneContainer atomicTransfert = ExperimentTestHelper.getOnetoOneContainer();

		ContainerUsed containerIn1 = ExperimentTestHelper.getContainerUsed("containerUsedIn1");		
		containerIn1.concentration = new PropertySingleValue(new Double(19.23)); 
		containerIn1.volume = new PropertySingleValue(new Double(25.00)); 
		containerIn1.experimentProperties=null;
		
		ContainerUsed containerOut1 = ExperimentTestHelper.getContainerUsed("containerUsedOut1");
		containerOut1.experimentProperties.put("finalVolume", new PropertySingleValue(new Double(30.0)));	
		containerOut1.experimentProperties.put("finalConcentration", new PropertySingleValue(new Double(10.0)));
		containerOut1.experimentProperties.put("requiredVolume", new PropertySingleValue());
		containerOut1.experimentProperties.put("bufferVolume", new PropertySingleValue());

		atomicTransfert.inputContainerUsed = containerIn1;
		atomicTransfert.outputContainerUsed = containerOut1;

		exp.atomicTransfertMethods.put(0, atomicTransfert);

		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);

		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);

		ExperimentHelper.doCalculations(exp,Experiments.calculationsRules);

		OneToOneContainer atomicTransfertResult = (OneToOneContainer)exp.atomicTransfertMethods.get(0);		
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("requiredVolume")).isNotNull();		
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("requiredVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("requiredVolume").value).isEqualTo(new Double(15.60));
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("bufferVolume")).isNotNull();		
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("bufferVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUsed.experimentProperties.get("bufferVolume").value).isEqualTo(new Double(14.40));
			
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalVolume")).isNotNull();
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalVolume").value).isEqualTo(new Double(30.0));
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalConcentration")).isNotNull();
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalConcentration").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.outputContainerUsed.experimentProperties.get("finalConcentration").value).isEqualTo(new Double(10.0)); 
		

	}
	

}
