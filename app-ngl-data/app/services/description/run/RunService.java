package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.ValuationCriteria;
import models.laboratory.run.description.AnalysisType;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.Logger;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;


public class RunService {
	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException {
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(AnalysisType.class, AnalysisType.find);
		
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		saveReadSetType(errors);
		saveAnalysisType(errors);
		saveRunCategories(errors);
		saveRunType(errors);
	}
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		l.add(DescriptionFactory.newReadSetType("Default","default-readset",  getReadSetPropertyDefinitions(), null, DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newReadSetType("RSARGUS","RSARGUS",  null, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
		
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}
	
	public static void saveAnalysisType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<AnalysisType> l = new ArrayList<AnalysisType>();
		l.add(DescriptionFactory.newAnalysisType("BAC pool assembly","BPA",  null, null, DescriptionFactory.getInstitutes(Institute.CODE.CNS) ));
		
		DAOHelpers.saveModels(AnalysisType.class, l, errors);
	}

	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "illumina"));
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Opgen", "opgen"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), null, DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), null, DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), null,  DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(DescriptionFactory.newRunType("RMISEQ","RMISEQ", 1, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), null,  DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(DescriptionFactory.newRunType("RGAIIx","RGAIIx", 1, RunCategory.find.findByCode("illumina"), getRunIlluminaPropertyDefinitions(), null,  DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(DescriptionFactory.newRunType("RARGUS","RARGUS", 1, RunCategory.find.findByCode("opgen"), null, null,  DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding",LevelService.getLevels(Level.CODE.File), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label",LevelService.getLevels(Level.CODE.File), String.class, true, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("isSentCCRT","isSentCCRT",LevelService.getLevels(Level.CODE.ReadSet), Boolean.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("isSentCollaborator","isSentCollaborator",LevelService.getLevels(Level.CODE.ReadSet), Boolean.class, false, "single"));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("md5","md5",LevelService.getLevels(Level.CODE.File), String.class, false, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getRunIlluminaPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("sequencingProgramType","sequencingProgramType",LevelService.getLevels(Level.CODE.Run), String.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<ValuationCriteria> getValuationCriterias(String...codes) throws DAOException {
		List<ValuationCriteria> valuationCriterias = new ArrayList<ValuationCriteria>();
		for(String code : codes){
			valuationCriterias.add(ValuationCriteria.find.findByCode(code));
		}	
		return valuationCriterias;
	}

}

