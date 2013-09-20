package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
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

	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		

		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);
				

		
		saveRunCategories(errors);
		
		saveRunType(errors);
		
		saveTreatmentCategory(errors);
		
		
		saveTreatmentContext(errors);
		
		
		saveTreatmentType(errors);
		
	}
	
	
	
	//@SuppressWarnings("unchecked")
	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "RHS", "rhs"));
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "RX", "rx"));
		
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		//newRunType(String name, String code, RunCategory category, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newRunType("RHS200","RHS200", RunCategory.find.findByCode("rhs"), null));
		l.add(DescriptionFactory.newRunType("RHS400","RHS400", RunCategory.find.findByCode("rhs"), null));
		l.add(DescriptionFactory.newRunType("RX","rx",RunCategory.find.findByCode("rx"),null ));
		
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	//@SuppressWarnings("unchecked")
	public static void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentCategory> l = new ArrayList<TreatmentCategory>();
		
		for (TreatmentCategory.CODE code : TreatmentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(TreatmentCategory.class, code.name(), code.name()));
		}
		
		DAOHelpers.saveModels(TreatmentCategory.class, l, errors);
	}
	
	
	public static void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentContext> l = new ArrayList<TreatmentContext>();
		l.add(DescriptionFactory.newTreatmentContext("context1","c1"));
		l.add(DescriptionFactory.newTreatmentContext("context2","c2"));
		l.add(DescriptionFactory.newTreatmentContext("context3","c3"));
		
		DAOHelpers.saveModels(TreatmentContext.class, l, errors);
	}
	
	
	public static void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentType> l = new ArrayList<TreatmentType>();
		//newTreatmentType(String name, String code, TreatmentCategory category, String names, List<PropertyDefinition> propertiesDefinitions, List<TreatmentContext>  contexts)
		l.add(DescriptionFactory.newTreatmentType("QualityAnalysis","QA", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.QC.name()), "names", getNGSRGropertyDefinitions(), getTreatmentContexts("c1"))); 
		
		DAOHelpers.saveModels(TreatmentType.class, l, errors);
	}
	
	
	private static List<TreatmentContext> getTreatmentContexts(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(TreatmentContext.class, TreatmentContext.find, codes);
	}
	
	


/*
	private static List<TreatmentType> getTreatmentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(TreatmentType.class, TreatmentType.find, codes);
	}
*/
	
	
	//Data Test
	public static List<PropertyDefinition> getNGSRGropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//String name, String code, List<Level> levels, Class<?> type, Boolean required
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellPosition","flowcellPosition", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycle","nbCycle", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellVersion","flowcellVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBase","nbBase", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatch","mismatch", LevelService.getLevels(Level.CODE.Run), Boolean.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("controlLane","controlLane", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rtaVersion","rtaVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterTotal","nbClusterTotal", LevelService.getLevels(Level.CODE.Run), Long.class, true));
		return propertyDefinitions;
	}
	
	
}

