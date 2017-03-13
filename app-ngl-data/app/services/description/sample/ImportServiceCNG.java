package services.description.sample;

import static services.description.DescriptionFactory.newImportType;
import static services.description.DescriptionFactory.newPropertiesDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
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
		l.add(newImportType("Defaut", "default-import", ImportCategory.find.findByCode("sample-import"), getSampleCNGPropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		
		l.add(newImportType("Import tubes", "tube-from-bank-reception", ImportCategory.find.findByCode("sample-import"), getBankReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNG)));
		
		
		DAOHelpers.saveModels(ImportType.class, l, errors);
		
	}

	private static List<PropertyDefinition> getBankReceptionPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Sex", "gender", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, null, 
				DescriptionFactory.newValues("0","1","2"), null,null,null,"single", 17, false, null,null));		
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getSampleCNGPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, true, "single"));
		return propertyDefinitions;
	}
	
}
