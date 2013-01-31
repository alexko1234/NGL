package model.dao;


import java.util.List;

import junit.framework.Assert;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.laboratory.common.description.dao.PropertyDefinitionDAO;
import models.laboratory.common.description.dao.ResolutionDAO;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ReagentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.laboratory.experiment.description.dao.ReagentTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.description.dao.ProjectTypeDAO;

import org.junit.Test;

import play.modules.spring.Spring;
import play.test.FakeApplication;
import utils.AbstractTests;



public class SpringTest extends AbstractTests{

	FakeApplication app;
	private long idCommonInfoType=3;
	private String codeCommonInfoType="codeExp2";
	private long idObjectType=1;
	private long idState=1;
	private long idExpType=2;
	private long idProtocol=1;
	private long idInstrumentUsedType=1;
	private long idCommonInfoTypeWithoutMeasure=4;
	
	@Test
	public void testType()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType type = objectTypeDAO.find("Experiment");
		checkObjectType(type);
	}
	
	private void checkObjectType(ObjectType type)
	{
		Assert.assertNotNull(type);
		Assert.assertNotNull(type.id);
		Assert.assertNotNull(type.type);
		Assert.assertNotNull(type.generic);
	}
	
	@Test
	public void testTypeById()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType type = objectTypeDAO.findById(idObjectType);
		checkObjectType(type);
	}
	
	
	@Test
	public void testAllTypes()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		List<ObjectType> types = objectTypeDAO.findAll();
		Assert.assertNotNull(types.size()>0);
		for(ObjectType type : types){
			checkObjectType(type);
		}
	}
	
	
	
	
	
	@Test
	public void testCommonInfo()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.findById(idCommonInfoType);
		checkCommonInfo(cit);
	}
	
	@Test
	public void testCommonInfoAll()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		List<CommonInfoType> cits = commonInfoTypeDAO.findAll();
		Assert.assertTrue(cits.size()>0);
	}
	
	@Test
	public void testCommonInfoByCode()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = commonInfoTypeDAO.findByCode(codeCommonInfoType);
		checkCommonInfo(cit);
	}
	
	@Test
	public void testCommonInfoByName()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		List<CommonInfoType> cits = commonInfoTypeDAO.findByName("");
		for(CommonInfoType cit : cits)
		{
			System.out.println(cit.code);
		}
	}
	
	private void checkCommonInfo(CommonInfoType cit)
	{
		Assert.assertNotNull(cit);
		Assert.assertNotNull(cit.id);
		Assert.assertNotNull(cit.name);
		Assert.assertNotNull(cit.collectionName);
		Assert.assertNotNull(cit.objectType);
		Assert.assertNotNull(cit.objectType.id);
		Assert.assertNotNull(cit.variableStates);
		Assert.assertTrue(cit.variableStates.size()>0);
		for(State state : cit.variableStates)
		{
			Assert.assertNotNull(state.id);
		}
		Assert.assertNotNull(cit.resolutions);
		Assert.assertTrue(cit.resolutions.size()>0);
		for(Resolution resolution : cit.resolutions){
			Assert.assertNotNull(resolution.id);
		}
		Assert.assertNotNull(cit.getPropertiesDefinition());
		Assert.assertTrue(cit.getPropertiesDefinition().size()>0);
		for(PropertyDefinition prop : cit.getPropertiesDefinition()){
			Assert.assertNotNull(prop.id);
		}
	}
	
	@Test
	public void testProjectType()
	{
		ProjectTypeDAO projectTypeDAO = Spring.getBeanOfType(ProjectTypeDAO.class);
		ProjectType projectType = projectTypeDAO.findById(1);
		Assert.assertNotNull(projectType);
		Assert.assertNotNull(projectType.projectCategory);
	}
	
	@Test
	public void testStateByCommonInfo()
	{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		List<State> states = stateDAO.findByCommonInfoType(idCommonInfoType);
		Assert.assertNotNull(states);
		Assert.assertTrue(states.size()>0);
		for(State state : states){
			Assert.assertNotNull(state.id);
		}
	}
	
	@Test
	public void testStateAll()
	{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		List<State> states = stateDAO.findAll();
		Assert.assertNotNull(states);
		Assert.assertTrue(states.size()>0);
		for(State state : states){
			Assert.assertNotNull(state.id);
		}
	}
	
	@Test
	public void testStateById()
	{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		State state = stateDAO.findById(idState);
		Assert.assertNotNull(state);
		Assert.assertNotNull(state.id);
	}
	
	@Test
	public void testResolutionByCommonInfo()
	{
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		List<Resolution> resolutions = resolutionDAO.findByCommonInfoType(idCommonInfoType);
		Assert.assertNotNull(resolutions);
		Assert.assertTrue(resolutions.size()>0);
		for(Resolution resolution : resolutions){
			Assert.assertNotNull(resolution.id);
		}
	}
	
	@Test
	public void testPropertyDefinitionByCommonInfo()
	{
		PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		List<PropertyDefinition> propertyDefinitions = propertyDefinitionDAO.findByCommonInfoType(idCommonInfoType);
		Assert.assertNotNull(propertyDefinitions);
		Assert.assertTrue(propertyDefinitions.size()>0);
		for(PropertyDefinition prop : propertyDefinitions){
			Assert.assertNotNull(prop.id);
			Assert.assertNotNull(prop.code);
			Assert.assertNotNull(prop.name);
			Assert.assertNotNull(prop.description);
			Assert.assertNotNull(prop.type);
			Assert.assertNotNull(prop.active);
			Assert.assertNotNull(prop.level);
			Assert.assertNotNull(prop.measureCategory);
			Assert.assertNotNull(prop.measureValue);
		}
	}
	
	@Test
	public void testPropertyDefinitionWithoutMeasure()
	{
		PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		List<PropertyDefinition> propertyDefinitions = propertyDefinitionDAO.findByCommonInfoType(idCommonInfoTypeWithoutMeasure);
		Assert.assertNotNull(propertyDefinitions);
		Assert.assertTrue(propertyDefinitions.size()>0);
		for(PropertyDefinition prop : propertyDefinitions){
			Assert.assertNotNull(prop.id);
			Assert.assertNull(prop.measureCategory);
			Assert.assertNull(prop.measureValue);
		}
	}
	
	@Test
	public void testExperimentType()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType expType = experimentTypeDAO.findByCommonInfoType(idCommonInfoType);
		checkExperimentType(expType);
	}
	
	@Test
	public void testExperimentTypeById()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType expType = experimentTypeDAO.findById(idExpType);
		checkExperimentType(expType);
	}
	
	@Test
	public void testExperimentTypeByCode()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType expType = experimentTypeDAO.findByCode(codeCommonInfoType);
		checkExperimentType(expType);
	}
	
	@Test
	public void testExperimentTypeAll()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> expTypes = experimentTypeDAO.findAll();
		Assert.assertTrue(expTypes.size()>0);
	}
	@Test
	public void testNextExperimentTypes()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType experimentType = experimentTypeDAO.findByCommonInfoType(idCommonInfoType);
		List<ExperimentType> nextExpTypes = experimentTypeDAO.findNextExperiments(experimentType.id);
		Assert.assertNotNull(nextExpTypes);
		Assert.assertTrue(nextExpTypes.size()>0);
		for(ExperimentType expType : nextExpTypes){
			Assert.assertNotNull(expType);
			Assert.assertNotNull(expType.id);
			//Assert.assertNotNull(expType.commonInfoType);
			//Assert.assertNotNull(expType.commonInfoType.id);
		}
	}
	
	private void checkExperimentType(ExperimentType expType)
	{
		Assert.assertNotNull(expType);
		Assert.assertNotNull(expType.id);
		//Assert.assertNotNull(expType.commonInfoType);
		//Assert.assertNotNull(expType.commonInfoType.id);
		Assert.assertNotNull(expType.protocols);
		Assert.assertTrue(expType.protocols.size()>0);
		for(Protocol protocol : expType.protocols){
			Assert.assertNotNull(protocol.id);
		}
	}
	@Test
	public void testReagentType()
	{
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		List<ReagentType> reagentTypes = reagentTypeDAO.findByProtocol(idProtocol);
		Assert.assertNotNull(reagentTypes);
		Assert.assertTrue(reagentTypes.size()>0);
		for(ReagentType reagentType : reagentTypes){
			Assert.assertNotNull(reagentType.id);
		}
	}
	
	@Test
	public void testProtocolByExperiment()
	{
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		List<Protocol> protocols = protocolDAO.findByExperimentType(2);
		Assert.assertTrue(protocols.size()>0);
	}
	
	@Test
	public void testProtocol()
	{
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		Protocol protocol = protocolDAO.findById(1);
		Assert.assertNotNull(protocol);
		Assert.assertNotNull(protocol.protocolCategory);
	}
	@Test
	public void testInstrumentUsedType()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType experimentType = experimentTypeDAO.findByCommonInfoType(idCommonInfoType);
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByExperimentType(experimentType.id);
		Assert.assertNotNull(instrumentUsedTypes);
		Assert.assertTrue(instrumentUsedTypes.size()>0);
		for(InstrumentUsedType instrumentUsedType : instrumentUsedTypes){
			checkInstrumentUsedType(instrumentUsedType);
		}
		
	}
	
	@Test
	public void testInstrumentUsedTypeById()
	{
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		InstrumentUsedType instrumentUsedType = instrumentUsedTypeDAO.findById(idInstrumentUsedType);
		checkInstrumentUsedType(instrumentUsedType);
	}
	
	@Test
	public void testInstrumentUsedTypeAll()
	{
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findAll();
		Assert.assertTrue(instrumentUsedTypes.size()>0);
	}
	
	private void checkInstrumentUsedType(InstrumentUsedType instrumentUsedType)
	{
		Assert.assertNotNull(instrumentUsedType.id);
		//Assert.assertNotNull(instrumentUsedType.commonInfoType);
		Assert.assertNotNull(instrumentUsedType.instruments);
		Assert.assertNotNull(instrumentUsedType.instrumentCategory);
		Assert.assertTrue(instrumentUsedType.instruments.size()>0);
		for(Instrument instrument :instrumentUsedType.instruments){
			Assert.assertNotNull(instrument.id);
			Assert.assertNotNull(instrument.code);
			Assert.assertNotNull(instrument.name);
		}
	}
	@Test
	public void testInstrument()
	{
		InstrumentDAO instrumentDAO = Spring.getBeanOfType(InstrumentDAO.class);
		List<Instrument> instruments = instrumentDAO.findByInstrumentUsedType(idInstrumentUsedType);
		Assert.assertNotNull(instruments);
		Assert.assertTrue(instruments.size()>0);
		for(Instrument instrument :instruments){
			Assert.assertNotNull(instrument.id);
			Assert.assertNotNull(instrument.code);
			Assert.assertNotNull(instrument.name);
		}
	}
	
	@Test
	public void testInstrumentCategory()
	{
		InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
		InstrumentCategory instrumentCategory = instrumentCategoryDAO.findById(1);
		Assert.assertNotNull(instrumentCategory);
		Assert.assertNotNull(instrumentCategory.inContainerSupportCategories);
		Assert.assertTrue(instrumentCategory.inContainerSupportCategories.size()>0);
		Assert.assertNotNull(instrumentCategory.outContainerSupportCategories);
		Assert.assertTrue(instrumentCategory.outContainerSupportCategories.size()>0);
	}
	
	//@Test
	public void testCRUD()
	{
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		Protocol proto = protocolDAO.findById(1);
		protocolDAO.update(proto);
		
		/*CommonInfoTypeDAO citDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType cit = citDAO.find(3);
		citDAO.update(cit);*/
		ExperimentTypeDAO expDao = Spring.getBeanOfType(ExperimentTypeDAO.class);
		
		ExperimentType expType = expDao.findById(4);
		expDao.update(expType);
		/*PropertyDefinition propDef = new PropertyDefinition();
		propDef.setActive(true);
		propDef.setChoiceInList(true);
		propDef.setCode("codeP");
		propDef.setDefaultValue("defaultPU2");
		propDef.setDisplayFormat("displayPU2");
		propDef.setDisplayOrder(1);
		propDef.setName("namePU2");
		propDef.setRequired(true);
		propDef.setType("typePU");
		List<PropertyDefinition> propertiesDefinition = new ArrayList<PropertyDefinition>();
		propertiesDefinition.add(propDef);
		
		CommonInfoType cit = new CommonInfoType();
		cit.setCode("codeCP");
		cit.setCollectionName("collCP");
		cit.setName("nameCP");
		cit.setObjectType(Spring.getBeanOfType(ObjectTypeDAO.class).findById(1));
		cit.setId((long) 25);
		cit.setPropertiesDefinition(propertiesDefinition);
		CommonInfoTypeDAO citDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		citDAO.update(cit);*/
		/*List<Value> values = new ArrayList<Value>();
		Value value = new Value();
		value.setValue("valueV2");
		value.setDefaultValue(true);
		values.add(value);
		propDef.setPossibleValues(values);
		
		MeasureCategory measureCategory = new MeasureCategory();
		measureCategory.setName("nameCU2");
		measureCategory.setCode("codeC2");
		propDef.setMeasureCategory(measureCategory);
		
		MeasureValue measureValue = new MeasureValue();
		measureValue.setValue("valueMU2");
		measureValue.setDefaultValue(true);
		propDef.setMeasureValue(measureValue);
		
		PropertyDefinitionDAO propDao = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		propDao.update(propDef);*/
			
	}

}
