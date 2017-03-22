package services.description.sample;

import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newSampleType;
import static services.description.DescriptionFactory.newValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.common.LevelService;
import services.description.common.MeasureService;

public class SampleServiceCNG extends AbstractSampleService {
	
	
	public  void saveSampleCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<SampleCategory> l = new ArrayList<SampleCategory>();
		
		l.add(newSampleCategory("Défaut", "default"));
		l.add(newSampleCategory("Inconnu", "unknown"));
		l.add(newSampleCategory("Matériel Immunoprécipité","IP-sample"));
		l.add(newSampleCategory("ARN", "RNA"));
		l.add(newSampleCategory("ADN", "DNA"));
		l.add(newSampleCategory("FAIRE", "FAIRE"));  // manquant dans sample_parametrage_CNG.xls ( voir Julie)
		l.add(newSampleCategory("Methylated Base DNA (MBD)","methylated-base-DNA")); // manquant dans sample_parametrage_CNG.xls ( voir Julie)
		l.add(newSampleCategory("Bisulfite DNA","bisulfite-DNA")); // dans la spec mais inexistant a l'heure actuelle
		l.add(newSampleCategory("Control","control"));
		
		DAOHelpers.saveModels(SampleCategory.class, l, errors);
	}
	

	public void saveSampleTypes(Map<String, List<ValidationError>> errors) throws DAOException{
		List<SampleType> l = new ArrayList<SampleType>();
				
		l.add(newSampleType("ARN", "RNA", SampleCategory.find.findByCode("RNA"),null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("ADN", "DNA", SampleCategory.find.findByCode("DNA"),null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("IP", "IP", SampleCategory.find.findByCode("IP-sample"),null, getInstitutes(Constants.CODE.CNG)));
		
		// a supprimer apres mise a jour de la base: est remplacé par "IP" ci dessus...
		l.add(newSampleType("Materiel Immunoprecipite", "IP-sample", SampleCategory.find.findByCode("IP-sample"), null, getInstitutes(Constants.CODE.CNG)));
		
		l.add(newSampleType("ADN Génomique", "gDNA", SampleCategory.find.findByCode("DNA"), null, getInstitutes(Constants.CODE.CNG))); 

		/* pas de subdivisions dans la base solexa...=> SampleType=SampleCategory*/
		l.add(newSampleType("FAIRE", "FAIRE", SampleCategory.find.findByCode("FAIRE"), null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("methylated base DNA (mbd)", "methylated-base-DNA", SampleCategory.find.findByCode("methylated-base-DNA"), null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("bisulfite DNA", "bisulfite-DNA", SampleCategory.find.findByCode("bisulfite-DNA"), null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("Control", "CTRL", SampleCategory.find.findByCode("control"), null, getInstitutes(Constants.CODE.CNG)));
		
		//default values		
		l.add(newSampleType("Défaut", "default-sample-cng", SampleCategory.find.findByCode("default"), null, getInstitutes(Constants.CODE.CNG)));
		l.add(newSampleType("Inconnu", "unknown", SampleCategory.find.findByCode("unknown"), null, getInstitutes(Constants.CODE.CNG)));
		
		//use only in  NGL-BI. Please not used in sample import !!!!!!!!!!!!!!!
		l.add(newSampleType("Non défini", "not-defined", SampleCategory.find.findByCode("unknown"),null, getInstitutes(Constants.CODE.CNG)));
		
		DAOHelpers.saveModels(SampleType.class, l, errors);
	}
	
	
	
	/*FDS 20/01/2016 DEPRECATED
	private static List<PropertyDefinition> getSampleCommonPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false, "single"));
		return propertyDefinitions;
	}
	*/
	
	/* FDS 20/01/2016 DEPRECATED
	public static List<PropertyDefinition> getPropertyDefinitionsADNGenomic() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false, "single"));
        propertyDefinitions.add(newPropertiesDefinition("WGA", "isWGA", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false, "single"));
        propertyDefinitions.add(newPropertiesDefinition("% GC", "gcPercent", LevelService.getLevels(Level.CODE.Sample),Double.class, false, "single"));
        return propertyDefinitions;
	}
	*/
	


}