package data.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.Value;
import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.HelperObjects;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.validation.ValidationError;
import play.db.DB;
import play.modules.spring.Spring;

public class DataTypeHelper {

	
	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Boolean choiceInList,
			List<Value> possiblesValues , Class<?> type, String description, String  displayFormat, int displayOrder
			,boolean propagation, String inOut, String defaultValue 
			,String level, MeasureCategory measureCategory, MeasureValue measureValue) {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.code = keyCode;
		propertyDefinition.name = keyName;
		propertyDefinition.required = required;
		propertyDefinition.active = active;
		propertyDefinition.choiceInList = choiceInList;
		propertyDefinition.possibleValues = possiblesValues;
		propertyDefinition.type = type.getName();
		propertyDefinition.description=description;
		propertyDefinition.displayFormat=displayFormat;
		propertyDefinition.displayOrder=displayOrder;
		propertyDefinition.propagation=propagation;
		propertyDefinition.inOut=inOut;
		propertyDefinition.defaultValue=defaultValue;
		propertyDefinition.level="current";
		propertyDefinition.measureCategory=measureCategory;
		propertyDefinition.measureValue=measureValue;		

		return propertyDefinition;
	}


	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Class<?> type) {
		return getPropertyDefinition(keyCode, keyName, required, active, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,null,null);
	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, type);
	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type, Boolean required,MeasureCategory measureCategory,MeasureValue measureValue) {
		return getPropertyDefinition(keyCode, keyName, required, Boolean.TRUE, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,measureCategory,measureValue);

	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type,MeasureCategory measureCategory,MeasureValue measureValue) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,measureCategory,measureValue);

	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type,List<Value> possiblesValues) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,possiblesValues, type, null, null, 0,Boolean.FALSE,null,null,null,null,null);
	}


	public static PropertyDefinition getPropertyDefinition(String keyCode,
			String keyName, Boolean false1, Class<String> class1,
			List<Value> listIndex) {

		return getPropertyDefinition(keyCode, keyName, false1, Boolean.TRUE, Boolean.TRUE,listIndex, class1, null, null, 0,Boolean.FALSE,null,null,null,null,null);

	}



	public static CommonInfoType getCommonInfoType(String code,String name,String collectionName, List<State> variableStates,List<PropertyDefinition> propertyDefinitions, List<Resolution> resolutions,String objectType) throws DAOException {

		CommonInfoType commonInfoType=new CommonInfoType();
		commonInfoType.name=name;
		commonInfoType.code=code;
		commonInfoType.collectionName=collectionName;
		commonInfoType.variableStates = variableStates;
		commonInfoType.resolutions = resolutions;
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		ObjectTypeDAO objectTypeDAO=Spring.getBeanOfType(ObjectTypeDAO.class);
		commonInfoType.objectType=objectTypeDAO.findByCode(objectType);

		return commonInfoType;
	}


	public static SampleCategory getSampleCategory(String code,String name) throws DAOException{

		SampleCategory sampleCategory=SampleCategory.find.findByCode(code);
		System.err.println("SampleCategory "+sampleCategory); 
		if(sampleCategory==null){
			sampleCategory=new SampleCategory();
			sampleCategory.code=code;
			sampleCategory.name=name;}
		return sampleCategory;
	}


	public static State getState(String code){

		State state = null;
		try {
			state = State.find.findByCode(code);

			if(state==null){
				state = new State();
				state.active=true;
				state.code=code;
				state.name=code;
				state.priority=0;
				//state.save();
			}


		} catch (DAOException e) {

			e.printStackTrace();
		}
		return state;
	}

	public static Resolution getResolution(String code){

		Resolution resolution = null;
		try {
			resolution = Resolution.find.findByCode(code);

			if(resolution==null){
				resolution = new Resolution();
				resolution.code=code;
				resolution.name=code;
				//resolution.save();
			}


		} catch (DAOException e) {

			e.printStackTrace();
		}
		return resolution;
	}
	
		
	public static SampleType getSampleType(String codeType, String nameType,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException
		{

			List<State> states = new ArrayList<State>();
			states.add(DataTypeHelper.getState("Etat"+codeType));
			List<Resolution> resolutions = new ArrayList<Resolution>();
			resolutions.add(DataTypeHelper.getResolution("Resolution"+codeType));

			CommonInfoType commonInfoType = DataTypeHelper.getCommonInfoType(codeType,nameType, "Sample", null, null, null, "Sample");
			commonInfoType.propertiesDefinitions=propertyDefinitions;

			SampleType sampleType = new SampleType();
			sampleType.setCommonInfoType(commonInfoType);

			sampleType.sampleCategory=getSampleCategory(codeCategory);

			return sampleType;
	}
	
	
	public static ExperimentType getExperimentType(String codeType, String nameType,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException
	{

		List<State> states = new ArrayList<State>();
		states.add(DataTypeHelper.getState("New"));
		states.add(DataTypeHelper.getState("In Progress"));
		states.add(DataTypeHelper.getState("Finish"));
		
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DataTypeHelper.getResolution("Resolution"+codeType));

		CommonInfoType commonInfoType = DataTypeHelper.getCommonInfoType(codeType,nameType, "Experiment", null, null, null, "Experiment");
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		commonInfoType.variableStates=states;
		
		ExperimentType experimentType = new ExperimentType();
		experimentType.setCommonInfoType(commonInfoType);

		experimentType.experimentCategory=getExperimentCategory(codeCategory);

		return experimentType;
	}


	public static MeasureCategory getMeasureCategory(String code,String name,String codeValue,String valueValue){

		MeasureCategory measureCategory = null;
		try {
			measureCategory = MeasureCategory.find.findByCode(code);
			if(measureCategory==null){
				measureCategory = new MeasureCategory();
				measureCategory.code=code;
				measureCategory.name=name;

				List<MeasureValue> measureValues=new ArrayList<MeasureValue>();

				MeasureValue measureValue=new MeasureValue();
				measureValue.code=codeValue;
				measureValue.value=valueValue;
				measureValue.defaultValue=true;
				measureValue.measureCategory=measureCategory;

				//measureValue.id=measureValue.save();
				measureValues.add(measureValue);

				measureCategory.measurePossibleValues=measureValues;

				//measureCategory.id=measureCategory.save();
			}

		} catch (DAOException e) {

			e.printStackTrace();
		}
		return measureCategory;
	}
	
	
	public static List<Value> getListFromProcedureLims(String procedure){
		List<Value> listIndex=new ArrayList<Value>();
		try{
			Connection connection=DB.getConnection("lims");
			Statement stm=connection.createStatement();

			ResultSet resultSet=stm.executeQuery(procedure);
			while(resultSet.next()){
				Value value =new Value();
				value.code=resultSet.getString(1);
				value.value=resultSet.getString(1);
				listIndex.add(value);
			}
			stm.close();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listIndex;
	}

	public static ImportType getImportType(String codeImport, String nameImport,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException
	{

		List<State> states = new ArrayList<State>();
		states.add(DataTypeHelper.getState("Etat"+codeImport));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DataTypeHelper.getResolution("Resolution"+codeImport));

		CommonInfoType commonInfoType = DataTypeHelper.getCommonInfoType(codeImport,nameImport, "Import", null, null, null, "Import");
		commonInfoType.propertiesDefinitions=propertyDefinitions;

		ImportType importType = new ImportType();
		importType.setCommonInfoType(commonInfoType);

		importType.importCategory=getImportCategory(codeCategory);

		return importType;
	}
	
	
	public static ProjectType getProjectType(String codeProject, String nameProject,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException
	{

		List<State> states = new ArrayList<State>();
		states.add(DataTypeHelper.getState("Etat"+codeProject));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DataTypeHelper.getResolution("Resolution"+codeProject));

		CommonInfoType commonInfoType = DataTypeHelper.getCommonInfoType(codeProject,nameProject, "Project", null, null, null, "Project");
		commonInfoType.propertiesDefinitions=propertyDefinitions;

		ProjectType projectType =new ProjectType();
		projectType.setCommonInfoType(commonInfoType);

		projectType.projectCategory=getProjectCategory(codeCategory);

		return projectType;
	}
	
	

	private static ProjectCategory getProjectCategory(String codeCategory) throws DAOException {
		ProjectCategory projectCategory = ProjectCategory.find.findByCode(codeCategory);

		if(projectCategory==null){
			System.err.println(" import category not find :"+codeCategory);
			projectCategory=new ProjectCategory();
			projectCategory.code=codeCategory;
			projectCategory.name=codeCategory;
		}
		return projectCategory;
	}


	private static ImportCategory getImportCategory(String codeCategory) throws DAOException {
		System.err.println("Find import category :"+codeCategory);
		ImportCategory importCategory = ImportCategory.find.findByCode(codeCategory);

		if(importCategory==null){
			System.err.println(" import category not find :"+codeCategory);
			importCategory=new ImportCategory();
			importCategory.code=codeCategory;
			importCategory.name=codeCategory;
		}
		return importCategory;
	}
	
	private static SampleCategory getSampleCategory(String codeCategory) throws DAOException {
		System.err.println("Find import category :"+codeCategory);
		SampleCategory sampleCategory = SampleCategory.find.findByCode(codeCategory);

		if(sampleCategory==null){
			System.err.println(" import category not find :"+codeCategory);
			sampleCategory=new SampleCategory();
			sampleCategory.code=codeCategory;
			sampleCategory.name=codeCategory;
		}
		return sampleCategory;
	}
	
	public static ExperimentCategory getExperimentCategory(String codeCategory) throws DAOException {
		System.err.println("Find import category :"+codeCategory);
		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode(codeCategory);

		if(experimentCategory==null){
			System.err.println(" import category not find :"+codeCategory);
			experimentCategory=new ExperimentCategory();
			experimentCategory.code=codeCategory;
			experimentCategory.name=codeCategory;
		}
		return experimentCategory;
	}

	
	public static <T extends CommonInfoType> Map<String,List<ValidationError>>  saveMapType(Class<T> type,Map<String, T > mapCommonInfoType) throws DAOException{

		Map<String,List<ValidationError>>errors=new HashMap<String, List<ValidationError>>();

			for(Entry<String,T> sampleType : mapCommonInfoType.entrySet()){

				T samp= new HelperObjects<T>().getObject(type, sampleType.getKey(), errors);
				if(samp!=null){
					samp.remove();
				}

				Logger.debug(" Save :"+sampleType.getValue().code);
				sampleType.getValue().save();

				
				samp=new  HelperObjects<T>().getObject(type, sampleType.getKey(), errors);
				Logger.debug(" Save :"+sampleType.getValue().code);
			}	

		return errors;
	}
}
