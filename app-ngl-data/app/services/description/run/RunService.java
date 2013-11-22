package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.ValidationCriteria;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;


public class RunService {
	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		
		DAOHelpers.removeAll(ValidationCriteria.class, ValidationCriteria.find);
		
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		saveValidationCriteria(errors);
		
		saveReadSetType(errors);
		saveRunCategories(errors);
		saveRunType(errors);
	}
	
	
	public static void saveValidationCriteria(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ValidationCriteria> l = new ArrayList<ValidationCriteria>();
		l.add(DescriptionFactory.newValidationCriteria("Default", "default-criteria",  "specDefaultCriteriaCNGforSAVQualityControl_1" ));
		l.add(DescriptionFactory.newValidationCriteria("High", "criteria-high",  "specDefaultCriteriaCNGforSAVQualityControl_2" ));
		l.add(DescriptionFactory.newValidationCriteria("Low", "criteria-low",  "specDefaultCriteriaCNGforSAVQualityControl_3" ));
		DAOHelpers.saveModels(ValidationCriteria.class, l, errors);
	}
	
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		l.add(DescriptionFactory.newReadSetType("Default","default-readset",  getReadSetPropertyDefinitions(), getValidationCriterias("default-criteria"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}



	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "illumina"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions(), getValidationCriterias("default-criteria","criteria-high","criteria-low"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions(), getValidationCriterias("default-criteria"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("illumina"), getRunPropertyDefinitions(), getValidationCriterias("default-criteria"),  DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	
	private static List<PropertyDefinition> getRunPropertyDefinitions() throws DAOException {
		return null;
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding",LevelService.getLevels(Level.CODE.File), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label",LevelService.getLevels(Level.CODE.File), String.class, true));
		return propertyDefinitions;
	}
	
	
	private static List<ValidationCriteria> getValidationCriterias(String...codes) throws DAOException {
		List<ValidationCriteria> lvc = new ArrayList<ValidationCriteria>();
		ValidationCriteria vc = null;
		for(String code : codes){
			vc = DAOHelpers.getModelByCode(ValidationCriteria.class, ValidationCriteria.find, code);
			lvc.add(vc);
		}	
		return lvc;
	}

	
}

