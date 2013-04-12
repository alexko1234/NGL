package models.dao;


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
import models.laboratory.common.description.StateCategory;
import models.laboratory.common.description.Value;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.AbstractExperiment;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.experiment.description.ReagentType;
import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processus.description.ProcessCategory;
import models.laboratory.processus.description.ProcessType;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.description.dao.SampleCategoryDAO;
import models.utils.dao.DAOException;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import play.api.modules.spring.Spring;
import utils.AbstractTests;
import play.test.Helpers;


/**
 * Test sur base vide avec dump.sql
 * @author ejacoby
 *
 */

public class SpringTest extends AbstractTests{
	
	@BeforeClass
	public static void start(){
		 app = getFakeApplication();
		 Helpers.start(app);
		/*  Map<String, String> flashData = Collections.emptyMap();
		  Map<String, Object> listeNull = Collections.emptyMap();
	      Http.Context context = new Http.Context(new Long(5),null,request, flashData, flashData,listeNull);
	      Http.Context.current.set(context);*/
	}
	
	
	@AfterClass
	public static void stop(){
		Helpers.stop(app);
	}
	
	/**
	 * Use to drop and create database schema
	 * Obsolete with remove test
	 * //////@Test
	 */
	public void initializeDB()
	{
		ExecuteSQLDAO initializeDatabaseDAO = Spring.getBeanOfType(ExecuteSQLDAO.class);
		Resource resource = new ClassPathResource("/schemaInnoDB.sql");
		initializeDatabaseDAO.executeScript(resource);
	}
	
	/**
	 * TEST OBJECT_TYPE
	 * @throws DAOException 
	 */
	@Test
	public void saveObjectType() throws DAOException
	{
		ObjectType objectType = new ObjectType();
		objectType.code="Test";
		objectType.generic=false;
		long id = objectType.save();
		ObjectType objectTypeDB = ObjectType.find.findById(id);
		checkObjectType(objectTypeDB);
		objectTypeDB = ObjectType.find.findById(objectTypeDB.id);
		Assert.assertTrue(objectType.code.equals(objectTypeDB.code));
		Assert.assertTrue(objectType.generic.equals(objectTypeDB.generic));

	}

	@Test
	public void updateObjectType() throws DAOException
	{
		ObjectType objectType = ObjectType.find.findByCode("Test");
		objectType.code="UpdateTest";
		objectType.update();
		objectType =  ObjectType.find.findById(objectType.id);
		Assert.assertTrue(objectType.code.equals("UpdateTest"));
		checkObjectType(objectType);

	}
	private void checkObjectType(ObjectType type)
	{
		Assert.assertNotNull(type);
		Assert.assertNotNull(type.id);
		Assert.assertNotNull(type.code);
		Assert.assertNotNull(type.generic);
	}

	@Test
	public void testType() throws DAOException
	{
		ObjectType type = ObjectType.find.findByCode("Experiment");
		checkObjectType(type);
	}



	@Test
	public void testAllTypes() throws DAOException
	{
		List<ObjectType> types = ObjectType.find.findAll();
		Assert.assertNotNull(types.size()>0);
		for(ObjectType type : types){
			checkObjectType(type);
		}
	}

	@Test
	public void testDeleteType() throws DAOException
	{
		ObjectType type = ObjectType.find.findByCode("UpdateTest");
		type.remove();
		ObjectType objectType = ObjectType.find.findByCode("UpdateTest");
		Assert.assertNull(objectType);
	}

	/**
	 * TEST RESOLUTION
	 * @throws DAOException 
	 */
	@Test
	public void saveResolution() throws DAOException
	{
		Resolution resolution = createResolution("Resol1", "Resol1");
		resolution.id = resolution.save();
		resolution=Resolution.find.findById(resolution.id);
		checkResolution(resolution);
	}

	@Test
	public void updateResolution() throws DAOException
	{
		Resolution resolution = Resolution.find.findByCode("Resol1");
		checkResolution(resolution);
		resolution.name="updateResol1";
		resolution.update();
		resolution = Resolution.find.findById(resolution.id);
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
	 * TEST STATE_CATEGORY
	 * @throws DAOException 
	 */
	@Test
	public void saveStateCategory() throws DAOException
	{
		StateCategory stateCategory = createStateCategory("catState1", "catState1");
		stateCategory.id=stateCategory.save();
		stateCategory=StateCategory.find.findById(stateCategory.id);
		checkAbstractCategory(stateCategory);
	}

	@Test
	public void updateStateCategory() throws DAOException
	{
		StateCategory stateCategory = StateCategory.find.findByCode("catState1");
		checkAbstractCategory(stateCategory);
		Assert.assertTrue(stateCategory.code.equals("catState1"));
		stateCategory.name="updateCatStat1";
		stateCategory.update();
		stateCategory = StateCategory.find.findById(stateCategory.id);
		checkAbstractCategory(stateCategory);
		Assert.assertTrue(stateCategory.name.equals("updateCatStat1"));
	}
	private StateCategory createStateCategory(String code, String name)
	{
		StateCategory stateCategory = new StateCategory();
		stateCategory.code=code;
		stateCategory.name=name;
		return stateCategory;

	}
	/**
	 * TEST STATE
	 * @throws DAOException 
	 */
	@Test
	public void saveState() throws DAOException
	{
		//StateCategory stateCategory = StateCategory.find.findByCode("catState1");
		//Assert.assertNotNull(stateCategory);
		StateCategory stateCategory = createStateCategory("catState2", "catState2");
		State state = createState("state1", "state1", 1, true,"experiment",stateCategory);
		state.id = state.save();
		state=State.find.findById(state.id);
		checkState(state);

	}

	//////////@Test
	public void updateState() throws DAOException
	{
		State state = State.find.findByCode("state1");
		checkState(state);
		Assert.assertTrue(state.code.equals("state1"));
		state.name="updateState1";
		state.update();
		state = State.find.findById(state.id);
		checkState(state);
		Assert.assertTrue(state.name.equals("updateState1"));
	}


	@Test
	public void testStateAll() throws DAOException
	{
		List<State> states = State.find.findAll();
		Assert.assertNotNull(states);
		Assert.assertTrue(states.size()>0);
		for(State state : states){
			Assert.assertNotNull(state.id);
		}
	}

	private State createState(String code, String name, Integer priority, boolean active, String level, StateCategory stateCategory)
	{
		State state = new State();
		state.code=code;
		state.name=name;
		state.priority=priority;
		state.active=active;
		state.level=level;
		state.stateCategory=stateCategory;
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
		Assert.assertNotNull(state.level);
		checkAbstractCategory(state.stateCategory);

	}


	/**
	 * TEST MEASURE_CATEGORY
	 * @throws DAOException 
	 */
	@Test
	public void saveMeasureCategory() throws DAOException
	{
		MeasureCategory measureCategory = createMeasureCategory("cat1", "cat1");
		List<MeasureValue> measureValues = new ArrayList<MeasureValue>();
		measureValues.add(createMeasureValue("value1", "value1", true,measureCategory));
		measureValues.add(createMeasureValue("value2", "value2", false,measureCategory));
		measureCategory.measurePossibleValues=measureValues;
		measureCategory.id = measureCategory.save();
		checkMeasureCategory(measureCategory);

	}

	@Test
	public void removeMeasureCategory() throws DAOException
	{
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat1");
		List<MeasureValue> measureValues = measureCategory.measurePossibleValues;
		measureCategory.remove();
		measureCategory = MeasureCategory.find.findByCode("cat1");
		Assert.assertNull(measureCategory);
		//Check measure Values
		for(MeasureValue measureValue : measureValues){
			Assert.assertNull(MeasureValue.find.findById(measureValue.id));
		}
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

	private MeasureValue createMeasureValue(String code, String value, boolean defaultValue, MeasureCategory measureCategory)
	{
		MeasureValue measureValue = new MeasureValue();
		measureValue.code=code;
		measureValue.value=value;
		measureValue.defaultValue=defaultValue;
		measureValue.measureCategory=measureCategory;
		return measureValue;
	}

	private void checkMeasureValue(MeasureValue measureValue)
	{
		Assert.assertNotNull(measureValue);
		Assert.assertNotNull(measureValue.id);
		Assert.assertNotNull(measureValue.code);
		Assert.assertNotNull(measureValue.value);
		Assert.assertNotNull(measureValue.defaultValue);
	}

	/**
	 * TEST REAGENT_TYPE
	 * @throws DAOException 
	 */
	@Test
	public void saveReagentType() throws DAOException
	{
		ReagentType reagentType = new ReagentType();
		StateCategory stateCategory = StateCategory.find.findByCode("catState1");
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state1"));
		states.add(createState("state2", "state2", 2, true,"experiment",stateCategory));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		resolutions.add(createResolution("resol2", "resol2"));
		ObjectType objectType = ObjectType.find.findByCode("Reagent");
		MeasureCategory measureCategory = createMeasureCategory("cat2", "cat2");
		MeasureValue measureValue = createMeasureValue("value2", "value2", true, measureCategory);
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value3","value3", true));
		possibleValues.add(createValue("value4","value4", false));
		List<Value> possibleValues2 = new ArrayList<Value>();
		possibleValues2.add(createValue("value5","value5", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop1", "prop1", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		propertiesDefinitions.add(createPropertyDefinition("prop2", "prop2", true, true, "default", "descProp2", "format2", 2, "in", "content", true, true, "type2", measureCategory, measureValue, measureValue, possibleValues2));
		CommonInfoType commonInfoType = createCommonInfoType("reagent1", "reagent1", "reagent1", states, resolutions, propertiesDefinitions, objectType);
		reagentType.setCommonInfoType(commonInfoType);
		reagentType.id = reagentType.save();
		reagentType=ReagentType.find.findByCode(reagentType.code);
		checkCommonInfoType(reagentType);
	}

	@Test
	public void updateReagentType() throws DAOException
	{
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentType.name="updateReagent1";
		reagentType.update();
		reagentType = ReagentType.find.findByCode("reagent1");
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
		Assert.assertNotNull(propertyDefinition.displayMeasureValue);
		checkMeasureValue(propertyDefinition.displayMeasureValue);
		Assert.assertNotNull(propertyDefinition.possibleValues);
		Assert.assertTrue(propertyDefinition.possibleValues.size()>0);
		for(Value value : propertyDefinition.possibleValues){
			checkValue(value);
		}
	}

	private PropertyDefinition createPropertyDefinition(String code, String name, boolean active, boolean choiceInList, String defaultValue, String description, String displayFormat, Integer displayOrder, String inOut, String level, boolean propagation, boolean required, String type,
			MeasureCategory measureCategory,MeasureValue measureValue, MeasureValue displayMeasureValue, List<Value> possibleValues)
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
		propertyDefinition.displayMeasureValue = displayMeasureValue;
		propertyDefinition.possibleValues=possibleValues;
		return propertyDefinition;
	}

	private Value createValue(String code, String value, boolean defaultValue)
	{
		Value newValue = new Value();
		newValue.code=code;
		newValue.value=value;
		newValue.defaultValue=defaultValue;
		return newValue;
	}

	private void checkValue(Value value)
	{
		Assert.assertNotNull(value);
		Assert.assertNotNull(value.code);
		Assert.assertNotNull(value.value);
		Assert.assertNotNull(value.defaultValue);
	}

	/**
	 * TEST PROTOCOL_CATEGORY
	 * @throws DAOException 
	 */
	@Test
	public void saveProtocolCategory() throws DAOException
	{
		ProtocolCategory protocolCategory = createProtocolCategory("protoCat1", "protoCat1");
		protocolCategory.id = protocolCategory.save();
		protocolCategory = ProtocolCategory.find.findById(protocolCategory.id);
		checkAbstractCategory(protocolCategory);
	}
	@Test
	public void updateProtocolCategory() throws DAOException
	{
		ProtocolCategory protocolCategory = ProtocolCategory.find.findByCode("protoCat1");
		protocolCategory.name="updateProtoCat1";
		protocolCategory.update();
		protocolCategory = ProtocolCategory.find.findByCode("protoCat1");
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
	 * @throws DAOException 
	 */
	@Test
	public void saveProtocol() throws DAOException
	{
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentTypes.add(reagentType);
		Protocol protocol = createProtocol("proto1","proto1", "path1", "V1", createProtocolCategory("protoCat2", "protoCat2"), reagentTypes);
		protocol.id = protocol.save();
		protocol = Protocol.find.findById(protocol.id);
		checkProtocol(protocol);
	}

	@Test
	public void updateProtocol() throws DAOException
	{
		Protocol protocol = Protocol.findByName("proto1");
		checkProtocol(protocol);
		protocol.name="updateProto1";
		protocol.update();
		protocol = Protocol.find.findById(protocol.id);
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
	private Protocol createProtocol(String code, String name, String filePath, String version, ProtocolCategory protocolCategory, List<ReagentType> reagentTypes)
	{
		Protocol protocol = new Protocol();
		protocol.name=name;
		protocol.code=code;
		protocol.filePath=filePath;
		protocol.version=version;
		protocol.protocolCategory=protocolCategory;
		protocol.reagentTypes=reagentTypes;
		return protocol;

	}

	/**
	 * TEST CONTAINER_SUPPORT_CATEGORY
	 * @throws DAOException 
	 */
	@Test
	public void saveContainerSupportCategory() throws DAOException
	{
		ContainerSupportCategory containerSupportCategory = createContainerSupportCategory("support1", "support1", 10, 10, 10);
		containerSupportCategory.id = containerSupportCategory.save();
		containerSupportCategory = ContainerSupportCategory.find.findByCode(containerSupportCategory.code);
		checkContainerSupportCategory(containerSupportCategory);
	}
	@Test
	public void updateContainerSupportCategory() throws DAOException
	{
		ContainerSupportCategory containerSupportCategory = ContainerSupportCategory.find.findByCode("support1");
		checkContainerSupportCategory(containerSupportCategory);
		containerSupportCategory.name="updateSupport1";
		containerSupportCategory.nbLine=5;
		containerSupportCategory.update();
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support1");
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
	 * @throws DAOException 
	 */
	@Test
	public void saveInstrumentCategory() throws DAOException
	{
		List<ContainerSupportCategory> inContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		inContainerSupportCategories.add(ContainerSupportCategory.find.findByCode("support1"));
		List<ContainerSupportCategory> outContainerSupportCategories = new ArrayList<ContainerSupportCategory>();
		outContainerSupportCategories.add(createContainerSupportCategory("support2", "support2", 5, 10, 10));
		InstrumentCategory instrumentCategory = createInstrumentCategory("InstCat1", "InstCat1", 1, inContainerSupportCategories, 1, outContainerSupportCategories);
		instrumentCategory.id = instrumentCategory.save();
		instrumentCategory = InstrumentCategory.find.findById(instrumentCategory.id);
		checkInstrumentCategory(instrumentCategory);
		Assert.assertTrue(instrumentCategory.inContainerSupportCategories.size()==1);
		Assert.assertTrue(instrumentCategory.outContainerSupportCategories.size()==1);
	}
	@Test
	public void updateInstrumentCategory() throws DAOException
	{
		InstrumentCategory instrumentCategory = InstrumentCategory.find.findByCode("InstCat1");
		checkInstrumentCategory(instrumentCategory);
		instrumentCategory.name="UpdateInstCat1";
		instrumentCategory.inContainerSupportCategories.add(createContainerSupportCategory("support3", "support3", 10, 10, 10));
		instrumentCategory.outContainerSupportCategories.add(createContainerSupportCategory("support4", "support4", 10, 10, 10));
		instrumentCategory.update();
		instrumentCategory = InstrumentCategory.find.findById(instrumentCategory.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveInstrumentUsedType() throws DAOException
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Instrument");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value6","value6", true));
		possibleValues.add(createValue("value7","value7", false));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop3", "prop3", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
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
		instrumentUsedType.id = instrumentUsedType.save();
		instrumentUsedType = InstrumentUsedType.find.findById(instrumentUsedType.id);
		checkInstrumentUsedType(instrumentUsedType);

	}

	@Test
	public void updateInstrumentUsedType() throws DAOException
	{
		InstrumentUsedType instrumentUsedType = InstrumentUsedType.find.findByCode("inst1");
		checkInstrumentUsedType(instrumentUsedType);
		instrumentUsedType.name="updateInst1";
		instrumentUsedType.instruments.add(createInstrument("inst2", "inst2"));
		instrumentUsedType.update();
		instrumentUsedType=InstrumentUsedType.find.findById(instrumentUsedType.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void savePurificationMethodType() throws DAOException
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Purification");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value8", "value8", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop4", "prop4", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("purif1", "purif1", "purif1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Protocol.findByName("updateProto1"));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentTypes.add(reagentType);
		protocols.add(createProtocol("proto2","proto2", "path2", "V2", createProtocolCategory("protoCat3", "protoCat3"), reagentTypes));

		PurificationMethodType purificationMethodType = createPurificationMethodType(commonInfoType, instrumentUsedTypes, protocols);
		purificationMethodType.id = purificationMethodType.save();
		purificationMethodType = PurificationMethodType.find.findById(purificationMethodType.id);
		checkAbstractExperiment(purificationMethodType);
	}

	@Test
	public void updatePurificationMethodType() throws DAOException
	{
		PurificationMethodType purificationMethodType = PurificationMethodType.find.findByCode("purif1");
		checkAbstractExperiment(purificationMethodType);
		purificationMethodType.name="updatePurif1";

		//Add experiment type
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Instrument");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value9", "value9", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop5", "prop5", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("inst3", "inst3", "inst3", states, resolutions, propertiesDefinitions, objectType);

		InstrumentCategory instrumentCategory = InstrumentCategory.find.findByCode("InstCat1");
		//Get instrument
		List<Instrument> instruments = new ArrayList<Instrument>();
		instruments.add(createInstrument("inst3", "inst3"));


		purificationMethodType.instrumentUsedTypes.add(createInstrumentUsedType(commonInfoType, instrumentCategory, instruments));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentTypes.add(reagentType);

		purificationMethodType.protocols.add(createProtocol("proto3","proto3", "path3", "V2", createProtocolCategory("protoCat4", "protoCat4"), reagentTypes));

		purificationMethodType.update();
		purificationMethodType = PurificationMethodType.find.findById(purificationMethodType.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveQualityControlType() throws DAOException
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("ControlQuality");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value10","value10", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop6", "prop6", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("qc1", "qc1", "qc1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Protocol.findByName("updateProto1"));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentTypes.add(reagentType);

		QualityControlType qualityControlType = createQualityControlType(commonInfoType, instrumentUsedTypes, protocols);
		qualityControlType.id = qualityControlType.save();
		qualityControlType = QualityControlType.find.findById(qualityControlType.id);
		checkAbstractExperiment(qualityControlType);
	}
	@Test
	public void updateQualityControlType() throws DAOException
	{
		QualityControlType qualityControlType = QualityControlType.find.findByCode("qc1");
		checkAbstractExperiment(qualityControlType);
		qualityControlType.name="updateQC1";

		//Add experiment type
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state1"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Instrument");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value11","value11", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop7", "prop7", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("inst4", "inst4", "inst4", states, resolutions, propertiesDefinitions, objectType);

		//Get instrumentCategory
		InstrumentCategory instrumentCategory = InstrumentCategory.find.findByCode("InstCat1");

		//Get instrument
		List<Instrument> instruments = new ArrayList<Instrument>();
		instruments.add(createInstrument("inst4", "inst4"));

		qualityControlType.instrumentUsedTypes.add(createInstrumentUsedType(commonInfoType, instrumentCategory, instruments));
		List<ReagentType> reagentTypes = new ArrayList<ReagentType>();
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentTypes.add(reagentType);
		qualityControlType.protocols.add(createProtocol("proto4","proto4", "path4", "V2", ProtocolCategory.find.findByCode("protoCat2"), reagentTypes));

		qualityControlType.update();
		qualityControlType = QualityControlType.find.findById(qualityControlType.id);
		checkAbstractExperiment(qualityControlType);
		Assert.assertTrue(qualityControlType.name.equals("updateQC1"));
		Assert.assertTrue(qualityControlType.protocols.size()==2);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveExperimentCategory() throws DAOException
	{
		ExperimentCategory experimentCategory = createExperimentCategory("expCat1", "expCat2");
		experimentCategory.id=experimentCategory.save();
		experimentCategory=ExperimentCategory.find.findById(experimentCategory.id);
		checkAbstractCategory(experimentCategory);

	}
	@Test
	public void updateExperimentCategory() throws DAOException
	{
		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode("expCat1");
		checkAbstractCategory(experimentCategory);
		experimentCategory.name="updateExpCat1";
		experimentCategory.update();
		experimentCategory = ExperimentCategory.find.findById(experimentCategory.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveExperimentType() throws DAOException
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("ControlQuality");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value12","value12", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop8", "prop8", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("exp1", "exp1", "exp1", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Protocol.findByName("updateProto1"));

		PurificationMethodType purif = PurificationMethodType.find.findByCode("purif1");
		List<PurificationMethodType> purificationMethodTypes = new ArrayList<PurificationMethodType>();
		purificationMethodTypes.add(purif);

		QualityControlType qc = QualityControlType.find.findByCode("qc1");
		List<QualityControlType> qualityControlTypes = new ArrayList<QualityControlType>();
		qualityControlTypes.add(qc);

		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode("expCat1");

		ExperimentType experimentType = createExperimentType(commonInfoType, protocols, instrumentUsedTypes, experimentCategory, new ArrayList<ExperimentType>(),
				true, true, purificationMethodTypes, true, true, qualityControlTypes);
		experimentType.id = experimentType.save();
		experimentType=ExperimentType.find.findById(experimentType.id);
		checkExperimentType(experimentType);
	}

	@Test
	public void updateExperimentType() throws DAOException
	{
		ExperimentType experimentType = ExperimentType.find.findByCode("exp1");
		checkExperimentType(experimentType);
		experimentType.name="updateExp1";
		experimentType.doPurification=false;
		//Create purification
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Purification");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value13","value13", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop9", "prop9", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("purif2", "purif2", "purif2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Protocol.findByName("updateProto1"));

		PurificationMethodType purificationMethodType = createPurificationMethodType(commonInfoType, instrumentUsedTypes, protocols);
		experimentType.possiblePurificationMethodTypes.add(purificationMethodType);
		//Create quality control
		//Create commonInfoType
		states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		objectType = ObjectType.find.findByCode("ControlQuality");

		possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value14","value14", true));
		propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop10", "prop10", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		commonInfoType = createCommonInfoType("qc2", "qc2", "qc2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		protocols = new ArrayList<Protocol>();
		protocols.add(Spring.getBeanOfType(ProtocolDAO.class).findByName("updateProto1"));
		QualityControlType qualityControlType = createQualityControlType(commonInfoType, instrumentUsedTypes, protocols);
		experimentType.possibleQualityControlTypes.add(qualityControlType);
		experimentType.update();
		experimentType=ExperimentType.find.findById(experimentType.id);
		checkExperimentType(experimentType);
		Assert.assertTrue(experimentType.name.equals("updateExp1"));
		Assert.assertFalse(experimentType.doPurification);
		Assert.assertTrue(experimentType.possiblePurificationMethodTypes.size()==2);
		Assert.assertTrue(experimentType.possibleQualityControlTypes.size()==2);

	}

	@Test
	public void savePreviousExperiment() throws DAOException
	{
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("ControlQuality");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value15","value15", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop11", "prop11", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("exp2", "exp2", "exp2", states, resolutions, propertiesDefinitions, objectType);

		//Create list instrument 
		List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
		instrumentUsedTypes.add(InstrumentUsedType.find.findByCode("inst1"));

		//Create liste protocol
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocols.add(Protocol.findByName("updateProto1"));

		PurificationMethodType purif = PurificationMethodType.find.findByCode("purif1");
		List<PurificationMethodType> purificationMethodTypes = new ArrayList<PurificationMethodType>();
		purificationMethodTypes.add(purif);

		QualityControlType qc = QualityControlType.find.findByCode("qc1");
		List<QualityControlType> qualityControlTypes = new ArrayList<QualityControlType>();
		qualityControlTypes.add(qc);

		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode("expCat1");

		List<ExperimentType> previousExperiment = new ArrayList<ExperimentType>();
		previousExperiment.add(ExperimentType.find.findByCode("exp1"));
		ExperimentType experimentType = createExperimentType(commonInfoType, protocols, instrumentUsedTypes, experimentCategory, new ArrayList<ExperimentType>(),
				true, true, purificationMethodTypes, true, true, qualityControlTypes);
		experimentType.previousExperimentTypes=previousExperiment;
		experimentType.id = experimentType.save();
		checkExperimentType(experimentType);
		experimentType = ExperimentType.find.findByCode("exp2");
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
	 * @throws DAOException 
	 */
	@Test
	public void saveProcessCategory() throws DAOException
	{
		ProcessCategory processCategory = createProcessCategory("processCat1", "processCat1");
		processCategory.id = processCategory.save();
		processCategory = ProcessCategory.find.findById(processCategory.id);
		checkAbstractCategory(processCategory);
	}
	@Test
	public void updateProcessCategory() throws DAOException
	{
		ProcessCategory processCategory = ProcessCategory.find.findByCode("processCat1");
		checkAbstractCategory(processCategory);
		processCategory.name="updateProcessCat1";
		processCategory.update();
		processCategory = ProcessCategory.find.findById(processCategory.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveProcessType() throws DAOException
	{
		List<ExperimentType> experimentTypes = new ArrayList<ExperimentType>();
		ExperimentType expType = ExperimentType.find.findByCode("exp1");
		experimentTypes.add(expType);
		ProcessCategory processCategory = ProcessCategory.find.findByCode("processCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Process");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value16","value16", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop12", "prop12", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("process1", "process1", "process1", states, resolutions, propertiesDefinitions, objectType);
		ProcessType processType = createProcessType(commonInfoType, experimentTypes, processCategory,expType,expType,expType);
		processType.id = processType.save();
		processType = ProcessType.find.findById(processType.id);
		checkProcessType(processType);
	}
	@Test
	public void updateProcessType() throws DAOException
	{
		ProcessType processType = ProcessType.find.findByCode("process1");
		checkProcessType(processType);
		processType.name="updateProcess1";
		ExperimentType exp2 = ExperimentType.find.findByCode("exp2");
		processType.experimentTypes.add(exp2);
		processType.update();
		processType = ProcessType.find.findById(processType.id);
		checkProcessType(processType);
		Assert.assertTrue(processType.name.equals("updateProcess1"));
		Assert.assertTrue(processType.experimentTypes.size()==2);

	}

	@Test
	public void removeProcessType() throws DAOException
	{
		ProcessType processType = ProcessType.find.findByCode("process1");
		processType.remove();
		Assert.assertNull(ProcessType.find.findByCode("process1"));
	}

	private ProcessType createProcessType(CommonInfoType commonInfoType, List<ExperimentType> experimentTypes, ProcessCategory processCategory, 
			ExperimentType voidExpType, ExperimentType firstExpType, ExperimentType lastExpType)
	{
		ProcessType processType = new ProcessType();
		processType.setCommonInfoType(commonInfoType);
		processType.experimentTypes=experimentTypes;
		processType.processCategory=processCategory;
		processType.voidExperimentType=voidExpType;
		processType.firstExperimentType=firstExpType;
		processType.lastExperimentType=lastExpType;
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
		Assert.assertNotNull(processType.voidExperimentType.id);
		Assert.assertNotNull(processType.firstExperimentType.id);
		Assert.assertNotNull(processType.lastExperimentType.id);
	}

	/**
	 * TEST PROJECT_CATEGORY
	 * @throws DAOException 
	 */
	@Test
	public void saveProjectCategory() throws DAOException
	{
		ProjectCategory projectCategory = createProjectCategory("projectCat1", "projectCat1");
		projectCategory.id = projectCategory.save();
		projectCategory = ProjectCategory.find.findById(projectCategory.id);
		checkAbstractCategory(projectCategory);
	}
	@Test
	public void updateProjectCategory() throws DAOException
	{
		ProjectCategory projectCategory = ProjectCategory.find.findByCode("projectCat1");
		checkAbstractCategory(projectCategory);
		projectCategory.name="updateProjectCat1";
		projectCategory.update();
		projectCategory = ProjectCategory.find.findById(projectCategory.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveProjectType() throws DAOException
	{
		ProjectCategory projectCategory = ProjectCategory.find.findByCode("projectCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Project");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value17","value17", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop13", "prop13", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("project1", "project1", "project1", states, resolutions, propertiesDefinitions, objectType);
		ProjectType projectType = createProjectType(commonInfoType, projectCategory);
		projectType.id = projectType.save();
		projectType = ProjectType.find.findById(projectType.id);
		checkProjectType(projectType);
	}
	@Test
	public void updateProjectType() throws DAOException
	{
		ProjectType projectType = ProjectType.find.findByCode("project1");
		checkProjectType(projectType);
		projectType.name="updateProject1";
		projectType.update();
		projectType = ProjectType.find.findById(projectType.id);
		checkProjectType(projectType);
		Assert.assertTrue(projectType.name.equals("updateProject1"));
	}

	@Test
	public void removeProjectType() throws DAOException
	{
		ProjectType projectType = ProjectType.find.findByCode("project1");
		projectType.remove();
		Assert.assertNull(ProjectType.find.findByCode("project1"));
	}
	@Test
	public void removeProjectCategory() throws DAOException
	{
		ProjectCategory projectCategory = ProjectCategory.find.findByCode("projectCat1");
		projectCategory.remove();
		Assert.assertNull(ProjectCategory.find.findByCode("projectCat1"));
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
	 * @throws DAOException 
	 */
	@Test
	public void saveSampleCategory() throws DAOException
	{
		SampleCategory sampleCategory = createSampleCategory("sampleCat1", "sampleCat1");
		sampleCategory.id = sampleCategory.save();
		sampleCategory = SampleCategory.find.findById(sampleCategory.id);
		checkAbstractCategory(sampleCategory);
	}
	@Test
	public void updateSampleCategory() throws DAOException
	{
		SampleCategory sampleCategory = SampleCategory.find.findByCode("sampleCat1");
		checkAbstractCategory(sampleCategory);
		sampleCategory.name="updateSampleCat1";
		sampleCategory.update();
		sampleCategory = SampleCategory.find.findById(sampleCategory.id);
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
	 * @throws DAOException 
	 */
	@Test
	public void saveSampleType() throws DAOException
	{
		SampleCategory sampleCategory = SampleCategory.find.findByCode("sampleCat1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Project");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value18","value18", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop14", "prop14", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("sample1", "sample1", "sample1", states, resolutions, propertiesDefinitions, objectType);
		SampleType sampleType = createSampleType(commonInfoType, sampleCategory);
		sampleType.id = sampleType.save();
		sampleType = SampleType.find.findById(sampleType.id);
		checkSampleType(sampleType);
	}
	@Test
	public void updateSampleType() throws DAOException
	{
		SampleType sampleType = SampleType.find.findByCode("sample1");
		checkSampleType(sampleType);
		sampleType.name="updateSample1";
		sampleType.update();
		sampleType = SampleType.find.findById(sampleType.id);
		checkSampleType(sampleType);
		Assert.assertTrue(sampleType.name.equals("updateSample1"));
	}

	@Test
	public void removeSampleType() throws DAOException
	{
		SampleType sampleType = SampleType.find.findByCode("sample1");
		sampleType.remove();
		Assert.assertNull(SampleType.find.findByCode("sample1"));
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

	@Test
	public void saveImportCategory() throws DAOException
	{
		ImportCategory importCategory = createImportCategory("import1", "import1");
		importCategory.id = importCategory.save();
		importCategory = ImportCategory.find.findById(importCategory.id);
		checkAbstractCategory(importCategory);
	}

	@Test
	public void updateImportCategory() throws DAOException
	{
		ImportCategory importCategory = ImportCategory.find.findByCode("import1");
		checkAbstractCategory(importCategory);
		importCategory.name="updateImport1";
		importCategory.update();
		importCategory = ImportCategory.find.findById(importCategory.id);
		checkAbstractCategory(importCategory);
		Assert.assertTrue(importCategory.name.equals("updateImport1"));
	}

	@Test
	public void saveImportType() throws DAOException
	{
		ImportCategory importCategory = ImportCategory.find.findByCode("import1");
		//Create commonInfoType
		List<State> states = new ArrayList<State>();
		states.add(State.find.findByCode("state2"));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(Resolution.find.findByCode("resol1"));
		ObjectType objectType = ObjectType.find.findByCode("Import");
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		MeasureValue measureValue = MeasureValue.findByValue("value2");
		List<Value> possibleValues = new ArrayList<Value>();
		possibleValues.add(createValue("value19","value19", true));
		List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
		propertiesDefinitions.add(createPropertyDefinition("prop15", "prop15", true, true, "default", "descProp1", "format1", 1, "in", "content", true, true, "type1", measureCategory, measureValue, measureValue, possibleValues));
		CommonInfoType commonInfoType = createCommonInfoType("import1", "import1", "import1", states, resolutions, propertiesDefinitions, objectType);
		ImportType importType = createImportType(commonInfoType, importCategory);
		importType.id = importType.save();
		importType = ImportType.find.findById(importType.id);
		checkImportType(importType);
	}
	@Test
	public void updateImportType() throws DAOException
	{
		ImportType importType = ImportType.find.findByCode("import1");
		checkImportType(importType);
		importType.name="updateImport1";
		importType.update();
		importType = ImportType.find.findById(importType.id);
		checkImportType(importType);
		Assert.assertTrue(importType.name.equals("updateImport1"));
	}
	@Test
	public void removeImportType() throws DAOException
	{
		ImportType importType = ImportType.find.findByCode("import1");
		importType.remove();
		Assert.assertNull(ImportType.find.findByCode("import1"));
	}

	private ImportType createImportType(CommonInfoType commonInfoType,  ImportCategory importCategory)
	{
		ImportType importType = new ImportType();
		importType.setCommonInfoType(commonInfoType);
		importType.importCategory=importCategory;
		return importType;
	}

	private void checkImportType(ImportType importType)
	{
		Assert.assertNotNull(importType);
		checkCommonInfoType(importType);
		checkAbstractCategory(importType.importCategory);
	}
	
	@Test
	public void saveContainerCategory() throws DAOException
	{
		ContainerCategory containerCategory = createContainerCategory("container1", "container1");
		containerCategory.id = containerCategory.save();
		containerCategory = ContainerCategory.find.findById(containerCategory.id);
		checkAbstractCategory(containerCategory);
	}
	
	
	@Test
	public void updateContainerCategory() throws DAOException
	{
		ContainerCategory containerCategory = ContainerCategory.find.findByCode("container1");
		checkAbstractCategory(containerCategory);
		containerCategory.name="updateContainer1";
		containerCategory.update();
		containerCategory = ContainerCategory.find.findById(containerCategory.id);
		checkAbstractCategory(containerCategory);
		Assert.assertTrue(containerCategory.name.equals("updateContainer1"));
	}
	
	@Test
	public void removeContainerCategory() throws DAOException
	{
		ContainerCategory containerCategory = ContainerCategory.find.findByCode("container1");
		containerCategory.remove();
		Assert.assertNull(ContainerCategory.find.findByCode("container1"));
	}
	
	public ContainerCategory createContainerCategory(String name, String code)
	{
		ContainerCategory containerCategory = new ContainerCategory();
		containerCategory.name=name;
		containerCategory.code=code;
		return containerCategory;
	}
	
	@Test
	public void removeImportCategory() throws DAOException
	{
		ImportCategory importCategory = ImportCategory.find.findByCode("import1");
		importCategory.remove();
		Assert.assertNull(ImportCategory.find.findByCode("sampleCat1"));
	}

	public ImportCategory createImportCategory(String name, String code)
	{
		ImportCategory importCategory = new ImportCategory();
		importCategory.name=name;
		importCategory.code=code;
		return importCategory;
	}
	@Test
	public void removeResolution() throws DAOException
	{
		Resolution resol = Resolution.find.findByCode("resol1");
		resol.remove();
		Assert.assertNull(Resolution.find.findByCode("resol1"));

	}

	@Test
	public void removeState() throws DAOException
	{
		State state = State.find.findByCode("state1");
		state.remove();
		state = State.find.findByCode("state1");
		Assert.assertNull(state);
	}

	@Test
	public void removeInstrumentUsedType() throws DAOException
	{
		InstrumentUsedType instrumentUsedType = InstrumentUsedType.find.findByCode("inst1");
		instrumentUsedType.remove();
		Assert.assertNull(InstrumentUsedType.find.findByCode("inst1"));

		//Remove all instrument
		instrumentUsedType = InstrumentUsedType.find.findByCode("inst3");
		instrumentUsedType.remove();
		instrumentUsedType = InstrumentUsedType.find.findByCode("inst4");
		instrumentUsedType.remove();

	}

	@Test
	public void removePurificationMethodType() throws DAOException
	{
		PurificationMethodType purificationMethodType = PurificationMethodType.find.findByCode("purif1");
		purificationMethodType.remove();
		Assert.assertNull(PurificationMethodType.find.findByCode("purif1"));
		//Remove all purif
		purificationMethodType = PurificationMethodType.find.findByCode("purif2");
		purificationMethodType.remove();
	}

	@Test
	public void removeQualityControlType() throws DAOException
	{
		QualityControlType qualityControlType = QualityControlType.find.findByCode("qc1");
		qualityControlType.remove();
		Assert.assertNull(QualityControlType.find.findByCode("qc1"));

		//Remove all quality
		qualityControlType = QualityControlType.find.findByCode("qc2");
		qualityControlType.remove();
	}

	@Test
	public void removeExperimentType() throws DAOException
	{
		ExperimentType experimentType = ExperimentType.find.findByCode("exp1");
		experimentType.remove();
		Assert.assertNull(ExperimentType.find.findByCode("exp1"));
		//Remove all experiment
		experimentType = ExperimentType.find.findByCode("exp2");
		experimentType.remove();
	}


	@Test
	public void removeExperimentCategory() throws DAOException
	{
		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode("expCat1");
		experimentCategory.remove();
		Assert.assertNull(ExperimentCategory.find.findByCode("expCat1"));
	}


	@Test
	public void removeProcessCategory() throws DAOException
	{
		ProcessCategory processCategory = ProcessCategory.find.findByCode("processCat1");
		processCategory.remove();
		Assert.assertNull(ProcessCategory.find.findByCode("processCat1"));
	}

	@Test
	public void removeSampleCategory() throws DAOException
	{
		SampleCategoryDAO sampleCategoryDAO = Spring.getBeanOfType(SampleCategoryDAO.class);
		SampleCategory sampleCategory = sampleCategoryDAO.findByCode("sampleCat1");
		sampleCategoryDAO.remove(sampleCategory);
		Assert.assertNull(sampleCategoryDAO.findByCode("sampleCat1"));
	}

	@Test
	public void removeReagentType() throws DAOException
	{
		ReagentType reagentType = ReagentType.find.findByCode("reagent1");
		reagentType.remove();
		Assert.assertNull(ReagentType.find.findByCode("reagent1"));

		//Remove state2
		State state = State.find.findByCode("state2");
		state.remove();
		//remove resol2
		Resolution resolution = Resolution.find.findByCode("resol2");
		resolution.remove();
		//remove cat2
		MeasureCategory measureCategory = MeasureCategory.find.findByCode("cat2");
		measureCategory.remove();

	}

	@Test
	public void removeContainerSupportCategory() throws DAOException
	{
		ContainerSupportCategory containerSupportCategory = ContainerSupportCategory.find.findByCode("support1");
		containerSupportCategory.remove();
		Assert.assertNull(ContainerSupportCategory.find.findByCode("support1"));
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support2");
		containerSupportCategory.remove();
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support3");
		containerSupportCategory.remove();
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support4");
		containerSupportCategory.remove();
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support5");
		containerSupportCategory.remove();
		containerSupportCategory = ContainerSupportCategory.find.findByCode("support6");
		containerSupportCategory.remove();
	}

	@Test
	public void removeInstrumentCategory() throws DAOException
	{
		InstrumentCategory instrumentCategory = InstrumentCategory.find.findByCode("InstCat1");
		instrumentCategory.remove();
		Assert.assertNull(InstrumentCategory.find.findByCode("InstCat1"));
		instrumentCategory = InstrumentCategory.find.findByCode("InstCat2");
		instrumentCategory.remove();
	}

	@Test
	public void removeProtocol() throws DAOException
	{
		Protocol protocol = Protocol.findByName("updateProto1");
		protocol.remove();
		Assert.assertNull(Protocol.findByName("updateProto1"));
		protocol = Protocol.find.findByCode("proto2");
		protocol.remove();
		protocol = Protocol.find.findByCode("proto3");
		protocol.remove();
		protocol = Protocol.find.findByCode("proto4");
		protocol.remove();
	}

	@Test
	public void removeProtocolCategory() throws DAOException
	{
		ProtocolCategory protocolCategory = ProtocolCategory.find.findByCode("protoCat1");
		protocolCategory.remove();
		Assert.assertNull(ProtocolCategory.find.findByCode("protoCat1"));
		protocolCategory = ProtocolCategory.find.findByCode("protoCat2");
		protocolCategory.remove();
		protocolCategory = ProtocolCategory.find.findByCode("protoCat3");
		protocolCategory.remove();
		protocolCategory = ProtocolCategory.find.findByCode("protoCat4");
		protocolCategory.remove();

	}

	@Test
	public void removeStateCategory() throws DAOException
	{
		StateCategory stateCategory = StateCategory.find.findByCode("catState1");
		stateCategory.remove();
		Assert.assertNull(StateCategory.find.findByCode("catState1"));
	}


}
