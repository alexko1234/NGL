package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

public class FirstData {


	public static Map<String,SampleType> getSampleAdnClone() throws DAOException{

		SampleType sampleTypeBac = DescriptionHelper.getSampleType("bac","Bac", "ADNClone", getPropertyDefinitionsADNClone());
		SampleType sampleTypePlasmide = DescriptionHelper.getSampleType("plasmide","Plasmide", "ADNClone", getPropertyDefinitionsADNClone());
		SampleType sampleTypeFosmide = DescriptionHelper.getSampleType("fosmide","Fosmide", "ADNClone", getPropertyDefinitionsADNClone());

		Map<String,SampleType> results=new HashMap<String, SampleType>();
		

		results.put(sampleTypeFosmide.code, sampleTypeFosmide);
		results.put(sampleTypeBac.code, sampleTypeBac);
		results.put(sampleTypePlasmide.code,sampleTypePlasmide);

		return results;
	}

	
	public static Map<String,SampleType> getSampleAdn() throws DAOException{

		SampleType sampleTypeADNGenomique = DescriptionHelper.getSampleType("ADNGenomique","ADN Génomique", "ADN", getPropertyDefinitions());
		SampleType sampleTypeADNMeta = DescriptionHelper.getSampleType("ADNMetagenomique","ADN métagénomique", "ADN", getPropertyDefinitions());
		SampleType sampleTypeADNTara = DescriptionHelper.getSampleType("ADNTara","ADN Tara", "ADN",  getPropertyDefinitions());

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		
		results.put(sampleTypeADNGenomique.code, sampleTypeADNGenomique);
		results.put(sampleTypeADNMeta.code, sampleTypeADNMeta);
		results.put(sampleTypeADNTara.code,sampleTypeADNTara);
		
		return results;

	}

	public static Map<String,SampleType> getSampleAmplicon() throws DAOException{

		SampleType sampleTypeAmplicon = DescriptionHelper.getSampleType("amplicon","Amplicon", "amplicon", getPropertyDefinitionsTara()); 
		SampleType sampleTypeAmpliconTara = DescriptionHelper.getSampleType("ampliconTara","Amplicon Tara", "amplicon", getPropertyDefinitionsTara());

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypeAmplicon.code, sampleTypeAmplicon);
		results.put(sampleTypeAmpliconTara.code, sampleTypeAmpliconTara);

		return results;
		
	}

	public static Map<String,SampleType> getSampleInconnu() throws DAOException{

		SampleType sampleTypeInconnu = DescriptionHelper.getSampleType("indetermine","Indetermine", "inconnu", getPropertyDefinitions()); 

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypeInconnu.code, sampleTypeInconnu);

		return results;

	}


	public static Map<String,SampleType> getSampleArn() throws DAOException{

		SampleType sampleTypeARNTotal = DescriptionHelper.getSampleType("ARNTotal","ARN total", "ARN", getPropertyDefinitions()); 
		SampleType sampleTypeARNTotalTara = DescriptionHelper.getSampleType("ARNTara","ARN Total Tara", "ARN", getPropertyDefinitionsTara()); 
		SampleType sampleTypeARNm = DescriptionHelper.getSampleType("ARNm","ARNm", "ARN", getPropertyDefinitions()); 
		SampleType sampleTypesRNA = DescriptionHelper.getSampleType("sRNA","sRNA", "ARN", getPropertyDefinitions()); 

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		
		results.put(sampleTypeARNTotal.code, sampleTypeARNTotal);
		results.put(sampleTypeARNTotalTara.code, sampleTypeARNTotalTara);
		results.put(sampleTypeARNm.code,sampleTypeARNm);
		results.put(sampleTypesRNA.code,sampleTypesRNA);

		return results;
	}

	public static Map<String,SampleType> getSamplecDNA() throws DAOException{

		SampleType sampleTypecDNA =DescriptionHelper.getSampleType("cDNA","cDNA", "ARN", getPropertyDefinitions());
		SampleType sampleTypecDNATARA =DescriptionHelper.getSampleType("cDNATara","cDNA Tara", "ARN", getPropertyDefinitionsTara());

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypecDNA.code, sampleTypecDNA);
		results.put(sampleTypecDNATARA.code, sampleTypecDNATARA);

		return results;
		
	}

	public static Map<String,SampleType> getSampleMatImmunoprecipites() throws DAOException{

		SampleType sampleTypechip = DescriptionHelper.getSampleType("chiP","ChiP", "materielImmunoprecipite", getPropertyDefinitions());
		SampleType sampleTypeclip = DescriptionHelper.getSampleType("clip","CLIP", "materielImmunoprecipite", getPropertyDefinitions());
		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypechip.code, sampleTypechip);
		results.put(sampleTypeclip.code, sampleTypeclip);

		return results;
		
	}


	private static List<PropertyDefinition> getPropertyDefinitionsTara() {
		List<PropertyDefinition> propertyDefinitions = getPropertyDefinitions();
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("station","Station TARA",Boolean.TRUE, Boolean.TRUE, Long.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("profondeur","Profondeur TARA",Boolean.TRUE, Boolean.TRUE, String.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("filtre","Filtre TARA",Boolean.TRUE, Boolean.TRUE, String.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("iteration","Iteration TARA",Boolean.TRUE, Boolean.TRUE, String.class));

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitions() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("tailleTaxon","Taille associée au taxon",Boolean.TRUE, Boolean.TRUE, Long.class));		
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("fragmente","Fragmenté",Boolean.TRUE, Boolean.TRUE, Boolean.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("adaptateurs","Adpatateurs",Boolean.TRUE, Boolean.TRUE, Boolean.class));

		return propertyDefinitions;
	}


	public static List<PropertyDefinition> getPropertyDefinitionsADNClone() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("tailleInsert","Taille d'insert",Boolean.TRUE, Boolean.TRUE,  Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("vecteur","Vecteur",Boolean.FALSE,Boolean.TRUE, Long.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("souche","Souche",Boolean.FALSE, Boolean.TRUE,  Double.class));		
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("siteClone","Site clone",Boolean.FALSE, Boolean.TRUE,  Double.class));

		return propertyDefinitions;
	}

	public static List<PropertyDefinition> getPropertyDefinitionsImportBq() {

		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("index", "Index Illumina",Boolean.FALSE, String.class,DescriptionHelper.getListFromProcedureLims("pl_Tag")));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("concFournisseur", "Concentration fourni",false,true,Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("quantiteFournisseur", "Quantite fournie",false,true, Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeFournisseur", "Volume fourni", false,true, Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("dateReception", "Date reception", Date.class));

		return propertyDefinitions;
	}

	public static List<PropertyDefinition> getPropertyDefinitionsImport() {

		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		MeasureCategory measureCategory1=DescriptionHelper.getMeasureCategory("concentration", "Concentration", "ng/ul", "ng/ul");
		MeasureCategory measureCategory2=DescriptionHelper.getMeasureCategory("volume", "Volume", "ul", "ul");
		MeasureCategory measureCategory3=DescriptionHelper.getMeasureCategory("quantity", "Quantité", "ng", "ng");
		
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("concFournisseur", "Concentration fournie", Double.class,Boolean.FALSE,measureCategory1,measureCategory1.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("quantiteFournisseur", "Quantite fournie",Double.class,Boolean.FALSE,measureCategory3,measureCategory3.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeFournisseur", "Volume fourni", Double.class,Boolean.FALSE,measureCategory2,measureCategory2.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("dateReception", "Date réception", Date.class));
	
		return propertyDefinitions;
	}


	public static Map<String,ExperimentType> getEXperimentType() throws DAOException {

		Map<String,ExperimentType> results=new HashMap<String, ExperimentType>();

		ExperimentType experimentTypeBq =DescriptionHelper.getExperimentType("receptionBanqueSolexa", "Reception Banque Solexa", "Librairie", null);
		ExperimentType experimentTypeBrut =DescriptionHelper.getExperimentType("reception", "Reception", "Sample", null);
		// Librairie ??
		ExperimentType experimentTypeFrag =DescriptionHelper.getExperimentType("receptionFragmentation", "Reception Fragmentation", "Librairie", null);

		results.put(experimentTypeBq.code, experimentTypeBq);
		results.put(experimentTypeBrut.code, experimentTypeBrut);
		results.put(experimentTypeFrag.code, experimentTypeFrag);
		return results;
		
	}

	public static Map<String,SampleType> getSampleTypeAll() throws DAOException{
		Map<String,SampleType> sampletypes=FirstData.getSampleAdn();
		sampletypes.putAll(FirstData.getSampleAdnClone());
		sampletypes.putAll(FirstData.getSampleAmplicon());
		sampletypes.putAll(FirstData.getSamplecDNA());
		sampletypes.putAll(FirstData.getSampleInconnu());
		sampletypes.putAll(FirstData.getSamplecDNA());
		sampletypes.putAll(FirstData.getSampleMatImmunoprecipites());
		return sampletypes;
	}

	public static Map<String,ImportType> getImportTypeAll() throws DAOException{
		Map<String,ImportType> importtypes=new HashMap<String, ImportType>();
		importtypes.put("importBanqueSolexa",DescriptionHelper.getImportType("importBanqueSolexa","Importation Sample Bq Solexa","importSample",getPropertyDefinitionsImportBq()));
		importtypes.put("importNormal",DescriptionHelper.getImportType("importNormal","Importation Normal","importSample",getPropertyDefinitionsImport()));
		return importtypes;
	}


	public static Map<String,ProjectType> getProjectTypeAll() throws DAOException {
		Map<String,ProjectType> projecttypes=new HashMap<String, ProjectType>();
		
		ProjectType projectType=DescriptionHelper.getProjectType("projectType", "Projet Type","Projet", null);
				
		projecttypes.put(projectType.code, projectType);
		return projecttypes;
	}
	
	/*public static Map<String,ContainerCategory> getContainerCategorieAll() throws DAOException{
		Map<String,ContainerCategory> containerCategories=new HashMap<String, ContainerCategory>();
		
		ContainerCategory containerCategoryTube=DescriptionHelper.getContainerCategory("Tube");
		ContainerCategory containerCategoryWell=DescriptionHelper.getContainerCategory("Well");
		ContainerCategory containerCategoryLane=DescriptionHelper.getContainerCategory("Lane");
		
		containerCategories.put(containerCategoryLane.code,containerCategoryLane);
		containerCategories.put(containerCategoryWell.code,containerCategoryWell);
		containerCategories.put(containerCategoryTube.code,containerCategoryTube);
		
		return containerCategories;
	}*/
	
}
