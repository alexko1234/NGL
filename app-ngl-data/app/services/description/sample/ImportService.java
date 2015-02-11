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
import models.laboratory.common.description.Value;
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
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getLibraryPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getCommonPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Index", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getTaraPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getCommonPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Station TARA", "taraStation", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Integer.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Nom Profondeur TARA", "taraDepth", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true,getTaraDepthCodeValues(true), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Profondeur TARA", "taraDepthCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, getTaraDepthCodeValues(false),"single"));
		propertyDefinitions.add(newPropertiesDefinition("Nom Filtre TARA", "taraFilter", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true,getTaraFilterCodeValues(true), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Filtre TARA", "taraFilterCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, getTaraFilterCodeValues(false),"single"));
		propertyDefinitions.add(newPropertiesDefinition("Iteration TARA", "taraIteration", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Materiel TARA", "taraSample", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code Barre TARA", "taraBarCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, "single"));
		return propertyDefinitions;
	}
	
	
	private static List<Value> getTaraStationValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("", ""));
		
		return values;
		
	}
	
	private static List<Value> getTaraDepthCodeValues(boolean reverse){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("CTL", "CTL"));
		values.add(DescriptionFactory.newValue("DCM", "Deep Chlorophyl Maximum"));
		values.add(DescriptionFactory.newValue("DOP", "DCM and OMZ Pool"));
		values.add(DescriptionFactory.newValue("DSP", "DCM and Surface Pool"));
		values.add(DescriptionFactory.newValue("IZZ", "IntegratedDepth"));
		values.add(DescriptionFactory.newValue("MES", "Meso"));
		values.add(DescriptionFactory.newValue("MXL", "MixedLayer"));
		values.add(DescriptionFactory.newValue("NSI", "NightSampling@25mt0"));
		values.add(DescriptionFactory.newValue("NSJ", "NightSampling@25mt24"));
		values.add(DescriptionFactory.newValue("NSK", "NightSampling@25mt48"));
		values.add(DescriptionFactory.newValue("OBL", "OBLIQUE"));
		values.add(DescriptionFactory.newValue("OMZ", "Oxygen Minimum Zone"));
		values.add(DescriptionFactory.newValue("OTH", "Other"));
		values.add(DescriptionFactory.newValue("PFA", "PF1"));
		values.add(DescriptionFactory.newValue("PFB", "PF2"));
		values.add(DescriptionFactory.newValue("PFC", "PF3"));
		values.add(DescriptionFactory.newValue("PFD", "PF4"));
		values.add(DescriptionFactory.newValue("PFE", "PF5"));
		values.add(DescriptionFactory.newValue("PFF", "PF6"));
		values.add(DescriptionFactory.newValue("PFG", "P1a"));
		values.add(DescriptionFactory.newValue("PFH", "P1b"));
		values.add(DescriptionFactory.newValue("PFI", "B2B1"));
		values.add(DescriptionFactory.newValue("PFJ", "B4B3"));
		values.add(DescriptionFactory.newValue("PFK", "B6B5"));
		values.add(DescriptionFactory.newValue("PFL", "B8B7"));
		values.add(DescriptionFactory.newValue("PFM", "B10B9"));
		values.add(DescriptionFactory.newValue("SOD", "Surface OMZ and DCM Pool"));
		values.add(DescriptionFactory.newValue("SOP", "Surface and OMZ Pool"));
		values.add(DescriptionFactory.newValue("SUR", "Surface"));
		values.add(DescriptionFactory.newValue("SXL", "Sub-MixedLayer@100m"));
		values.add(DescriptionFactory.newValue("ZZZ", "DiscreteDepth"));
		
		if(reverse){
			List<Value> rValues = new ArrayList<Value>();
			for(Value v : values){
				rValues.add(DescriptionFactory.newValue(v.name, v.code));
			}
			values = rValues;
		}
		
		return values;
		
	}
	
	private static List<Value> getTaraFilterCodeValues(boolean reverse){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("AACC", "0-0.2"));
		values.add(DescriptionFactory.newValue("AAZZ", "0-inf"));
		values.add(DescriptionFactory.newValue("BBCC", "0.1-0.2"));
		values.add(DescriptionFactory.newValue("CCEE", "0.2-0.45"));
		values.add(DescriptionFactory.newValue("CCII", "0.2-1.6"));
		values.add(DescriptionFactory.newValue("CCKK", "0.22-3"));
		values.add(DescriptionFactory.newValue("EEGG", "0.45-0.8"));
		values.add(DescriptionFactory.newValue("EEOO", "0.45-8"));
		values.add(DescriptionFactory.newValue("GGKK", "0.8-3"));
		values.add(DescriptionFactory.newValue("GGMM", "0.8-5"));
		values.add(DescriptionFactory.newValue("GGQQ", "0.8-20"));
		values.add(DescriptionFactory.newValue("GGRR", "0.8-200"));
		values.add(DescriptionFactory.newValue("GGSS", "0.8-180"));
		values.add(DescriptionFactory.newValue("GGZZ", "0.8-inf"));
		values.add(DescriptionFactory.newValue("IIQQ", "1.6-20"));
		values.add(DescriptionFactory.newValue("KKQQ", "3-20"));
		values.add(DescriptionFactory.newValue("KKZZ", "3-inf"));
		values.add(DescriptionFactory.newValue("MMQQ", "5-20"));
		values.add(DescriptionFactory.newValue("QQRR", "20-200"));
		values.add(DescriptionFactory.newValue("QQSS", "20-180"));
		values.add(DescriptionFactory.newValue("SSUU", "180-2000"));
		values.add(DescriptionFactory.newValue("SSZZ", "180-inf"));
		values.add(DescriptionFactory.newValue("TTZZ", "300-inf"));
		values.add(DescriptionFactory.newValue("YYYY", "pool"));
		values.add(DescriptionFactory.newValue("ZZZZ", "inf-inf"));

		if(reverse){
			List<Value> rValues = new ArrayList<Value>();
			for(Value v : values){
				rValues.add(DescriptionFactory.newValue(v.name, v.code));
			}
			values = rValues;
		}
		return values;
		
	}
	
	
	private static List<PropertyDefinition> getLibraryTaraPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getTaraPropertyDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Index", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true, "single"));
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
