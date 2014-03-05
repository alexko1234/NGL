package services.description.sample;

import static services.description.DescriptionFactory.newImportType;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class ImportService {
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(ImportType.class, ImportType.find);
		DAOHelpers.removeAll(ImportCategory.class, ImportCategory.find);
		
		saveImportCategories(errors);
		saveImportTypes(errors);
	}

	public static void saveImportCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportCategory> l = new ArrayList<ImportCategory>();
		l.add(saveImportCategory("Sample Import", "sample-import"));
		DAOHelpers.saveModels(ImportCategory.class, l, errors);
	}

	private static ImportCategory saveImportCategory(String name, String code) {
		ImportCategory ic = DescriptionFactory.newSimpleCategory(ImportCategory.class,name, code);
		return ic;
	}

	
	private static void saveImportTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportType> l = new ArrayList<ImportType>();
		
		l.add(newImportType("Defaut", "default-import", ImportCategory.find.findByCode("sample-import"), getCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newImportType("Banque", "library", ImportCategory.find.findByCode("sample-import"), getLibraryPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		l.add(newImportType("Tara", "tara-default", ImportCategory.find.findByCode("sample-import"), getTaraPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		l.add(newImportType("Banque tara", "tara-library", ImportCategory.find.findByCode("sample-import"), getLibraryTaraPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		
		DAOHelpers.saveModels(ImportType.class, l, errors);
		
	}

	private static List<PropertyDefinition> getCommonPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getLibraryPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getCommonPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Index", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true));
		propertyDefinitions.add(newPropertiesDefinition("IndexCategory", "tagCategory", LevelService.getLevels(Level.CODE.Content), String.class, true));		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getTaraPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getCommonPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Station TARA", "taraStation", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Integer.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Profondeur TARA", "taraDepth", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Filtre TARA", "taraFilter", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Iteration TARA", "taraIteration", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Integer.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Materiel TARA", "taraSample", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Code Barre TARA", "taraBarCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getLibraryTaraPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getTaraPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Index", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true));
		propertyDefinitions.add(newPropertiesDefinition("IndexCategory", "tagCategory", LevelService.getLevels(Level.CODE.Content), String.class, true));
		return propertyDefinitions;
	}
	
	public static List<Institute> getInstitutes(Institute.CODE...codes) throws DAOException {
		List<Institute> institutes = new ArrayList<Institute>();
		for(Institute.CODE code : codes){
			institutes.add(Institute.find.findByCode(code.name()));
		}
		return institutes;
	}
	
}
