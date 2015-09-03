package services.description.sample;

import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newSampleType;
import static services.description.DescriptionFactory.newValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
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
		//FDS 05/06/2015 JIRA NGL-672: ajout des categories CNG qui n'existaient pas au CNS 
		l.add(newSampleCategory("FAIRE", "FAIRE"));  // manquant dans sample_parametrage_CNG.xls ( voir Julie)
		l.add(newSampleCategory("Methylated Base DNA (MBD)","methylated-base-DNA")); // manquant dans sample_parametrage_CNG.xls ( voir Julie)
		l.add(newSampleCategory("Bisulfite DNA","bisulfite-DNA")); // dans la spec mais inexistant a l'heure actuelle
		//FDS 16/16/2015 necessaire pour l'import des tubes...
		l.add(newSampleCategory("Control","control"));
		
		DAOHelpers.saveModels(SampleCategory.class, l, errors);
	}
	
	/* FDS 05/06/205 JIRA NGL-672: ajout Institute.CODE.CNG pour les sampleType communs CNS/CNG 
	 * NOTE: pour le CNG certains SampleCategory n'ont pas de SampleType definis=> utiliser le meme code (cf G.albini)
	 */
	public void saveSampleTypes(Map<String, List<ValidationError>> errors) throws DAOException{
		List<SampleType> l = new ArrayList<SampleType>();
		
		
		l.add(newSampleType("ARN", "RNA", SampleCategory.find.findByCode("RNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		

		// il y a du ChIP et du MedIP au CNG mais ce n'est pas detaillé au niveau sample dans la base Solexa  creer un.SampleType de meme nom que SampleCategory
		l.add(newSampleType("Materiel Immunoprecipite", "IP-sample", SampleCategory.find.findByCode("IP-sample"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("ADN Génomique", "gDNA", SampleCategory.find.findByCode("DNA"), getPropertyDefinitionsADNGenomic(), getInstitutes(Institute.CODE.CNG))); 

		/* SampleTypes specifique CNG
		 * utiliser  getSampleCNGPropertyDefinitions()
		 * pas de subdivisions dans la base solexa...=> SampleType=SampleCategory
		 */
		l.add(newSampleType("FAIRE", "FAIRE", SampleCategory.find.findByCode("FAIRE"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("methylated base DNA (mbd)", "methylated-base-DNA", SampleCategory.find.findByCode("methylated-base-DNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("bisulfite DNA", "bisulfite-DNA", SampleCategory.find.findByCode("bisulfite-DNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("Control", "CTRL", SampleCategory.find.findByCode("control"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		//default values
		//l.add(newSampleType("Défaut", "default-sample-cns", SampleCategory.find.findByCode("default"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));		
		l.add(newSampleType("Défaut", "default-sample-cng", SampleCategory.find.findByCode("default"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("Inconnu", "unknown", SampleCategory.find.findByCode("unknown"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		//use only in  NGL-BI. Please not used in sample import !!!!!!!!!!!!!!!
		l.add(newSampleType("Non défini", "not-defined", SampleCategory.find.findByCode("unknown"),null, getInstitutes(Institute.CODE.CNG)));
		
		DAOHelpers.saveModels(SampleType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getSampleCNGPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, true, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getSampleCommonPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false, "single"));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADNGenomic() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false, "single"));
        propertyDefinitions.add(newPropertiesDefinition("WGA", "isWGA", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false, "single"));
        //TODO GCpercent same as TreatmentService
        propertyDefinitions.add(newPropertiesDefinition("% GC", "gcPercent", LevelService.getLevels(Level.CODE.Sample),Double.class, false, "single"));
        //For CNG only
        return propertyDefinitions;
	}
	
	


}
