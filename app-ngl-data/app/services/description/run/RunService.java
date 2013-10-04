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

	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);
				

		saveReadSetType(errors);
		
		saveRunCategories(errors);
		
		saveRunType(errors);
		
		saveTreatmentCategory(errors);
		
		saveTreatmentContext(errors);
		
		saveTreatmentType(errors);
		
	}
	
	
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		//newReadSetType(name, code, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newReadSetType("default","d",  getReadSetRGropertyDefinitions()));
		l.add(DescriptionFactory.newReadSetType("readSet","r", getReadSetRGropertyDefinitions()));
		
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}



	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		// name, code
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "rhs"));
		
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		//newRunType(String name, String code, Integer nbLanes, RunCategory category, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("rhs"), getRunRGropertyDefinitions()));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("rhs"), getRunRGropertyDefinitions()));
		
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	public static void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentCategory> l = new ArrayList<TreatmentCategory>();
		
		for (TreatmentCategory.CODE code : TreatmentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(TreatmentCategory.class, code.name(), code.name()));
		}
		
		DAOHelpers.saveModels(TreatmentCategory.class, l, errors);
	}
	
	
	public static void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentContext> l = new ArrayList<TreatmentContext>();
		l.add(DescriptionFactory.newTreatmentContext("Default","D"));
		l.add(DescriptionFactory.newTreatmentContext("Read1","Read1"));
		l.add(DescriptionFactory.newTreatmentContext("Read2","Read2"));
		l.add(DescriptionFactory.newTreatmentContext("Pairs","Pairs"));
		l.add(DescriptionFactory.newTreatmentContext("Single","Single"));
		
		DAOHelpers.saveModels(TreatmentContext.class, l, errors);
	}
	
	
	public static void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentType> l = new ArrayList<TreatmentType>();
		
		//newTreatmentType(String name, String code, TreatmentCategory category, String names, List<PropertyDefinition> propertiesDefinitions, List<TreatmentContext>  contexts)
		l.add(DescriptionFactory.newTreatmentType("ngs_rg","ngs_rg", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.Quality_QC.name()), "ngs_rg", getNGSRGropertyDefinitions(), getTreatmentContexts("Read1", "Read2")));

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
	public static List<PropertyDefinition> getNGSRGropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//String name, String code, List<Level> levels, Class<?> type, Boolean required
        //Run level
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellPosition","flowcellPosition", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycle","nbCycle", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellVersion","flowcellVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBase","nbBase", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatch","mismatch", LevelService.getLevels(Level.CODE.Run), Boolean.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("controlLane","controlLane", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rtaVersion","rtaVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterTotal","nbClusterTotal", LevelService.getLevels(Level.CODE.Run), Long.class, true));
		
        // Lane level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing", LevelService.getLevels(Level.CODE.Lane), String.class, true));
        //propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing", LevelService.getLevels(Level.CODE.Lane), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex2","nbCycleReadIndex2", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead2","nbCycleRead2", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead1","nbCycleRead1", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex1","nbCycleReadIndex1", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane, Level.CODE.ReadSet), Long.class, true));
        
        // ReadSet level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("q30","q30", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbUsableCluster","nbUsableCluster", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("insertLength","insertLength", LevelService.getLevels(Level.CODE.ReadSet), Integer.class, true));
        //propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        //propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbRead","nbRead", LevelService.getLevels(Level.CODE.ReadSet), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("score","score", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbUsableBase","nbUsableBase", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("fraction","fraction", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        
        // file level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label", LevelService.getLevels(Level.CODE.File), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding", LevelService.getLevels(Level.CODE.File), String.class, true));
        
        return propertyDefinitions;
	}
	
	//Data Test
	public static List<PropertyDefinition> getRunRGropertyDefinitions() throws DAOException {
        return null;
	}
	public static List<PropertyDefinition> getReadSetRGropertyDefinitions() throws DAOException {
        return null;
	}
	
	
}

