package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class RunService {
	
	/*
			
			runStatesCode : IP_S, IP_RG, F_RG, F			

			readSetStatesCode : IP_RG, F_RG, IW_QC, F_QC, A, UA

			runTypeCode : RHS2000, RHS2500, RHS2500R	
			
		//getReadSetGlobalPropertyDefinitions : 
		"usefulSequences","usefulSequences",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
		"usefulBases","usefulBases",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
	 */

	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		saveReadSetType(errors);
		saveRunCategories(errors);
		saveRunType(errors);
	}
	
	
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		l.add(DescriptionFactory.newReadSetType("Default","default-readset",  getReadSetPropertyDefinitions()));
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}



	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		// name, code
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "illumina"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		//newRunType(String name, String code, Integer nbLanes, RunCategory category, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions()));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions()));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions()));
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	
	//Data Test
	private static List<PropertyDefinition> getRunPropertyDefinitions() throws DAOException {
		return null;
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding",LevelService.getLevels(Level.CODE.File), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label",LevelService.getLevels(Level.CODE.File), String.class, true));
		return propertyDefinitions;
	}
	
}

