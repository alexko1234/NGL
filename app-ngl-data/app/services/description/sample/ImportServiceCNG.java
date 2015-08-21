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
import services.description.common.LevelService;

public class ImportServiceCNG extends AbstractImportService {


	public  void saveImportCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportCategory> l = new ArrayList<ImportCategory>();
		l.add(saveImportCategory("Sample Import", "sample-import"));
		DAOHelpers.saveModels(ImportCategory.class, l, errors);
	}

	
	public void saveImportTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportType> l = new ArrayList<ImportType>();
		l.add(newImportType("Defaut", "default-import", ImportCategory.find.findByCode("sample-import"), getCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		DAOHelpers.saveModels(ImportType.class, l, errors);
		
	}

	private static List<PropertyDefinition> getCommonPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
	
	
	
}
