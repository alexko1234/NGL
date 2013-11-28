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
import play.Logger;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;


public class RunService {
	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException {
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		
		saveReadSetType(errors);
		saveRunCategories(errors);
		saveRunType(errors);
	}
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		l.add(DescriptionFactory.newReadSetType("Default","default-readset",  getReadSetPropertyDefinitions(), getValidationCriterias("default-criteria-readset"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}

	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "illumina"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("illumina"), null, getValidationCriterias("default-criteria-run","criteria-high-run","criteria-low-run"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("illumina"), null, getValidationCriterias("default-criteria-run"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("illumina"), null, getValidationCriterias("default-criteria-run"),  DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getReadSetPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding",LevelService.getLevels(Level.CODE.File), String.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label",LevelService.getLevels(Level.CODE.File), String.class, true));
		return propertyDefinitions;
	}
	
	private static List<ValidationCriteria> getValidationCriterias(String...codes) throws DAOException {
		List<ValidationCriteria> validationCriterias = new ArrayList<ValidationCriteria>();
		for(String code : codes){
			validationCriterias.add(ValidationCriteria.find.findByCode(code));
		}	
		return validationCriterias;
	}

}

