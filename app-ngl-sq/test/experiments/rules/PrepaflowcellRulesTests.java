package experiments.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.instance.InstrumentUsed;

import org.junit.Test;

import play.Logger;
import play.Logger.ALogger;
import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;
import experiments.ExperimentTestHelper;

public class PrepaflowcellRulesTests extends AbstractTests {
	
	protected static ALogger logger=Logger.of("PrepaflowcellRulesTests");

	
	@Test
	public void validateExperimentPrepaflowcell() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");
		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isFalse();

	}

	@Test
	public void validateExperimentSameTagInPosition() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");
		Container container =new Container();
		Content content=new Content("CONTENT3", "TYPE", "CATEG");
		content.properties=new HashMap<String, PropertyValue>();
		content.properties.put("tag", new PropertySingleValue("IND1"));
		content.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container.contents.add(content);

		ContainerUsed containerUsed=new ContainerUsed(container);
		containerUsed.percentage= 0.0;
		exp.atomicTransfertMethods.get(0).getInputContainers().add(containerUsed);

		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.size()).isEqualTo(1);

	}


	@Test
	public void validateExperimentManyTagCategory() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");
		Container container =new Container();
		Content content=new Content("CONTENT3", "TYPE", "CATEG");
		content.properties=new HashMap<String, PropertyValue>();
		content.properties.put("tag", new PropertySingleValue("IND11"));
		content.properties.put("tagCategory", new PropertySingleValue("OTHERCATEGORIE"));
		container.contents.add(content);

		ContainerUsed containerUsed=new ContainerUsed(container);
		containerUsed.percentage= 0.0;
		exp.atomicTransfertMethods.get(0).getInputContainers().add(containerUsed);

		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.size()).isEqualTo(1);

	}

	@Test
	public void validateExperimentSumPercentInPutContainer() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");
		Container container =new Container();
		Content content=new Content("CONTENT3", "TYPE", "CATEG");
		content.properties=new HashMap<String, PropertyValue>();
		content.properties.put("tag", new PropertySingleValue("IND11"));
		content.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container.contents.add(content);

		ContainerUsed containerUsed=new ContainerUsed(container);
		containerUsed.percentage= 10.0;
		exp.atomicTransfertMethods.get(0).getInputContainers().add(containerUsed);

		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.size()).isEqualTo(1);

	}

	@Test
	public void validateExperimentPrepaflowcellLaneNotNull() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");
		exp.atomicTransfertMethods.get(0).getInputContainers().clear();
		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.size()).isEqualTo(1);

	}


	@Test
	public void validateExperimentDuplicateContainerInLane() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");

		ContainerUsed container1_1=ExperimentTestHelper.getContainerUsed("CONTAINER1_1");
		container1_1.percentage=0.0;
		Content content1_1=new Content("CONTENT1_1","TYPE","CATEGORIE");
		container1_1.contents=new ArrayList<Content>();
		content1_1.properties=new HashMap<String, PropertyValue>();
		content1_1.properties.put("tag", new PropertySingleValue("IND1"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		content1_1.properties.put("tag", new PropertySingleValue("IND2"));
		content1_1.properties.put("tagCategory", new PropertySingleValue("TAGCATEGORIE"));
		container1_1.contents.add(content1_1);

		exp.atomicTransfertMethods.get(0).getInputContainers().add(container1_1);

		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.size()).isEqualTo(1);

	}

	@Test
	public void validateExperimentPrepaflowcellInstrumentProperties() {
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		Experiment exp=ExperimentTestHelper.getFakeExperimentWithAtomicExperiment("prepa-flowcell");

		exp.instrument=new InstrumentUsed();
		exp.instrument.code="cBot Fluor A";
		exp.instrument.outContainerSupportCategoryCode="flowcell-1";
		exp.instrumentProperties=new HashMap<String, PropertyValue>();
		exp.instrumentProperties.put("control", new PropertySingleValue("3"));

		ExperimentValidationHelper.validateRules(exp, contextValidation);
		contextValidation.displayErrors(logger);
		assertThat(contextValidation.hasErrors()).isTrue();
		assertThat(contextValidation.errors.get("instrument").size()).isEqualTo(2);

	}

}