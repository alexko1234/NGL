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
		l.add(newSampleCategory("Materiel Immunoprecipite","IP-sample"));
		l.add(newSampleCategory("Amplicon", "amplicon"));
		l.add(newSampleCategory("Inconnu", "default")); //old : "unknown"
		l.add(newSampleCategory("ADN", "DNA"));
		l.add(newSampleCategory("ARN", "RNA"));
		l.add(newSampleCategory("cDNA", "cDNA"));
		DAOHelpers.saveModels(SampleCategory.class, l, errors);
	}
	
	
	public static void saveSampleTypes(Map<String, List<ValidationError>> errors) throws DAOException{
		List<SampleType> l = new ArrayList<SampleType>();
		
		l.add(newSampleType("BAC", "BAC", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("Plasmide", "plasmid", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("Fosmide", "fosmid", SampleCategory.find.findByCode("cloned-DNA"), getPropertyDefinitionsADNClone(), getInstitutes(Institute.CODE.CNS)));
		
		l.add(newSampleType("ADN Génomique", "gDNA", SampleCategory.find.findByCode("DNA"), getPropertyDefinitionsADNGenomic(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ADN Métagénomique", "MeTa-DNA", SampleCategory.find.findByCode("DNA"), getPropertyDefinitionsADN(), getInstitutes(Institute.CODE.CNS)));
		
		l.add(newSampleType("Amplicon", "amplicon", SampleCategory.find.findByCode("amplicon"), getPropertyDefinitionsAmplicon(), getInstitutes(Institute.CODE.CNS)));
		//default value
		l.add(newSampleType("Indéterminé", "default-sample-cns", SampleCategory.find.findByCode("default"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		
		l.add(newSampleType("ARN total", "total-RNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ARNm", "mRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("Small RNA", "sRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ARN déplété", "depletedRNA", SampleCategory.find.findByCode("RNA"), getPropertyDefinitionsARN(), getInstitutes(Institute.CODE.CNS)));

		l.add(newSampleType("cDNA", "cDNA", SampleCategory.find.findByCode("cDNA"), getPropertyDefinitionscDNA(), getInstitutes(Institute.CODE.CNS)));
		
		
		l.add(newSampleType("ChIP", "chIP", SampleCategory.find.findByCode("IP-sample"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		l.add(newSampleType("ClIP", "clIP", SampleCategory.find.findByCode("IP-sample"), getSampleCommonPropertyDefinitions(), getInstitutes(Institute.CODE.CNS)));
		
		//dnoisett, new, for CNG import
		l.add(newSampleType("Indéterminé", "default-sample-cng", SampleCategory.find.findByCode("default"), getSampleCNGPropertyDefinitions(), getInstitutes(Institute.CODE.CNG)));
		
		DAOHelpers.saveModels(SampleType.class, l, errors);
	}
	
	private static List<PropertyDefinition> getSampleCNGPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getSampleCommonPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb")));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADNClone() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Taille d'insert", "insertSize", LevelService.getLevels(Level.CODE.Sample),Double.class, false,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb")));
		propertyDefinitions.add(newPropertiesDefinition("Vecteur", "vector", LevelService.getLevels(Level.CODE.Sample),String.class, false));
		propertyDefinitions.add(newPropertiesDefinition("Souche", "strain", LevelService.getLevels(Level.CODE.Sample),String.class, false));
		propertyDefinitions.add(newPropertiesDefinition("Site clone", "cloneSite", LevelService.getLevels(Level.CODE.Sample),String.class, false));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADN() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("WGA", "isWGA", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsADNGenomic() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitionsADN());
        propertyDefinitions.add(newPropertiesDefinition("%GC", "gcPercent", LevelService.getLevels(Level.CODE.Sample),Float.class, false));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsAmplicon() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Matériel ciblé", "targetSampleCategory", LevelService.getLevels(Level.CODE.Sample),String.class, false));
        propertyDefinitions.add(newPropertiesDefinition("Plusieurs Régions ciblées", "isSeveralTargets", LevelService.getLevels(Level.CODE.Sample), Boolean.class, false));
        propertyDefinitions.add(newPropertiesDefinition("Régions ciblées", "targets", LevelService.getLevels(Level.CODE.Sample), String.class, false));
        
        return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsARN() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Sample, Level.CODE.Content),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("Mb"), MeasureUnit.find.findByCode("Mb")));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, true));
		propertyDefinitions.add(newPropertiesDefinition("Code LIMS", "limsCode", LevelService.getLevels(Level.CODE.Sample),Integer.class, false));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionscDNA() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getSampleCommonPropertyDefinitions());
        propertyDefinitions.add(newPropertiesDefinition("Type de synthèse", "synthesisType", LevelService.getLevels(Level.CODE.Sample), String.class, false, newValues("random","oligoDT")));        
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
