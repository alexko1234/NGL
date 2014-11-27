package experiments;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyImgValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.utils.InstanceConstants;

import org.junit.Test;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Logger.ALogger;
import play.data.validation.ValidationError;
import utils.AbstractTests;
import utils.Constants;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;
import controllers.experiments.api.Experiments;
import fr.cea.ig.MongoDBDAO;

public class ExperimentTests extends AbstractTests{
	
	protected static ALogger logger=Logger.of("ExperimentTest");

	
	@Test
	public void validatePropertiesFileImgErr() {
		Experiment exp = ExperimentTestHelper.getFakeExperiment();
		
		PropertyImgValue pImgValue = new  PropertyImgValue();
		byte[] data = new byte[] { (byte)0xe0, 0x4f, (byte)0xd0,
			    0x20, (byte)0xea, 0x3a, 0x69, 0x10, (byte)0xa2, (byte)0xd8, 0x08, 0x00, 0x2b,
			    0x30, 0x30, (byte)0x9d };
		pImgValue.value = data;
		pImgValue.fullname = "phylogeneticTree2.jpg";
		pImgValue.extension = "jpg";
		pImgValue.width = 250;
		pImgValue.height = 250;
		
		ContextValidation cv = new ContextValidation(Constants.TEST_USER); 
		cv.putObject("stateCode", "IP");
		
		PropertyDefinition pDef = getPropertyImgDefinition();
		
		Map<String, PropertyDefinition> hm= new HashMap<String, PropertyDefinition>();
		hm.put("restrictionEnzyme", pDef);
		
		cv.putObject("propertyDefinitions", hm.values());
		
		pImgValue.validate(cv);
		
		exp.instrumentProperties.put("enzymeChooser", pImgValue);
		
		showErrors(cv);
		
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		
		Experiment expBase = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, "TESTYANNEXP");
		
		assertThat(expBase.instrumentProperties.get("enzymeChooser").value);
		
		ExperimentValidationHelper.validateInstrumentUsed(exp.instrument,exp.instrumentProperties,cv);
		
		pImgValue.fullname = "test";
		
		expBase.instrumentProperties.clear();
		expBase.instrumentProperties.put("enzymeChooser", pImgValue);
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("code", expBase.code),
				DBUpdate.set("instrumentProperties",expBase.instrumentProperties));
		
		Experiment expBase2 = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, ExperimentTestHelper.EXP_CODE);
		
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, "TESTYANNEXP");
		
		assertThat(expBase2.instrumentProperties.get("enzymeChooser").value);
		
	}
	
	@Test
	public void validateFlowCellCalculations() {
		Experiment exp = ExperimentTestHelper.getFakeExperiment();
		exp.state.code = "IP";
		ManytoOneContainer atomicTransfert = ExperimentTestHelper.getManytoOneContainer();
		
		ContainerUsed containerIn1 = ExperimentTestHelper.getContainerUsed("containerUsedIn1");
		containerIn1.percentage = 50.0;
		containerIn1.concentration = new PropertySingleValue(new Integer(10)); 
		containerIn1.experimentProperties.put("NaOHVolume", new PropertySingleValue(new Double(1)));
		containerIn1.experimentProperties.put("NaOHConcentration", new PropertySingleValue(new Double(20)));
		containerIn1.experimentProperties.put("finalConcentration1", new PropertySingleValue(new Double(2)));
		containerIn1.experimentProperties.put("finalVolume1", new PropertySingleValue(new Double(20)));
		containerIn1.experimentProperties.put("phixConcentration", new PropertySingleValue(new Double(0.020)));
		containerIn1.experimentProperties.put("finalConcentration2", new PropertySingleValue(new Double(0.014)));
		containerIn1.experimentProperties.put("finalVolume2", new PropertySingleValue(new Double(1000)));
		
		ContainerUsed containerOut1 = ExperimentTestHelper.getContainerUsed("containerUsedOut1");
		containerOut1.experimentProperties.put("phixPercent", new PropertySingleValue(new Double(1)));
		containerOut1.experimentProperties.put("finalVolume", new PropertySingleValue(new Double(120)));
		
		atomicTransfert.inputContainerUseds.add(containerIn1);
		atomicTransfert.outputContainerUsed = containerOut1;
		
		exp.atomicTransfertMethods.put(0, atomicTransfert);
		
		ContextValidation contextValidation = new ContextValidation(Constants.TEST_USER);
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);
		
		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);
		
		Experiments.doCalculations(exp);
		
		ManytoOneContainer atomicTransfertResult = (ManytoOneContainer)exp.atomicTransfertMethods.get(0);		
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume1")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume1").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume1").value).isEqualTo(new Double(4));
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("NaOHConcentration")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("NaOHConcentration").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("NaOHConcentration").value).isEqualTo(new Double(20));
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("EBVolume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("EBVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("EBVolume").value).isEqualTo(new Double(15));
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume2")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume2").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume2").value).isEqualTo(new Double(7));
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("phixVolume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("phixVolume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("phixVolume").value).isEqualTo(new Double(7));
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("HT1Volume")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("HT1Volume").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("HT1Volume").value).isEqualTo(new Double(986)); 
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume3")).isNotNull();
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume3").value).isInstanceOf(Double.class);
		assertThat(atomicTransfertResult.inputContainerUseds.get(0).experimentProperties.get("requiredVolume3").value).isEqualTo(new Double(60)); 
		
	}
	
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
	
	/*@Test
	public void saveExperiment(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.save(),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateExperimentProperties(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateExperimentProperties(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateExperimentInformations(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateExperimentInformations(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateContainers(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateContainers(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateInstrumentInformations(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateInstrumentInformations(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateInstrumentProperties(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.updateInstrumentProperties(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void nextState(){
		Experiment exp = ExperimentTestHelper.getFakePrepFlowcell();
		
		Result result = callAction(controllers.experiments.api.routes.ref.Experiments.nextState(exp.code),fakeRequest().withJsonBody(Json.toJson(exp)));
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}*/
	
	public PropertyDefinition getPropertyImgDefinition() {
		PropertyDefinition pDef = new PropertyDefinition();
		pDef.code = "restrictionEnzyme";
		pDef.name = "restrictionEnzyme";		
		pDef.active = true;
		pDef.required = true;
		pDef.valueType = "File";
		//pDef.propertyType = "Img";
		return pDef;
	}
	
	private void showErrors(ContextValidation cv) {
		if(cv.errors.size() > 0){
			for(Entry<String, List<ValidationError>> e : cv.errors.entrySet()){
				System.out.println(e);
			}
		}
	}
}
