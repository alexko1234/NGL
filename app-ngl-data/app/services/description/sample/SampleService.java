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
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;
public class SampleService {
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		DAOHelpers.removeAll(SampleType.class, SampleType.find);
		DAOHelpers.removeAll(SampleCategory.class, SampleCategory.find);
		
		saveSampleCategories(errors);
		saveSampleTypes(errors);
	}

	public static void saveSampleCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<SampleCategory> l = new ArrayList<SampleCategory>();
		
		l.add(newSampleCategory("ADN Clone", "cloned-DNA"));
		l.add(newSampleCategory("Matériel Immunoprécipité","IP-sample"));
		l.add(newSampleCategory("Amplicon", "amplicon"));
		l.add(newSampleCategory("Inconnu", "default"));
		l.add(newSampleCategory("ADN", "DNA"));
		l.add(newSampleCategory("ARN", "RNA"));
		l.add(newSampleCategory("cDNA", "cDNA"));
		
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
	public static void saveSampleTypes(Map<String, List<ValidationError>> errors) throws DAOException{
		List<SampleType> l = new ArrayList<SampleType>();
		
		l.add(newSampleType("BAC", "BAC", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));	
		l.add(newSampleType("Plasmide", "plasmid", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));	
		l.add(newSampleType("Fosmide", "fosmid", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));	
		
		// ajout CNG
		l.add(newSampleType("ADN Génomique", "gDNA", SampleCategory.find.findByCode("DNA"), getPropertyDefinitionsADNGenomic(), getInstitutes(Institute.CODE.CNS,Institute.CODE.CNG))); 
		l.add(newSampleType("ADN Métagénomique", "MeTa-DNA", SampleCategory.find.findByCode("DNA"), getPropertyDefinitionsADN(), getInstitutes(Institute.CODE.CNS)));	
		
		l.add(newSampleType("Amplicon", "amplicon", SampleCategory.find.findByCode("amplicon"), getPropertyDefinitionsAmplicon(), getInstitutes(Institute.CODE.CNS)));
		
		l.add(newSampleType("ARN total", "total-RNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ARNm", "mRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("Small RNA", "sRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ARN déplété", "depletedRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("aARN", "aRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));		
		
		l.add(newSampleType("ARN", "RNA", SampleCategory.find.findByCode("RNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		l.add(newSampleType("cDNA", "cDNA", SampleCategory.find.findByCode("cDNA"), getPropertyDefinitionscDNA(), getInstitutes(Institute.CODE.CNS)));	

		l.add(newSampleType("ChIP", "chIP", SampleCategory.find.findByCode("IP-sample"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ClIP", "clIP", SampleCategory.find.findByCode("IP-sample"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		// il y a du ChIP et du MedIP au CNG mais ce n'est pas detaillé au niveau sample dans la base Solexa  creer un.SampleType de meme nom que SampleCategory
		l.add(newSampleType("Materiel Immunoprecipite", "IP-sample", SampleCategory.find.findByCode("IP-sample"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		/* SampleTypes specifique CNG
		 * utiliser  getSampleCNGPropertyDefinitions()
		 * pas de subdivisions dans la base solexa...=> SampleType=SampleCategory
		 */
		l.add(newSampleType("FAIRE", "FAIRE", SampleCategory.find.findByCode("FAIRE"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("methylated base DNA (mbd)", "methylated-base-DNA", SampleCategory.find.findByCode("methylated-base-DNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("bisulfite DNA", "bisulfite-DNA", SampleCategory.find.findByCode("bisulfite-DNA"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		l.add(newSampleType("Control", "CTRL", SampleCategory.find.findByCode("control"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		//default values
		l.add(newSampleType("Indéterminé", "default-sample-cns", SampleCategory.find.findByCode("default"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));		
		l.add(newSampleType("Indéterminé", "default-sample-cng", SampleCategory.find.findByCode("default"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
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
	
		
	public static List<PropertyDefinition> getPropertyDefinitionsADNClone() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Taille d'insert", "insertSize", LevelService.getLevels(Level.CODE.Sample),Double.class, false,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Vecteur", "vector", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Souche", "strain", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Site clone", "cloneSite", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADN() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("WGA", "isWGA", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false, "single"));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADNGenomic() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitionsADN());
        //TODO GCpercent same as TreatmentService
        propertyDefinitions.add(newPropertiesDefinition("% GC", "gcPercent", LevelService.getLevels(Level.CODE.Sample),Double.class, false, "single"));
        //For CNG only
        return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsAmplicon() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Matériel ciblé", "targetSampleCategory", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Plusieurs Régions ciblées", "isSeveralTargets", LevelService.getLevels(Level.CODE.Sample), Boolean.class, false, "single"));
        propertyDefinitions.add(newPropertiesDefinition("Régions ciblées", "targets", LevelService.getLevels(Level.CODE.Sample), String.class, false, "single"));
        
        return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsARN() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Sample, Level.CODE.Content),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false, "single"));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionscDNA() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Type de synthèse", "synthesisType", LevelService.getLevels(Level.CODE.Sample), String.class, false, newValues("random","oligoDT"), "single"));        
        return propertyDefinitions;
	}
	
	public static SampleCategory newSampleCategory(String name, String code) {
		SampleCategory sc = DescriptionFactory.newSimpleCategory(SampleCategory.class,name, code);
		return sc;
	}
	
	public static List<Institute> getInstitutes(Institute.CODE...codes) throws DAOException {
		List<Institute> institutes = new ArrayList<Institute>();
		for(Institute.CODE code : codes){
			institutes.add(Institute.find.findByCode(code.name()));
		}
		return institutes;
	}
}
