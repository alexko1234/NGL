package model.dao;


import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.Value;
import models.laboratory.common.description.dao.MeasureCategoryDAO;
import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.laboratory.common.description.dao.ResolutionDAO;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.laboratory.experiment.description.AbstractExperiment;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.experiment.description.ReagentType;
import models.laboratory.experiment.description.dao.ExperimentCategoryDAO;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.experiment.description.dao.ProtocolCategoryDAO;
import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.laboratory.experiment.description.dao.PurificationMethodTypeDAO;
import models.laboratory.experiment.description.dao.QualityControlTypeDAO;
import models.laboratory.experiment.description.dao.ReagentTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;
import models.laboratory.processus.description.dao.ProcessCategoryDAO;
import models.laboratory.processus.description.dao.ProcessTypeDAO;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.description.dao.ProjectCategoryDAO;
import models.laboratory.project.description.dao.ProjectTypeDAO;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.description.dao.SampleCategoryDAO;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import models.utils.ExecuteSQLDAO;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import play.modules.spring.Spring;
import play.test.FakeApplication;
import utils.AbstractTests;


/**
 * Test sur base vide avec dump.sql
 * @author ejacoby
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//TODO erreur classpath
//@ContextConfiguration(locations = { "classpath:/test/application-context.xml" })
public class SpringTest extends AbstractTests{

	FakeApplication app;

	
	
	
	@Test
	public void initializeDB()
	{
		ExecuteSQLDAO initializeDatabaseDAO = Spring.getBeanOfType(ExecuteSQLDAO.class);
		Resource resource = new ClassPathResource("/dump30012013.sql");
		initializeDatabaseDAO.executeScript(resource);
	}
	
	/**
	 * TEST OBJECT_TYPE
	 */
	@Test
	public void addObjectType()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = new ObjectType();
		objectType.type="Test";
		objectType.generic=false;
		ObjectType objectTypeDB = objectTypeDAO.add(objectType);
		checkObjectType(objectTypeDB);
		objectTypeDB = objectTypeDAO.findById(objectTypeDB.id);
		Assert.assertTrue(objectType.type.equals(objectTypeDB.type));
		Assert.assertTrue(objectType.generic.equals(objectTypeDB.generic));
	}

	@Test
	public void updateObjectType()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Test");
		objectType.type="UpdateTest";
		objectTypeDAO.update(objectType);
		objectType = objectTypeDAO.findById(objectType.id);
		Assert.assertTrue(objectType.type.equals("UpdateTest"));
		checkObjectType(objectType);

	}
	private void checkObjectType(ObjectType type)
	{
		Assert.assertNotNull(type);
		Assert.assertNotNull(type.id);
		Assert.assertNotNull(type.type);
		Assert.assertNotNull(type.generic);
	}

	@Test
	public void testType()
	{
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType type = objectTypeDAO.find("Experiment");
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


	/**
	 * TEST RESOLUTION
	 */
	@Test
	public void addResolution()
	{
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		Resolution resolution = createResolution("Resol1", "Resol1");
		Resolution resolutionDB = resolutionDAO.add(resolution);
		resolutionDB=resolutionDAO.findById(resolutionDB.id);
		checkResolution(resolutionDB);
	}

	@Test
	public void updateResolution()
	{
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		Resolution resolution = resolutionDAO.findByCode("Resol1");
		checkResolution(resolution);
		resolution.name="updateResol1";
		resolutionDAO.update(resolution);
		resolution = resolutionDAO.findById(resolution.id);
		checkResolution(resolution);
		Assert.assertTrue(resolution.name.equals("updateResol1"));

	}

	private void checkResolution(Resolution resolution)
	{
		Assert.assertNotNull(resolution);
		Assert.assertNotNull(resolution.id);
		Assert.assertNotNull(resolution.name);
		Assert.assertNotNull(resolution.code);
	}

	private Resolution createResolution(String code, String name)
	{
		Resolution resolution = new Resolution();
		resolution.code=code;
		resolution.name=name;
		return resolution;
	}

	/**
	 * TEST STATE
	 */
	@Test
	public void addState()
	{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		State state = createState("state1", "state1", 1, true);
		State stateDB = stateDAO.add(state);
		stateDB=stateDAO.findById(stateDB.id);
		checkState(stateDB);
		Assert.assertTrue(stateDB.code.equals(state.code));
	}

	//@Test
	public void updateState()
	{
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		State state = stateDAO.findByCode("state1");
		checkState(state);
		Assert.assertTrue(state.code.equals("state1"));
		state.name="updateState1";
		stateDAO.update(state);
		state = stateDAO.findById(state.id);
		checkState(state);
		Assert.assertTrue(state.name.equals("updateState1"));
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

	private State createState(String code, String name, Integer priority, boolean active)
	{
		State state = new State();
		state.code=code;
		state.name=name;
		state.priority=priority;
		state.active=active;
		return state;
	}

	private void checkState(State state)
	{
		Assert.assertNotNull(state);
		Assert.assertNotNull(state.id);
		Assert.assertNotNull(state.code);
		Assert.assertNotNull(state.name);
		Assert.assertNotNull(state.active);
		Assert.assertNotNull(state.priority);
	}

	/**
	 * TEST MEASURE_CATEGORY
	 */
	@Test
	public void addMeasureCategory()
	{
		MeasureCategory measureCategory = createMeasureCategory("cat1", "cat1");
		List<MeasureValue> measureValues = new ArrayList<MeasureValue>();
		measureValues.add(createMeasureValue("value1", true,measureCategory));
		measureValues.add(createMeasureValue("value2", false,measureCategory));
		measureCategory.measurePossibleValues=measureValues;
		MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
		measureCategory = measureCategoryDAO.add(measureCategory);
		checkMeasureCategory(measureCategory);

	}

	private void checkMeasureCategory(MeasureCategory measureCategory)
	{
		Assert.assertNotNull(measureCategory);
		Assert.assertNotNull(measureCategory.id);
		Assert.assertNotNull(measureCategory.code);
		Assert.assertNotNull(measureCategory.name);
		Assert.assertNotNull(measureCategory.measurePossibleValues);
		Assert.assertTrue(measureCategory.measurePossibleValues.size()>0);
		for(MeasureValue measureValue : measureCategory.measurePossibleValues){
			checkMeasureValue(measureValue);
		}

	}

	private MeasureCategory createMeasureCategory(String code, String name)
	{
		MeasureCategory measureCategory = new MeasureCategory();
		measureCategory.code=code;
		measureCategory.name=name;
		return measureCategory;
	}

	private MeasureValue createMeasureValue(String value, boolean defaultValue, MeasureCategory measureCategory)
	{
		MeasureValue measureValue = new MeasureValue();
		measureValue.value=value;
		measureValue.defaultValue=defaultValue;
		measureValue.measureCaterory=measureCategory;
		return measureValue;
	}

	private void checkMeasureValue(MeasureValue measureValue)
	{
		Assert.assertNotNull(measureValue);
		Assert.assertNotNull(measureValue.id);
		Assert.assertNotNull(measureValue.value);
		Assert.assertNotNull(measureValue.defaultValue);
	}

	/**
	 * TEST REAGENT_TYPE
	 */
	@Test
	public void addReagentType()
	{
		ReagentType reagentType = new ReagentType();
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state1"));
		states.add(createState("state2", "state2", 2, true));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		resolutions.add(createResolution("resol2", "resol2"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Reagent");
		MeasureCategory measureCategory = createMeasureCategory("cat2", "cat2");
		MeasureValue measureValue = createMeasureValue("value2", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value3", true));
		possibleValues.add(createValue("value4", false));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop1", "prop1", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		propertiesDefinitions.add(createPropertyDefinition("prop2", "prop2", true, true, "default", "descProp2", "format2", 2, "in", "content", true, true, "type2", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("reagent1", "reagent1", "reagent1", states, resolutions, propertiesDefinitions, objectType);
		reagentType.setCommonInfoType(commonInfoType);
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		reagentType = reagentTypeDAO.add(reagentType);
		reagentType=reagentTypeDAO.findByCode(reagentType.code);
		checkCommonInfoType(reagentType);
	}

	@Test
	public void updateReagentType()
	{
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentType.name="updateReagent1";
		reagentTypeDAO.update(reagentType);
		reagentType = reagentTypeDAO.findByCode("reagent1");
		Assert.assertTrue(reagentType.name.equals("updateReagent1"));
	}

	private void checkCommonInfoType(CommonInfoType commonInfoType)
	{
		Assert.assertNotNull(commonInfoType);
		Assert.assertNotNull(commonInfoType.id);
		Assert.assertNotNull(commonInfoType.code);
		Assert.assertNotNull(commonInfoType.name);
		Assert.assertNotNull(commonInfoType.collectionName);
		Assert.assertNotNull(commonInfoType.variableStates);
		Assert.assertTrue(commonInfoType.variableStates.size()>0);
		for(State state : commonInfoType.variableStates){
			checkState(state);
		}
		Assert.assertNotNull(commonInfoType.resolutions);
		Assert.assertTrue(commonInfoType.resolutions.size()>0);
		for(Resolution resolution : commonInfoType.resolutions){
			checkResolution(resolution);
		}
		Assert.assertNotNull(commonInfoType.propertiesDefinitions);
		Assert.assertTrue(commonInfoType.propertiesDefinitions.size()>0);
		for(PropertyDefinition propertyDefinition : commonInfoType.propertiesDefinitions){
			checkPropertyDefinition(propertyDefinition);
		}
		Assert.assertNotNull(commonInfoType.objectType);
		checkObjectType(commonInfoType.objectType);
	}

	private CommonInfoType createCommonInfoType(String code, String name, String collectionName, 
			List<State> variableStates, List<Resolution> resolutions, List<PropertyDefinition> propertiesDefinitions, ObjectType objectType)
	{
		CommonInfoType commonInfoType=new CommonInfoType();
		commonInfoType.code=code;
		commonInfoType.name=name;
		commonInfoType.collectionName=collectionName;
		commonInfoType.variableStates=variableStates;
		commonInfoType.resolutions=resolutions;
		commonInfoType.propertiesDefinitions=propertiesDefinitions;
		commonInfoType.objectType=objectType;
		return commonInfoType;
	}

	private void checkPropertyDefinition(PropertyDefinition propertyDefinition)
	{
		Assert.assertNotNull(propertyDefinition);
		Assert.assertNotNull(propertyDefinition.id);
		Assert.assertNotNull(propertyDefinition.code);
		Assert.assertNotNull(propertyDefinition.name);
		Assert.assertNotNull(propertyDefinition.active);
		Assert.assertNotNull(propertyDefinition.choiceInList);
		Assert.assertNotNull(propertyDefinition.defaultValue);
		Assert.assertNotNull(propertyDefinition.description);
		Assert.assertNotNull(propertyDefinition.displayFormat);
		Assert.assertNotNull(propertyDefinition.displayOrder);
		Assert.assertNotNull(propertyDefinition.inOut);
		Assert.assertNotNull(propertyDefinition.level);
		Assert.assertNotNull(propertyDefinition.propagation);
		Assert.assertNotNull(propertyDefinition.required);
		Assert.assertNotNull(propertyDefinition.type);
		Assert.assertNotNull(propertyDefinition.measureCategory);
		Assert.assertNotNull(propertyDefinition.measureValue);
		checkMeasureValue(propertyDefinition.measureValue);
		Assert.assertNotNull(propertyDefinition.possibleValues);
		Assert.assertTrue(propertyDefinition.possibleValues.size()>0);
		for(Value value : propertyDefinition.possibleValues){
			checkValue(value);
		}
	}

	private PropertyDefinition createPropertyDefinition(String code, String name, boolean active, boolean choiceInList, String defaultValue, String description, String displayFormat, Integer displayOrder, String inOut, String level, boolean propagation, boolean required, String type,
			MeasureCategory measureCategory,MeasureValue measureValue, List<Value> possibleValues)
	{
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.code=code;
		propertyDefinition.name=name;
		propertyDefinition.active=active;
		propertyDefinition.choiceInList=choiceInList;
		propertyDefinition.defaultValue=defaultValue;
		propertyDefinition.description=description;
		propertyDefinition.displayFormat=displayFormat;
		propertyDefinition.displayOrder=displayOrder;
		propertyDefinition.inOut=inOut;
		propertyDefinition.level=level;
		propertyDefinition.propagation=propagation;
		propertyDefinition.required=required;
		propertyDefinition.type=type;
		propertyDefinition.measureCategory=measureCategory;
		propertyDefinition.measureValue=measureValue;
		propertyDefinition.possibleValues=possibleValues;
		return propertyDefinition;
	}

	private Value createValue(String value, boolean defaultValue)
	{
		Value newValue = new Value();
		newValue.value=value;
		newValue.defaultValue=defaultValue;
		return newValue;
	}

	private void checkValue(Value value)
	{
		Assert.assertNotNull(value);
		Assert.assertNotNull(value.value);
		Assert.assertNotNull(value.defaultValue);
	}

	/**
	 * TEST PROTOCOL_CATEGORY
	 */
	@Test
	public void addProtocolCategory()
	{
		ProtocolCategory protocolCategory = createProtocolCategory("protoCat1", "protoCat1");
		ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
		protocolCategory = (ProtocolCategory) protocolCategoryDAO.add(protocolCategory);
		protocolCategory = protocolCategoryDAO.findById(protocolCategory.id);
		checkAbstractCategory(protocolCategory);
	}
	@Test
	public void updateProtocolCategory()
	{
		ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
		ProtocolCategory protocolCategory = (ProtocolCategory) protocolCategoryDAO.findByCode("protoCat1");
		protocolCategory.name="updateProtoCat1";
		protocolCategoryDAO.update(protocolCategory);
		protocolCategory = (ProtocolCategory) protocolCategoryDAO.findByCode("protoCat1");
		checkAbstractCategory(protocolCategory);
		Assert.assertTrue(protocolCategory.name.equals("updateProtoCat1"));
	}

	private void checkAbstractCategory(AbstractCategory abstractCategory)
	{
		Assert.assertNotNull(abstractCategory);
		Assert.assertNotNull(abstractCategory.id);
		Assert.assertNotNull(abstractCategory.code);
		Assert.assertNotNull(abstractCategory.name);
	}

	private ProtocolCategory createProtocolCategory(String code, String name)
	{
		ProtocolCategory protocolCategory = new ProtocolCategory();
		protocolCategory.code=code;
		protocolCategory.name=name;
		return protocolCategory;
	}

	/**
	 * TEST PROTOCOL
	 */
	@Test
	public void addProtocol()
	{
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentTypes.add(reagentType);
		Protocol protocol = createProtocol("proto1", "path1", "V1", createProtocolCategory("protoCat2", "protoCat2"), reagentTypes);
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocol = protocolDAO.add(protocol);
		protocol = protocolDAO.findById(protocol.id);
		checkProtocol(protocol);
	}

	@Test
	public void updateProtocol()
	{
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		Protocol protocol = protocolDAO.findByName("proto1");
		checkProtocol(protocol);
		protocol.name="updateProto1";
		protocolDAO.update(protocol);
		protocol = protocolDAO.findById(protocol.id);
		checkProtocol(protocol);
		Assert.assertTrue(protocol.name.equals("updateProto1"));
	}

	private void checkProtocol(Protocol protocol)
	{
		Assert.assertNotNull(protocol);
		Assert.assertNotNull(protocol.id);
		Assert.assertNotNull(protocol.name);
		Assert.assertNotNull(protocol.filePath);
		Assert.assertNotNull(protocol.version);
		Assert.assertNotNull(protocol.protocolCategory);
		checkAbstractCategory(protocol.protocolCategory);
		Assert.assertNotNull(protocol.reagentTypes);
		Assert.assertTrue(protocol.reagentTypes.size()>0);

	}
	private Protocol createProtocol(String name, String filePath, String version, ProtocolCategory protocolCategory, List<ReagentType> reagentTypes)
	{
		Protocol protocol = new Protocol();
		protocol.name=name;
		protocol.filePath=filePath;
		protocol.version=version;
		protocol.protocolCategory=protocolCategory;
		protocol.reagentTypes=reagentTypes;
		return protocol;

	}

	/**
	 * TEST CONTAINER_SUPPORT_CATEGORY
	 */
	@Test
	public void addContainerSupportCategory()
	{
		ContainerSupportCategory containerSupportCategory = createContainerSupportCategory("support1", "support1", 10, 10, 10);
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		containerSupportCategory = containerSupportCategoryDAO.add(containerSupportCategory);
		containerSupportCategory = containerSupportCategoryDAO.findByCode(containerSupportCategory.code);
		checkContainerSupportCategory(containerSupportCategory);
	}
	@Test
	public void updateContainerSupportCategory()
	{
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		ContainerSupportCategory containerSupportCategory = containerSupportCategoryDAO.findByCode("support1");
		checkContainerSupportCategory(containerSupportCategory);
		containerSupportCategory.name="updateSupport1";
		containerSupportCategory.nbLine=5;
		containerSupportCategoryDAO.update(containerSupportCategory);
		containerSupportCategory = containerSupportCategoryDAO.findByCode("support1");
		Assert.assertTrue(containerSupportCategory.name.equals("updateSupport1"));
		Assert.assertTrue(containerSupportCategory.nbLine==5);

	}
	private ContainerSupportCategory createContainerSupportCategory(String code, String name, int nbLine, int nbColumn, int nbUsableContainer)
	{
		ContainerSupportCategory containerSupportCategory = new ContainerSupportCategory();
		containerSupportCategory.code=code;
		containerSupportCategory.name=name;
		containerSupportCategory.nbLine=nbLine;
		containerSupportCategory.nbColumn=nbColumn;
		containerSupportCategory.nbUsableContainer=nbUsableContainer;
		return containerSupportCategory;

	}

	private void checkContainerSupportCategory(ContainerSupportCategory containerSupportCategory)
	{
		checkAbstractCategory(containerSupportCategory);
		Assert.assertNotNull(containerSupportCategory.nbLine);
		Assert.assertNotNull(containerSupportCategory.nbColumn);
		Assert.assertNotNull(containerSupportCategory.nbUsableContainer);
	}

	/**
	 * TEST INSTRUMENT_CATEGORY
	 */
	@Test
	public void addInstrumentCategory()
	{
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
		List<ContainerSupportCategory> inContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		inContainerSupportCategories.add(containerSupportCategoryDAO.findByCode("support1"));
		List<ContainerSupportCategory> outContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		outContainerSupportCategories.add(createContainerSupportCategory("support2", "support2", 5, 10, 10));
		InstrumentCategory instrumentCategory = createInstrumentCategory("InstCat1", "InstCat1", 1, inContainerSupportCategories, 1, outContainerSupportCategories);
		InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
		instrumentCategory = instrumentCategoryDAO.add(instrumentCategory);
		instrumentCategory = instrumentCategoryDAO.findById(instrumentCategory.id);
		checkInstrumentCategory(instrumentCategory);
	}
	@Test
	public void updateInstrumentCategory()
	{
		InstrumentCategoryDAO instrumentCategoryDAO = Spring.getBeanOfType(InstrumentCategoryDAO.class);
		InstrumentCategory instrumentCategory = instrumentCategoryDAO.findByCode("InstCat1");
		checkInstrumentCategory(instrumentCategory);
		instrumentCategory.name="UpdateInstCat1";
		instrumentCategory.inContainerSupportCategories.add(createContainerSupportCategory("support3", "support3", 10, 10, 10));
		instrumentCategory.outContainerSupportCategories.add(createContainerSupportCategory("support4", "support4", 10, 10, 10));
		instrumentCategoryDAO.update(instrumentCategory);
		instrumentCategory = instrumentCategoryDAO.findById(instrumentCategory.id);
		checkInstrumentCategory(instrumentCategory);
		Assert.assertTrue(instrumentCategory.name.equals("UpdateInstCat1"));
		Assert.assertTrue(instrumentCategory.inContainerSupportCategories.size()==2);
		Assert.assertTrue(instrumentCategory.outContainerSupportCategories.size()==2);

	}
	private InstrumentCategory createInstrumentCategory(String code, String name, 
			int nbInContainerSupportCategories, List<ContainerSupportCategory> inContainerSupportCategories,
			int nbOutContainerSupportCategories, List<ContainerSupportCategory> outContainerSupportCategories)
	{
		InstrumentCategory instrumentCategory = new InstrumentCategory();
		instrumentCategory.code=code;
		instrumentCategory.name=name;
		instrumentCategory.nbInContainerSupportCategories=nbInContainerSupportCategories;
		instrumentCategory.inContainerSupportCategories=inContainerSupportCategories;
		instrumentCategory.nbOutContainerSupportCategories=nbOutContainerSupportCategories;
		instrumentCategory.outContainerSupportCategories=outContainerSupportCategories;
		return instrumentCategory;
	}
	private void checkInstrumentCategory(InstrumentCategory instrumentCategory)
	{
		checkAbstractCategory(instrumentCategory);
		Assert.assertNotNull(instrumentCategory.nbInContainerSupportCategories);
		Assert.assertNotNull(instrumentCategory.nbOutContainerSupportCategories);
		Assert.assertNotNull(instrumentCategory.inContainerSupportCategories);
		Assert.assertTrue(instrumentCategory.inContainerSupportCategories.size()>0);
		for(ContainerSupportCategory containerSupportCategory :instrumentCategory.inContainerSupportCategories){
			checkContainerSupportCategory(containerSupportCategory);
		}
		Assert.assertNotNull(instrumentCategory.outContainerSupportCategories);
		Assert.assertTrue(instrumentCategory.outContainerSupportCategories.size()>0);
		for(ContainerSupportCategory containerSupportCategory :instrumentCategory.outContainerSupportCategories){
			checkContainerSupportCategory(containerSupportCategory);
		}
	}

	/**
	 * TEST INSTRUMENT_USED_TYPE
	 */
	@Test
	public void addInstrumentUsedType()
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Instrument");
		MeasureCategory measureCategory = createMeasureCategory("cat3", "cat3");
		MeasureValue measureValue = createMeasureValue("value3", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value5", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop3", "prop3", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("inst1", "inst1", "inst1", states, resolutions, propertiesDefinitions, objectType);

		//Get instrumentCategory
		List<ContainerSupportCategory> inContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		inContainerSupportCategories.add(createContainerSupportCategory("support5", "support5", 10, 10, 10));
		List<ContainerSupportCategory> outContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		outContainerSupportCategories.add(createContainerSupportCategory("support6", "support6", 10, 10, 10));
		InstrumentCategory instrumentCategory = createInstrumentCategory("InstCat2", "InstCat2", 10, inContainerSupportCategories, 10, outContainerSupportCategories);

		//Get instrument
		List<Instrument> instruments = new ArrayList<Instrument>();
		instruments.add(createInstrument("inst1", "inst1"));

		InstrumentUsedType instrumentUsedType = createInstrumentUsedType(commonInfoType, instrumentCategory, instruments);
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedType = instrumentUsedTypeDAO.add(instrumentUsedType);
		instrumentUsedType = instrumentUsedTypeDAO.findById(instrumentUsedType.id);
		checkInstrumentUsedType(instrumentUsedType);

	}

	@Test
	public void updateInstrumentUsedType()
	{
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		InstrumentUsedType instrumentUsedType = instrumentUsedTypeDAO.findByCode("inst1");
		checkInstrumentUsedType(instrumentUsedType);
		instrumentUsedType.name="updateInst1";
		instrumentUsedType.instruments.add(createInstrument("inst2", "inst2"));
		instrumentUsedTypeDAO.update(instrumentUsedType);
		instrumentUsedType=instrumentUsedTypeDAO.findById(instrumentUsedType.id);
		checkInstrumentUsedType(instrumentUsedType);
		Assert.assertTrue(instrumentUsedType.name.equals("updateInst1"));
		Assert.assertTrue(instrumentUsedType.instruments.size()==2);
	}

	private InstrumentUsedType createInstrumentUsedType(CommonInfoType commonInfoType, InstrumentCategory instrumentCategory,List<Instrument> instruments)
	{
		InstrumentUsedType instrumentUsedType = new InstrumentUsedType();
		instrumentUsedType.setCommonInfoType(commonInfoType);
		instrumentUsedType.instrumentCategory=instrumentCategory;
		instrumentUsedType.instruments=instruments;
		return instrumentUsedType;
	}

	private void checkInstrumentUsedType(InstrumentUsedType instrumentUsedType)
	{
		Assert.assertNotNull(instrumentUsedType);
		checkCommonInfoType(instrumentUsedType);
		checkInstrumentCategory(instrumentUsedType.instrumentCategory);
		Assert.assertNotNull(instrumentUsedType.instruments);
		Assert.assertTrue(instrumentUsedType.instruments.size()>0);
		for(Instrument instrument : instrumentUsedType.instruments){
			checkInstrument(instrument);
		}
	}
	private Instrument createInstrument(String code, String name)
	{
		Instrument instrument = new Instrument();
		instrument.code=code;
		instrument.name=name;
		return instrument;
	}
	private void checkInstrument(Instrument instrument)
	{
		Assert.assertNotNull(instrument);
		Assert.assertNotNull(instrument.id);
		Assert.assertNotNull(instrument.code);
		Assert.assertNotNull(instrument.name);
	}
	/**
	 * TEST PURIFICATION_METHOD_TYPE
	 */
	@Test
	public void addPurificationMethodType()
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Purification");
		MeasureCategory measureCategory = createMeasureCategory("cat4", "cat4");
		MeasureValue measureValue = createMeasureValue("value4", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value6", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop4", "prop4", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("purif1", "purif1", "purif1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypes.add(instrumentUsedTypeDAO.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocols.add(protocolDAO.findByName("updateProto1"));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentTypes.add(reagentType);
		protocols.add(createProtocol("proto1", "path2", "V2", createProtocolCategory("protoCat3", "protoCat3"), reagentTypes));

		PurificationMethodType purificationMethodType = createPurificationMethodType(commonInfoType, instrumentUsedTypes, protocols);
		PurificationMethodTypeDAO purificationMethodTypeDAO = Spring.getBeanOfType(PurificationMethodTypeDAO.class);
		purificationMethodType = purificationMethodTypeDAO.add(purificationMethodType);
		purificationMethodType = purificationMethodTypeDAO.findById(purificationMethodType.id);
		checkAbstractExperiment(purificationMethodType);
	}

	@Test
	public void updatePurificationMethodType()
	{
		PurificationMethodTypeDAO purificationMethodTypeDAO = Spring.getBeanOfType(PurificationMethodTypeDAO.class);
		PurificationMethodType purificationMethodType = purificationMethodTypeDAO.findByCode("purif1");
		checkAbstractExperiment(purificationMethodType);
		purificationMethodType.name="updatePurif1";

		//Add experiment type
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Instrument");
		MeasureCategory measureCategory = createMeasureCategory("cat5", "cat5");
		MeasureValue measureValue = createMeasureValue("value5", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value6", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop4", "prop4", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("inst3", "inst3", "inst3", states, resolutions, propertiesDefinitions, objectType);

		//Get instrumentCategory
		List<ContainerSupportCategory> inContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		inContainerSupportCategories.add(createContainerSupportCategory("support7", "support7", 10, 10, 10));
		List<ContainerSupportCategory> outContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		outContainerSupportCategories.add(createContainerSupportCategory("support8", "support8", 10, 10, 10));
		InstrumentCategory instrumentCategory = createInstrumentCategory("InstCat3", "InstCat3", 10, inContainerSupportCategories, 10, outContainerSupportCategories);

		//Get instrument
		List<Instrument> instruments = new ArrayList<Instrument>();
		instruments.add(createInstrument("inst3", "inst3"));

		purificationMethodType.instrumentUsedTypes.add(createInstrumentUsedType(commonInfoType, instrumentCategory, instruments));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentTypes.add(reagentType);
		purificationMethodType.protocols.add(createProtocol("proto3", "path3", "V2", createProtocolCategory("protoCat4", "protoCat4"), reagentTypes));

		purificationMethodTypeDAO.update(purificationMethodType);
		purificationMethodType = purificationMethodTypeDAO.findById(purificationMethodType.id);
		checkAbstractExperiment(purificationMethodType);
		Assert.assertTrue(purificationMethodType.name.equals("updatePurif1"));
		Assert.assertTrue(purificationMethodType.protocols.size()==3);
		Assert.assertTrue(purificationMethodType.instrumentUsedTypes.size()==2);
	}


	private PurificationMethodType createPurificationMethodType(CommonInfoType commonInfoType, List<InstrumentUsedType> instrumentUsedTypes, List<Protocol> protocols)
	{
		PurificationMethodType purificationMethodType = new PurificationMethodType();
		purificationMethodType.setCommonInfoType(commonInfoType);
		purificationMethodType.instrumentUsedTypes=instrumentUsedTypes;
		purificationMethodType.protocols=protocols;
		return purificationMethodType;
	}

	private void checkAbstractExperiment(AbstractExperiment experiment)
	{
		Assert.assertNotNull(experiment);
		checkCommonInfoType(experiment);
		Assert.assertNotNull(experiment.instrumentUsedTypes);
		Assert.assertTrue(experiment.instrumentUsedTypes.size()>0);
		for(InstrumentUsedType instrumentUsedType : experiment.instrumentUsedTypes){
			checkInstrumentUsedType(instrumentUsedType);
		}
		Assert.assertNotNull(experiment.protocols);
		Assert.assertTrue(experiment.protocols.size()>0);
		for(Protocol protocol : experiment.protocols){
			checkProtocol(protocol);
		}
	}


	/**
	 * TEST QUALITY_CONTROL_TYPE
	 */
	@Test
	public void addQualityControlType()
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("ControlQuality");
		MeasureCategory measureCategory = createMeasureCategory("cat6", "cat6");
		MeasureValue measureValue = createMeasureValue("value5", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value7", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop5", "prop5", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("qc1", "qc1", "qc1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypes.add(instrumentUsedTypeDAO.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocols.add(protocolDAO.findByName("updateProto1"));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentTypes.add(reagentType);
		protocols.add(createProtocol("proto2", "path2", "V3", createProtocolCategory("protoCat5", "protoCat5"), reagentTypes));

		QualityControlType qualityControlType = createQualityControlType(commonInfoType, instrumentUsedTypes, protocols);
		QualityControlTypeDAO qualityControlTypeDAO = Spring.getBeanOfType(QualityControlTypeDAO.class);
		qualityControlType = qualityControlTypeDAO.add(qualityControlType);
		qualityControlType = qualityControlTypeDAO.findById(qualityControlType.id);
		checkAbstractExperiment(qualityControlType);
	}
	@Test
	public void updateQualityControlType()
	{
		QualityControlTypeDAO qualityControlTypeDAO = Spring.getBeanOfType(QualityControlTypeDAO.class);
		QualityControlType qualityControlType = qualityControlTypeDAO.findByCode("qc1");
		checkAbstractExperiment(qualityControlType);
		qualityControlType.name="updateQC1";

		//Add experiment type
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Instrument");
		MeasureCategory measureCategory = createMeasureCategory("cat7", "cat7");
		MeasureValue measureValue = createMeasureValue("value6", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value8", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop6", "prop6", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("inst4", "inst4", "inst4", states, resolutions, propertiesDefinitions, objectType);

		//Get instrumentCategory
		List<ContainerSupportCategory> inContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		inContainerSupportCategories.add(createContainerSupportCategory("support9", "support9", 10, 10, 10));
		List<ContainerSupportCategory> outContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		outContainerSupportCategories.add(createContainerSupportCategory("support10", "support10", 10, 10, 10));
		InstrumentCategory instrumentCategory = createInstrumentCategory("InstCat4", "InstCat4", 10, inContainerSupportCategories, 10, outContainerSupportCategories);

		//Get instrument
		List<Instrument> instruments = new ArrayList<Instrument>();
		instruments.add(createInstrument("inst4", "inst4"));

		qualityControlType.instrumentUsedTypes.add(createInstrumentUsedType(commonInfoType, instrumentCategory, instruments));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		ReagentType reagentType = reagentTypeDAO.findByCode("reagent1");
		reagentTypes.add(reagentType);
		qualityControlType.protocols.add(createProtocol("proto4", "path4", "V2", createProtocolCategory("protoCat6", "protoCat6"), reagentTypes));

		qualityControlTypeDAO.update(qualityControlType);
		qualityControlType = qualityControlTypeDAO.findById(qualityControlType.id);
		checkAbstractExperiment(qualityControlType);
		Assert.assertTrue(qualityControlType.name.equals("updateQC1"));
		Assert.assertTrue(qualityControlType.protocols.size()==3);
		Assert.assertTrue(qualityControlType.instrumentUsedTypes.size()==2);
	}

	private QualityControlType createQualityControlType(CommonInfoType commonInfoType, List<InstrumentUsedType> instrumentUsedTypes, List<Protocol> protocols)
	{
		QualityControlType qualityControlType = new QualityControlType();
		qualityControlType.setCommonInfoType(commonInfoType);
		qualityControlType.instrumentUsedTypes=instrumentUsedTypes;
		qualityControlType.protocols=protocols;
		return qualityControlType;
	}

	/**
	 * TEST EXPERIMENT_CATEGORY
	 */
	@Test
	public void addExperimentCategory()
	{
		ExperimentCategory experimentCategory = createExperimentCategory("expCat1", "expCat2");
		ExperimentCategoryDAO experimentCategoryDAO = Spring.getBeanOfType(ExperimentCategoryDAO.class);
		experimentCategory=experimentCategoryDAO.add(experimentCategory);
		experimentCategory=experimentCategoryDAO.findById(experimentCategory.id);
		checkAbstractCategory(experimentCategory);

	}
	@Test
	public void updateExperimentCategory()
	{
		ExperimentCategoryDAO experimentCategoryDAO = Spring.getBeanOfType(ExperimentCategoryDAO.class);
		ExperimentCategory experimentCategory = experimentCategoryDAO.findByCode("expCat1");
		checkAbstractCategory(experimentCategory);
		experimentCategory.name="updateExpCat1";
		experimentCategoryDAO.update(experimentCategory);
		experimentCategory = experimentCategoryDAO.findById(experimentCategory.id);
		checkAbstractCategory(experimentCategory);
		Assert.assertTrue(experimentCategory.name.equals("updateExpCat1"));
	}
	private ExperimentCategory createExperimentCategory(String code, String name)
	{
		ExperimentCategory experimentCategory = new ExperimentCategory();
		experimentCategory.code=code;
		experimentCategory.name=name;
		return experimentCategory;
	}
	/**
	 * TEST EXPERIMENT_TYPE
	 */
	@Test
	public void addExperimentType()
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("ControlQuality");
		MeasureCategory measureCategory = createMeasureCategory("cat8", "cat8");
		MeasureValue measureValue = createMeasureValue("value6", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value8", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop6", "prop6", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("exp1", "exp1", "exp1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypes.add(instrumentUsedTypeDAO.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocols.add(protocolDAO.findByName("updateProto1"));

		PurificationMethodType purif = Spring.getBeanOfType(PurificationMethodTypeDAO.class).findByCode("purif1");
		List<PurificationMethodType> purificationMethodTypes = new ArrayList<PurificationMethodType>();
		purificationMethodTypes.add(purif);

		QualityControlType qc = Spring.getBeanOfType(QualityControlTypeDAO.class).findByCode("qc1");
		List<QualityControlType> qualityControlTypes = new ArrayList<QualityControlType>();
		qualityControlTypes.add(qc);

		ExperimentCategory experimentCategory = Spring.getBeanOfType(ExperimentCategoryDAO.class).findByCode("expCat1");

		ExperimentType experimentType = createExperimentType(commonInfoType, protocols, instrumentUsedTypes, experimentCategory, new ArrayList<ExperimentType>(),
				true, true, purificationMethodTypes, true, true, qualityControlTypes);
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		experimentType = experimentTypeDAO.add(experimentType);
		experimentType=experimentTypeDAO.findById(experimentType.id);
		checkExperimentType(experimentType);
	}

	@Test
	public void updateExperimentType()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		ExperimentType experimentType = experimentTypeDAO.findByCode("exp1");
		checkExperimentType(experimentType);
		experimentType.name="updateExp1";
		experimentType.doPurification=false;
		//Create purification
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(Spring.getBeanOfType(StateDAO.class).findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Spring.getBeanOfType(ResolutionDAO.class).findByCode("resol1"));
		ObjectType objectType = Spring.getBeanOfType(ObjectTypeDAO.class).find("Purification");
		MeasureCategory measureCategory = createMeasureCategory("cat9", "cat9");
		MeasureValue measureValue = createMeasureValue("value7", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value9", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop7", "prop7", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("purif2", "purif2", "purif2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypes.add(instrumentUsedTypeDAO.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Spring.getBeanOfType(ProtocolDAO.class).findByName("updateProto1"));

		PurificationMethodType purificationMethodType = createPurificationMethodType(commonInfoType, instrumentUsedTypes, protocols);
		experimentType.possiblePurificationMethodTypes.add(purificationMethodType);
		//Create quality control
		//Create commonInfoType
		states = new ArrayList<State>();
		states.add(Spring.getBeanOfType(StateDAO.class).findByCode("state2"));
		resolutions = new ArrayList<Resolution>();
		resolutions.add(Spring.getBeanOfType(ResolutionDAO.class).findByCode("resol1"));
		objectType = Spring.getBeanOfType(ObjectTypeDAO.class).find("ControlQuality");
		measureCategory = createMeasureCategory("cat10", "cat10");
		measureValue = createMeasureValue("value8", true, measureCategory);
		possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value10", true));
		propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop8", "prop8", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		commonInfoType = createCommonInfoType("qc2", "qc2", "qc2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(Spring.getBeanOfType(InstrumentUsedTypeDAO.class).findByCode("inst1"));

		//Create liste protocol
		protocols = new ArrayList<Protocol>();
		protocols.add(Spring.getBeanOfType(ProtocolDAO.class).findByName("updateProto1"));
		QualityControlType qualityControlType = createQualityControlType(commonInfoType, instrumentUsedTypes, protocols);
		experimentType.possibleQualityControlTypes.add(qualityControlType);
		experimentTypeDAO.update(experimentType);
		experimentType=experimentTypeDAO.findById(experimentType.id);
		checkExperimentType(experimentType);
		Assert.assertTrue(experimentType.name.equals("updateExp1"));
		Assert.assertFalse(experimentType.doPurification);
		Assert.assertTrue(experimentType.possiblePurificationMethodTypes.size()==2);
		Assert.assertTrue(experimentType.possibleQualityControlTypes.size()==2);

	}

	@Test
	public void addPreviousExperiment()
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("ControlQuality");
		MeasureCategory measureCategory = createMeasureCategory("cat11", "cat11");
		MeasureValue measureValue = createMeasureValue("value7", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value9", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop7", "prop7", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("exp2", "exp2", "exp2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		instrumentUsedTypes.add(instrumentUsedTypeDAO.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		protocols.add(protocolDAO.findByName("updateProto1"));

		PurificationMethodType purif = Spring.getBeanOfType(PurificationMethodTypeDAO.class).findByCode("purif1");
		List<PurificationMethodType> purificationMethodTypes = new ArrayList<PurificationMethodType>();
		purificationMethodTypes.add(purif);

		QualityControlType qc = Spring.getBeanOfType(QualityControlTypeDAO.class).findByCode("qc1");
		List<QualityControlType> qualityControlTypes = new ArrayList<QualityControlType>();
		qualityControlTypes.add(qc);

		ExperimentCategory experimentCategory = Spring.getBeanOfType(ExperimentCategoryDAO.class).findByCode("expCat1");

		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> previousExperiment = new ArrayList<ExperimentType>();
		previousExperiment.add(experimentTypeDAO.findByCode("exp1"));
		ExperimentType experimentType = createExperimentType(commonInfoType, protocols, instrumentUsedTypes, experimentCategory, new ArrayList<ExperimentType>(),
				true, true, purificationMethodTypes, true, true, qualityControlTypes);
		experimentType.previousExperimentTypes=previousExperiment;
		experimentType = experimentTypeDAO.add(experimentType);
		checkExperimentType(experimentType);
		experimentType = experimentTypeDAO.findByCode("exp2");
		Assert.assertNotNull(experimentType.previousExperimentTypes);
		Assert.assertTrue(experimentType.previousExperimentTypes.size()==1);
	}

	private ExperimentType createExperimentType(CommonInfoType commonInfoType, List<Protocol> protocols, List<InstrumentUsedType> instrumentUsedTypes,
			ExperimentCategory experimentCategory, List<ExperimentType> previousExperimentTypes,
			boolean doPurification, boolean mandatoryPurification, List<PurificationMethodType> possiblePurificationMethodTypes,
			boolean doQualityControl, boolean mandatoryQualityControl, List<QualityControlType> possibleQualityControlTypes)
	{
		ExperimentType experimentType = new ExperimentType();
		experimentType.setCommonInfoType(commonInfoType);
		experimentType.protocols=protocols;
		experimentType.instrumentUsedTypes=instrumentUsedTypes;
		experimentType.experimentCategory=experimentCategory;
		experimentType.previousExperimentTypes=previousExperimentTypes;
		experimentType.doPurification=doPurification;
		experimentType.mandatoryPurification=mandatoryPurification;
		experimentType.possiblePurificationMethodTypes=possiblePurificationMethodTypes;
		experimentType.doQualityControl=doQualityControl;
		experimentType.mandatoryQualityControl=mandatoryQualityControl;
		experimentType.possibleQualityControlTypes=possibleQualityControlTypes;
		return experimentType;
	}

	private void checkExperimentType(ExperimentType experimentType)
	{
		checkAbstractExperiment(experimentType);
		Assert.assertNotNull(experimentType.doPurification);
		Assert.assertNotNull(experimentType.mandatoryPurification);
		Assert.assertNotNull(experimentType.possiblePurificationMethodTypes);
		Assert.assertTrue(experimentType.possiblePurificationMethodTypes.size()>0);
		for(PurificationMethodType purificationMethodType : experimentType.possiblePurificationMethodTypes){
			checkAbstractExperiment(purificationMethodType);
		}
		Assert.assertNotNull(experimentType.doQualityControl);
		Assert.assertNotNull(experimentType.mandatoryQualityControl);
		Assert.assertNotNull(experimentType.possibleQualityControlTypes);
		Assert.assertTrue(experimentType.possibleQualityControlTypes.size()>0);
		for(QualityControlType qualityControlType : experimentType.possibleQualityControlTypes){
			checkAbstractExperiment(qualityControlType);
		}


	}

	/**
	 * TEST PROCESS_CATEGORY
	 */
	@Test
	public void addProcessCategory()
	{
		ProcessCategory processCategory = createProcessCategory("processCat1", "processCat1");
		ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
		processCategory = processCategoryDAO.add(processCategory);
		processCategory = processCategoryDAO.findById(processCategory.id);
		checkAbstractCategory(processCategory);
	}
	@Test
	public void updateProcessCategory()
	{
		ProcessCategoryDAO processCategoryDAO = Spring.getBeanOfType(ProcessCategoryDAO.class);
		ProcessCategory processCategory = processCategoryDAO.findByCode("processCat1");
		checkAbstractCategory(processCategory);
		processCategory.name="updateProcessCat1";
		processCategoryDAO.update(processCategory);
		processCategory = processCategoryDAO.findById(processCategory.id);
		checkAbstractCategory(processCategory);
		Assert.assertTrue(processCategory.name.equals("updateProcessCat1"));
	}

	private ProcessCategory createProcessCategory(String code, String name)
	{
		ProcessCategory processCategory = new ProcessCategory();
		processCategory.code=code;
		processCategory.name=name;
		return processCategory;
	}

	/**
	 * TEST PROCESS_TYPE
	 */
	@Test
	public void addProcessType()
	{
		List<ExperimentType> experimentTypes = new ArrayList<ExperimentType>();
		experimentTypes.add(Spring.getBeanOfType(ExperimentTypeDAO.class).findByCode("exp1"));
		ProcessCategory processCategory = Spring.getBeanOfType(ProcessCategoryDAO.class).findByCode("processCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Process");
		MeasureCategory measureCategory = createMeasureCategory("cat12", "cat12");
		MeasureValue measureValue = createMeasureValue("value8", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value10", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop8", "prop8", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("process1", "process1", "process1", states, resolutions, propertiesDefinitions, objectType);
		ProcessType processType = createProcessType(commonInfoType, experimentTypes, processCategory);
		ProcessTypeDAO processTypeDAO = Spring.getBeanOfType(ProcessTypeDAO.class);
		processType = processTypeDAO.add(processType);
		processType = processTypeDAO.findById(processType.id);
		checkProcessType(processType);
	}
	@Test
	public void updateProcessType()
	{
		ProcessTypeDAO processTypeDAO = Spring.getBeanOfType(ProcessTypeDAO.class);
		ProcessType processType = processTypeDAO.findByCode("process1");
		checkProcessType(processType);
		processType.name="updateProcess1";
		ExperimentType exp2 = Spring.getBeanOfType(ExperimentTypeDAO.class).findByCode("exp2");
		processType.experimentTypes.add(exp2);
		processTypeDAO.update(processType);
		processType = processTypeDAO.findById(processType.id);
		checkProcessType(processType);
		Assert.assertTrue(processType.name.equals("updateProcess1"));
		Assert.assertTrue(processType.experimentTypes.size()==2);
		
	}

	private ProcessType createProcessType(CommonInfoType commonInfoType, List<ExperimentType> experimentTypes, ProcessCategory processCategory)
	{
		ProcessType processType = new ProcessType();
		processType.setCommonInfoType(commonInfoType);
		processType.experimentTypes=experimentTypes;
		processType.processCategory=processCategory;
		return processType;
	}
	
	private void checkProcessType(ProcessType processType)
	{
		Assert.assertNotNull(processType);
		checkCommonInfoType(processType);
		checkAbstractCategory(processType.processCategory);
		Assert.assertNotNull(processType.experimentTypes);
		Assert.assertTrue(processType.experimentTypes.size()>0);
		for(ExperimentType experimentType : processType.experimentTypes){
			checkExperimentType(experimentType);
		}
	}
	
	/**
	 * TEST PROJECT_CATEGORY
	 */
	@Test
	public void addProjectCategory()
	{
		ProjectCategory projectCategory = createProjectCategory("projectCat1", "projectCat1");
		ProjectCategoryDAO projectCategoryDAO = Spring.getBeanOfType(ProjectCategoryDAO.class);
		projectCategory = projectCategoryDAO.add(projectCategory);
		projectCategory = projectCategoryDAO.findById(projectCategory.id);
		checkAbstractCategory(projectCategory);
	}
	@Test
	public void updateProjectCategory()
	{
		ProjectCategoryDAO projectCategoryDAO = Spring.getBeanOfType(ProjectCategoryDAO.class);
		ProjectCategory projectCategory = projectCategoryDAO.findByCode("projectCat1");
		checkAbstractCategory(projectCategory);
		projectCategory.name="updateProjectCat1";
		projectCategoryDAO.update(projectCategory);
		projectCategory = projectCategoryDAO.findById(projectCategory.id);
		checkAbstractCategory(projectCategory);
		Assert.assertTrue(projectCategory.name.equals("updateProjectCat1"));
	}

	private ProjectCategory createProjectCategory(String code, String name)
	{
		ProjectCategory projectCategory = new ProjectCategory();
		projectCategory.code=code;
		projectCategory.name=name;
		return projectCategory;
	}

	/**
	 * TEST PROJECT_TYPE
	 */
	@Test
	public void addProjectType()
	{
		ProjectCategory projectCategory = Spring.getBeanOfType(ProjectCategoryDAO.class).findByCode("projectCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Project");
		MeasureCategory measureCategory = createMeasureCategory("cat13", "cat13");
		MeasureValue measureValue = createMeasureValue("value9", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value11", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop9", "prop9", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("project1", "project1", "project1", states, resolutions, propertiesDefinitions, objectType);
		ProjectType projectType = createProjectType(commonInfoType, projectCategory);
		ProjectTypeDAO projectTypeDAO = Spring.getBeanOfType(ProjectTypeDAO.class);
		projectType = projectTypeDAO.add(projectType);
		projectType = projectTypeDAO.findById(projectType.id);
		checkProjectType(projectType);
	}
	@Test
	public void updateProjectType()
	{
		ProjectTypeDAO projectTypeDAO = Spring.getBeanOfType(ProjectTypeDAO.class);
		ProjectType projectType = projectTypeDAO.findByCode("project1");
		checkProjectType(projectType);
		projectType.name="updateProject1";
		projectTypeDAO.update(projectType);
		projectType = projectTypeDAO.findById(projectType.id);
		checkProjectType(projectType);
		Assert.assertTrue(projectType.name.equals("updateProject1"));
	}
	
	private ProjectType createProjectType(CommonInfoType commonInfoType,  ProjectCategory projectCategory)
	{
		ProjectType projectType = new ProjectType();
		projectType.setCommonInfoType(commonInfoType);
		projectType.projectCategory=projectCategory;
		return projectType;
	}
	
	private void checkProjectType(ProjectType projectType)
	{
		Assert.assertNotNull(projectType);
		checkCommonInfoType(projectType);
		checkAbstractCategory(projectType.projectCategory);
	}
	
	/**
	 * TEST SAMPLE_CATEGORY
	 */
	@Test
	public void addSampleCategory()
	{
		SampleCategory sampleCategory = createSampleCategory("sampleCat1", "sampleCat1");
		SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
		sampleCategory = sampleCategoryDAO.add(sampleCategory);
		sampleCategory = sampleCategoryDAO.findById(sampleCategory.id);
		checkAbstractCategory(sampleCategory);
	}
	@Test
	public void updateSampleCategory()
	{
		SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
		SampleCategory sampleCategory = sampleCategoryDAO.findByCode("sampleCat1");
		checkAbstractCategory(sampleCategory);
		sampleCategory.name="updateSampleCat1";
		sampleCategoryDAO.update(sampleCategory);
		sampleCategory = sampleCategoryDAO.findById(sampleCategory.id);
		checkAbstractCategory(sampleCategory);
		Assert.assertTrue(sampleCategory.name.equals("updateSampleCat1"));
	}

	private SampleCategory createSampleCategory(String code, String name)
	{
		SampleCategory sampleCategory = new SampleCategory();
		sampleCategory.code=code;
		sampleCategory.name=name;
		return sampleCategory;
	}

	/**
	 * TEST SAMPLE_TYPE
	 */
	@Test
	public void addSampleType()
	{
		SampleCategory sampleCategory = Spring.getBeanOfType(SampleCategoryDAO.class).findByCode("sampleCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		states.add(stateDAO.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
		resolutions.add(resolutionDAO.findByCode("resol1"));
		ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
		ObjectType objectType = objectTypeDAO.find("Project");
		MeasureCategory measureCategory = createMeasureCategory("cat14", "cat14");
		MeasureValue measureValue = createMeasureValue("value9", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value12", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop10", "prop10", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("sample1", "sample1", "sample1", states, resolutions, propertiesDefinitions, objectType);
		SampleType sampleType = createSampleType(commonInfoType, sampleCategory);
		SampleTypeDAO sampleTypeDAO = Spring.getBeanOfType(SampleTypeDAO.class);
		sampleType = sampleTypeDAO.add(sampleType);
		sampleType = sampleTypeDAO.findById(sampleType.id);
		checkSampleType(sampleType);
	}
	@Test
	public void updateSampleType()
	{
		SampleTypeDAO sampleTypeDAO = Spring.getBeanOfType(SampleTypeDAO.class);
		SampleType sampleType = sampleTypeDAO.findByCode("sample1");
		checkSampleType(sampleType);
		sampleType.name="updateSample1";
		sampleTypeDAO.update(sampleType);
		sampleType = sampleTypeDAO.findById(sampleType.id);
		checkSampleType(sampleType);
		Assert.assertTrue(sampleType.name.equals("updateSample1"));
	}
	
	private SampleType createSampleType(CommonInfoType commonInfoType,  SampleCategory sampleCategory)
	{
		SampleType sampleType = new SampleType();
		sampleType.setCommonInfoType(commonInfoType);
		sampleType.sampleCategory=sampleCategory;
		return sampleType;
	}
	
	private void checkSampleType(SampleType sampleType)
	{
		Assert.assertNotNull(sampleType);
		checkCommonInfoType(sampleType);
		checkAbstractCategory(sampleType.sampleCategory);
	}
	
}
