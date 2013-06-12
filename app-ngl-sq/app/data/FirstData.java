package data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.State;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;

public class FirstData {


	public static Map<String,SampleType> getSampleAdnClone() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypeBac = DescriptionHelper.getSampleType("bac","Bac", "ADNClone", getPropertyDefinitionsADNClone());
		SampleType sampleTypePlasmide = DescriptionHelper.getSampleType("plasmide","Plasmide", "ADNClone", getPropertyDefinitionsADNClone());
		SampleType sampleTypeFosmide = DescriptionHelper.getSampleType("fosmide","Fosmide", "ADNClone", getPropertyDefinitionsADNClone());

		Map<String,SampleType> results=new HashMap<String, SampleType>();


		results.put(sampleTypeFosmide.code, sampleTypeFosmide);
		results.put(sampleTypeBac.code, sampleTypeBac);
		results.put(sampleTypePlasmide.code,sampleTypePlasmide);

		return results;
	}


	public static Map<String,SampleType> getSampleAdn() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypeADNGenomique = DescriptionHelper.getSampleType("ADNGenomique","ADN Génomique", "ADN", getPropertyDefinitionsAdn());
		SampleType sampleTypeADNMeta = DescriptionHelper.getSampleType("ADNMetagenomique","ADN métagénomique", "ADN", getPropertyDefinitionsAdn());
	//	SampleType sampleTypeADNTara = DescriptionHelper.getSampleType("ADNTara","ADN Tara", "ADN",  getPropertyDefinitionsAdn());

		Map<String,SampleType> results=new HashMap<String, SampleType>();


		results.put(sampleTypeADNGenomique.code, sampleTypeADNGenomique);
		results.put(sampleTypeADNMeta.code, sampleTypeADNMeta);
	//	results.put(sampleTypeADNTara.code,sampleTypeADNTara);

		return results;

	}

	

	public static Map<String,SampleType> getSampleAmplicon() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypeAmplicon = DescriptionHelper.getSampleType("amplicon","Amplicon", "amplicon", getPropertyDefinitionsAmplicon()); 
	//	SampleType sampleTypeAmpliconTara = DescriptionHelper.getSampleType("ampliconTara","Amplicon Tara", "amplicon", getPropertyDefinitionsAmplicon());

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypeAmplicon.code, sampleTypeAmplicon);
	//	results.put(sampleTypeAmpliconTara.code, sampleTypeAmpliconTara);

		return results;

	}

	


	public static Map<String,SampleType> getSampleInconnu() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypeInconnu = DescriptionHelper.getSampleType("indetermine","Indetermine", "inconnu", getPropertyDefinitions()); 

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypeInconnu.code, sampleTypeInconnu);

		return results;

	}


	public static Map<String,SampleType> getSampleArn() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		SampleType sampleTypeARNTotal = DescriptionHelper.getSampleType("ARNTotal","ARN total", "ARN", getPropertyDefinitionsArn()); 
	//	SampleType sampleTypeARNTotalTara = DescriptionHelper.getSampleType("ARNTara","ARN Total Tara", "ARN", getPropertyDefinitionsArn()); 
		SampleType sampleTypeARNm = DescriptionHelper.getSampleType("ARNm","ARNm", "ARN", getPropertyDefinitionsArn()); 
		SampleType sampleTypesRNA = DescriptionHelper.getSampleType("sRNA","sRNA", "ARN", getPropertyDefinitionsArn()); 

		Map<String,SampleType> results=new HashMap<String, SampleType>();


		results.put(sampleTypeARNTotal.code, sampleTypeARNTotal);
	//	results.put(sampleTypeARNTotalTara.code, sampleTypeARNTotalTara);
		results.put(sampleTypeARNm.code,sampleTypeARNm);
		results.put(sampleTypesRNA.code,sampleTypesRNA);

		return results;
	}

	public static Map<String,SampleType> getSamplecDNA() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypecDNA =DescriptionHelper.getSampleType("cDNA","cDNA", "cDNA", getPropertyDefinitionscDNA());
	//	SampleType sampleTypecDNATARA =DescriptionHelper.getSampleType("cDNATara","cDNA Tara", "cDNA", getPropertyDefinitionscDNA());

		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypecDNA.code, sampleTypecDNA);
	//	results.put(sampleTypecDNATARA.code, sampleTypecDNATARA);

		return results;

	}


	public static Map<String,SampleType> getSampleMatImmunoprecipites() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		SampleType sampleTypechip = DescriptionHelper.getSampleType("chiP","ChiP", "materielImmunoprecipite", getPropertyDefinitions());
		SampleType sampleTypeclip = DescriptionHelper.getSampleType("clip","CLIP", "materielImmunoprecipite", getPropertyDefinitions());
		Map<String,SampleType> results=new HashMap<String, SampleType>();

		results.put(sampleTypechip.code, sampleTypechip);
		results.put(sampleTypeclip.code, sampleTypeclip);

		return results;

	}

	//TODO revoir la liste avec Julie
		private static List<PropertyDefinition> getPropertyDefinitions() {
			List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
			propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("tailleTaxon","Taille associée au taxon",Boolean.TRUE, Boolean.TRUE, Long.class));		
			propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("fragmente","Fragmenté",Boolean.TRUE, Boolean.TRUE, Boolean.class));
			propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("adaptateur","Adpatateurs",Boolean.TRUE, Boolean.TRUE, Boolean.class));
			propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("codeLims","Code LIMS",Boolean.TRUE, Boolean.TRUE, Integer.class));
			return propertyDefinitions;
		}

		private static List<PropertyDefinition> getPropertyDefinitionsArn(){
			List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	        propertyDefinitions.addAll(getPropertyDefinitions());
	        propertyDefinitions.remove(DescriptionHelper.getPropertyDefinition("fragmente","Fragmenté",Boolean.TRUE, Boolean.TRUE, Boolean.class));
	        return propertyDefinitions;
		}
	

	public static List<PropertyDefinition> getPropertyDefinitionsADNClone() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitions());
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("tailleInsert","Taille d'insert",Boolean.TRUE, Boolean.TRUE,  Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("vecteur","Vecteur",Boolean.FALSE,Boolean.TRUE, Long.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("souche","Souche",Boolean.FALSE, Boolean.TRUE,  Double.class));		
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("siteClone","Site clone",Boolean.FALSE, Boolean.TRUE,  Double.class));

		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsAdn() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitions());
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("WGA","WGA",Boolean.FALSE, Boolean.TRUE,  Double.class));		
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionscDNA() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitions());
        //TODO julie liste de valeur
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("typeSynthese","Type de synthèse",Boolean.FALSE, Boolean.TRUE,  String.class));		
		return propertyDefinitions;
	}

	private static List<PropertyDefinition> getPropertyDefinitionsAmplicon() {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        propertyDefinitions.addAll(getPropertyDefinitions());
        //TODO julie
        // Specifier le type de donnees
        propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("materielCible","Matériel ciblé",Boolean.FALSE, Boolean.TRUE,  String.class));
        propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("nbRegion","Nombre régions ciblées",Boolean.FALSE, Boolean.TRUE,  Integer.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("regionCiblees","Région Ciblée",Boolean.FALSE, Boolean.TRUE,  String.class));
		
		return propertyDefinitions;

	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsImportBq() throws DAOException {

		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		propertyDefinitions.addAll(getPropertyDefinitionsImport());
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("index", "Index Illumina",Boolean.FALSE, String.class,DescriptionHelper.getListFromProcedureLims("pl_Tag")));

		return propertyDefinitions;
	}

	public static List<PropertyDefinition> getPropertyDefinitionsImportTara() {

		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();

		//TODO Add list values
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("station", "Station TARA",false,true,Integer.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("profondeur", "Profondeur TARA",false,true,String.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("filtre", "Filtre TARA",false,true,String.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("iteration", "Iteration TARA",false,true,Integer.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("materiel", "Materiel TARA",false,true,String.class));
		
		return propertyDefinitions;
	}
	
	public static Map<String,MeasureCategory> getMeasureCategoryAll(){
		Map<String,MeasureCategory> measureCategories=new HashMap<String, MeasureCategory>();
		
		MeasureCategory measureCategory1=DescriptionHelper.getMeasureCategory("concentration", "Concentration", "ng/ul", "ng/ul");
		MeasureCategory measureCategory2=DescriptionHelper.getMeasureCategory("volume", "Volume", "ul", "ul");
		MeasureCategory measureCategory3=DescriptionHelper.getMeasureCategory("quantity", "Quantité", "ng", "ng");
		MeasureCategory measureCategory4=DescriptionHelper.getMeasureCategory("taille", "Taille", "bases", "bases");

		measureCategories.put(measureCategory1.code, measureCategory1);
		measureCategories.put(measureCategory2.code, measureCategory2);
		measureCategories.put(measureCategory3.code, measureCategory3);
		measureCategories.put(measureCategory4.code, measureCategory4);
		
		return measureCategories;
	}
	
	public static List<PropertyDefinition> getPropertyDefinitionsImport() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		MeasureCategory measureCategory1=MeasureCategory.find.findByCode("concentration");		
		MeasureCategory measureCategory2=MeasureCategory.find.findByCode("quantity");
		MeasureCategory measureCategory3=MeasureCategory.find.findByCode("volume");
		
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("concFournisseur", "Concentration fournie", Double.class,Boolean.FALSE,measureCategory1,measureCategory1.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("quantiteFournisseur", "Quantite fournie",Double.class,Boolean.FALSE,measureCategory3,measureCategory3.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeFournisseur", "Volume fourni", Double.class,Boolean.FALSE,measureCategory2,measureCategory2.measurePossibleValues.get(0)));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("concReelle", "Concentration réelle",false,true,Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("quantiteReelle", "Quantite réelle",false,true, Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeReelle", "Volume réel", false,true, Double.class));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("dateReception", "Date réception", Date.class));

		return propertyDefinitions;
	}


	public static Map<String,ExperimentType> getExperimentType() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		Map<String,ExperimentType> results=new HashMap<String, ExperimentType>();

		//ExperimentType experimentTypeBq =DescriptionHelper.getExperimentType("receptionBanqueSolexa", "Reception Banque Solexa", "Librairie", null);
		ExperimentType experimentTypeBrut =DescriptionHelper.getExperimentType("voidExperimentType", "Sans Experiment Type", "Sample", null);
		// Librairie ??
		//ExperimentType experimentTypeFrag =DescriptionHelper.getExperimentType("receptionFragmentation", "Reception Fragmentation", "Librairie", null);

		//results.put(experimentTypeBq.code, experimentTypeBq);
		results.put(experimentTypeBrut.code, experimentTypeBrut);
		//results.put(experimentTypeFrag.code, experimentTypeFrag);
		return results;

	}

	public static Map<String,SampleType> getSampleTypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,SampleType> sampletypes=FirstData.getSampleAdn();
		sampletypes.putAll(FirstData.getSampleAdnClone());
		sampletypes.putAll(FirstData.getSampleAmplicon());
		sampletypes.putAll(FirstData.getSamplecDNA());
		sampletypes.putAll(FirstData.getSampleInconnu());
		sampletypes.putAll(FirstData.getSampleArn());
		sampletypes.putAll(FirstData.getSampleMatImmunoprecipites());
		return sampletypes;
	}

	public static Map<String,ImportType> getImportTypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,ImportType> importtypes=new HashMap<String, ImportType>();
		importtypes.put("importBanqueSolexa",DescriptionHelper.getImportType("importBanqueSolexa","Importation Sample Bq Solexa","importSample",getPropertyDefinitionsImportBq()));
		importtypes.put("importNormal",DescriptionHelper.getImportType("importNormal","Importation Normal","importSample",getPropertyDefinitionsImport()));
		importtypes.put("importTara",DescriptionHelper.getImportType("importTara","Importation Tara","importSample",getPropertyDefinitionsImportTara()));
		return importtypes;
	}


	public static Map<String,ProjectType> getProjectTypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String,ProjectType> projecttypes=new HashMap<String, ProjectType>();

		ProjectType projectType=DescriptionHelper.getProjectType("projectType", "Projet Type","Projet", null);

		projecttypes.put(projectType.code, projectType);
		return projecttypes;
	}

	public static Map<String,ContainerCategory> getContainerCategorieAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,ContainerCategory> containerCategories=new HashMap<String, ContainerCategory>();

		ContainerCategory containerCategoryTube=DescriptionHelper.getCategory(ContainerCategory.class,"Tube");
		ContainerCategory containerCategoryWell=DescriptionHelper.getCategory(ContainerCategory.class, "Well");
		ContainerCategory containerCategoryLane=DescriptionHelper.getCategory(ContainerCategory.class,"Lane");

		containerCategories.put(containerCategoryLane.code,containerCategoryLane);
		containerCategories.put(containerCategoryWell.code,containerCategoryWell);
		containerCategories.put(containerCategoryTube.code,containerCategoryTube);

		return containerCategories;
	}

	public static Map<String,ContainerSupportCategory> getContainerSupportCategoryAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,ContainerSupportCategory> containerCategories=new HashMap<String, ContainerSupportCategory>();

		ContainerSupportCategory containerSupport1=DescriptionHelper.getCategory(ContainerSupportCategory.class,"Sheet96");
		containerSupport1.nbColumn=8;
		containerSupport1.nbLine=12;
		containerSupport1.nbUsableContainer=96;
		ContainerSupportCategory containerSupport2=DescriptionHelper.getCategory(ContainerSupportCategory.class,"Sheet384");
		containerSupport2.nbColumn=16;
		containerSupport2.nbLine=24;
		containerSupport2.nbUsableContainer=384;

		//TODO plusieurs tailles
		ContainerSupportCategory containerSupport3=DescriptionHelper.getCategory(ContainerSupportCategory.class, "Strip");
		containerSupport3.nbColumn=1;
		containerSupport3.nbLine=8;
		containerSupport3.nbUsableContainer=8;

		//TODO difference entre Miseq et Hiseq
		ContainerSupportCategory containerSupport4=DescriptionHelper.getCategory(ContainerSupportCategory.class,"Flowcell8");
		containerSupport4.nbColumn=8;
		containerSupport4.nbLine=1;
		containerSupport4.nbUsableContainer=8;

		ContainerSupportCategory containerSupport5=DescriptionHelper.getCategory(ContainerSupportCategory.class,"Flowcell1");
		containerSupport5.nbColumn=1;
		containerSupport5.nbLine=1;
		containerSupport5.nbUsableContainer=1;

		containerCategories.put(containerSupport1.code,containerSupport1);
		containerCategories.put(containerSupport2.code,containerSupport2);
		containerCategories.put(containerSupport3.code,containerSupport3);
		containerCategories.put(containerSupport4.code,containerSupport4);
		containerCategories.put(containerSupport5.code,containerSupport5);

		return containerCategories;
	}


	public static Map<String, State> getStateAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String,State> states=new HashMap<String, State>();

		//TODO Probleme car existe dans les experiments , container et processus
		State stateN = DescriptionHelper.getState("N","New","containing");
		states.put(stateN.code,stateN);

		State stateA = DescriptionHelper.getState("A","Available","containing");
		states.put(stateA.code,stateA);

		State stateIWP = DescriptionHelper.getState("IWP","In Waiting Process","containing");
		states.put(stateIWP.code,stateIWP);

		State stateIW = DescriptionHelper.getState("IW","In Waiting","containing");
		states.put(stateIW.code,stateIW);

		State stateIU = DescriptionHelper.getState("IU","In Used","containing");
		states.put(stateIU.code,stateIU);

		State stateU = DescriptionHelper.getState("U","Unavailable","containing");
		states.put(stateU.code,stateU);

		State stateIS = DescriptionHelper.getState("IS","In Stock","containing");
		states.put(stateIS.code,stateIS);

		return states;
	}

	public static Map<String,ProcessType> getProcessTypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,ProcessType> process=new HashMap<String, ProcessType>();

		//TODO add ExperimentType
		ProcessType processType =  DescriptionHelper.getProcessType("bqMPIllumina","Banque Mate Pair Illumina",null,new ArrayList<ExperimentType>(getExperimentTypeBqMP().values())
				,getExperimentTypeBqMP().get("voidExperimentType"),getExperimentTypeBqMP().get("circularisationBqMP"),getExperimentTypeBqMP().get("ampliPCRBqMP"),null,null);
		process.put(processType.code, processType);

		return process;
	}


	public static Map<String,PurificationMethodType> getPurificationMethodtypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,PurificationMethodType> purifs=new HashMap<String, PurificationMethodType>();

		// Ampure NON Ampli
		List<PropertyDefinition> propertyDefinitions=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeInput", "Volume entrée", true, true, false, null, Long.class,"Volume de sortie de l'experience qui précède","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeMeasure", "Volume mesuré", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		//TODO
		//propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("nbBilles", "Nb billes", true, true, false, null, Interger.class);
		//TODO		
		//instrumentUsedTypes, protocols, resolutions);		
		PurificationMethodType purificationMethodType =  DescriptionHelper.getPurificationMethodType("ampureNonAmpli","Ampure NON AMPLI"
				, propertyDefinitions,null,null,new ArrayList<State>(getStateAll().values()),null);


		// Ampure Ampli
		List<PropertyDefinition> propertyDefinitions2=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeInput", "Volume entrée", true, true, false, null, Long.class,"Volume de sortie de l'experience qui précède","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeMeasure", "Volume mesuré", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		//TODO
		//propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("nbBilles", "Nb billes", true, true, false, null, Interger.class);
		//TODO		
		//instrumentUsedTypes, protocols, resolutions);		
		PurificationMethodType purificationMethodType2 =  DescriptionHelper.getPurificationMethodType("ampureAmpli","Ampure AMPLI"
				, propertyDefinitions2,null,null,new ArrayList<State>(getStateAll().values()),null);


		// Colonne Zimoclean
		List<PropertyDefinition> propertyDefinitions3=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		PurificationMethodType purificationMethodType3 =  DescriptionHelper.getPurificationMethodType("colonneZimoclean","Colonne Zimoclean"
				, propertyDefinitions3,null,null,new ArrayList<State>(getStateAll().values()),null);

		List<PropertyDefinition> propertyDefinitions4=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		PurificationMethodType purificationMethodType4 =  DescriptionHelper.getPurificationMethodType("purifADNBiotinyle","Purification ADN biotinylé"
				, propertyDefinitions4,null,null,new ArrayList<State>(getStateAll().values()),null);


		List<PropertyDefinition> propertyDefinitions5=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));
		PurificationMethodType purificationMethodType5 =  DescriptionHelper.getPurificationMethodType("nucleospin","Nucleospin"
				, propertyDefinitions5,null,null,new ArrayList<State>(getStateAll().values()),null);


		purifs.put(purificationMethodType.code, purificationMethodType);
		purifs.put(purificationMethodType2.code, purificationMethodType2);
		purifs.put(purificationMethodType3.code, purificationMethodType3);
		purifs.put(purificationMethodType4.code, purificationMethodType4);
		purifs.put(purificationMethodType5.code, purificationMethodType5);

		return purifs;
	}


	public static Map<String,QualityControlType> getQualityControlAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,QualityControlType> qc=new HashMap<String, QualityControlType>();

		// QC bioanalyzer Non ampli
		List<PropertyDefinition> propertyDefinitions=new ArrayList<PropertyDefinition>();				
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("volumeBioanalyzerInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("concentrationBioanalyzerInput", "Concentration entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("tailleBioanalyzerOutput", "Taille bioanalyzer", true, true, false, null, Integer.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("taille"),MeasureValue.find.findByCode("bases")));
		//Comment gérer les fichiers
		propertyDefinitions.add(DescriptionHelper.getPropertyDefinition("profilDNAHS", "Profil DNA HS", true, true, false, null, String.class,"",null, 1, false, "out", null,null, null,null));

		List<InstrumentUsedType> instrumentUsedTypes=DescriptionHelper.arrayToListType(InstrumentUsedType.class, new String[]{"agilent2100"});

		QualityControlType qctype1 =  DescriptionHelper.getQualityControlType("qcBionalyzerNonAmpli", "QC bionanalyzer Non Ampli", "Bioanalyzer", propertyDefinitions,  instrumentUsedTypes, null, new ArrayList<State>(getStateAll().values()), null);
		
		
		// QC bioanalyzer Ampli
		List<PropertyDefinition> propertyDefinitions2=new ArrayList<PropertyDefinition>();				
		propertyDefinitions2.add(DescriptionHelper.getPropertyDefinition("volumeBioanalyzerInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions2.add(DescriptionHelper.getPropertyDefinition("concentrationBioanalyzerInput", "Concentration entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));
		propertyDefinitions2.add(DescriptionHelper.getPropertyDefinition("tailleBioanalyzerOutput", "Taille bioanalyzer", true, true, false, null, Integer.class,"",null, 1, false, "out", null,null, MeasureCategory.find.findByCode("taille"),MeasureValue.find.findByCode("bases")));
		propertyDefinitions2.add(DescriptionHelper.getPropertyDefinition("profilDNAHS", "Profil DNA HS", true, true, false, null, String.class,"",null, 1, false, "out", null,null, null,null));

		QualityControlType qctype2 =  DescriptionHelper.getQualityControlType("qcBionalyzerAmpli", "QC bionanalyzer Ampli", "Bioanalyzer", propertyDefinitions2,  instrumentUsedTypes, null, new ArrayList<State>(getStateAll().values()), null);
		
		//QC Qubit
		List<PropertyDefinition> propertyDefinitions3=new ArrayList<PropertyDefinition>();				
		propertyDefinitions3.add(DescriptionHelper.getPropertyDefinition("volumeQubitInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions3.add(DescriptionHelper.getPropertyDefinition("concentrationQubitInput", "Concentration entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));

		propertyDefinitions3.add(DescriptionHelper.getPropertyDefinition("volumeQubitOutput", "Volume sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions3.add(DescriptionHelper.getPropertyDefinition("concentrationQubitOutput", "Concentration sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));

		//Manque Kit utilisé
		List<InstrumentUsedType> instrumentUsedTypes3=DescriptionHelper.arrayToListType(InstrumentUsedType.class, new String[]{"qubit"});
		
		QualityControlType qctype3 =  DescriptionHelper.getQualityControlType("qcQubit", "QC Qubit", "Qubit", propertyDefinitions3,  instrumentUsedTypes3, null, new ArrayList<State>(getStateAll().values()), null);

		
		// qPCR
		List<PropertyDefinition> propertyDefinitions4=new ArrayList<PropertyDefinition>();				
		propertyDefinitions4.add(DescriptionHelper.getPropertyDefinition("volumeQpcrInput", "Volume entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("volume"),MeasureValue.find.findByCode("ul")));			 			
		propertyDefinitions4.add(DescriptionHelper.getPropertyDefinition("concentrationQpcrInput", "Concentration entrée", true, true, false, null, Long.class,"","##,##", 1, false, "in", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));

		//Manque autres valeurs voir avec Julie
		propertyDefinitions4.add(DescriptionHelper.getPropertyDefinition("concentrationQpcrOutput", "Concentration sortie", true, true, false, null, Long.class,"","##,##", 1, false, "out", null,null, MeasureCategory.find.findByCode("concentration"),MeasureValue.find.findByCode("ng/ul")));

		List<InstrumentUsedType> instrumentUsedTypes4=DescriptionHelper.arrayToListType(InstrumentUsedType.class, new String[]{"tecan"});
		
		QualityControlType qctype4 =  DescriptionHelper.getQualityControlType("qcQubit", "QC Qubit", "Qubit", propertyDefinitions4,  instrumentUsedTypes4, null, new ArrayList<State>(getStateAll().values()), null);

		
		qc.put(qctype1.code, qctype1);
		qc.put(qctype2.code, qctype2);
		qc.put(qctype3.code, qctype3);
		qc.put(qctype4.code, qctype4);
		return qc;
	}



	public static LinkedHashMap<String, ExperimentType> getExperimentTypeBqMP() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		LinkedHashMap<String, ExperimentType> experimentTypes =  new  LinkedHashMap<String, ExperimentType>();

		
		//TODO pour toutes les ExperimentTypes
		//List<PropertyDefinition> propertyDefinitions
		//List<Protocol> protocol
		//List<State> states
		//List<Resolution> resolutions
		//List<PurificationMethodType> possiblePurificationMethodTypes
		//List<QualityControlType> possibleQualityControlTypes

		List<ExperimentType> previousExp=new ArrayList<ExperimentType>();
		
		ExperimentType prev =ExperimentType.find.findByCode("voidExperimentType");
		if(prev!=null){
			previousExp.add(prev);
		}
		ExperimentType circularisationBqP=DescriptionHelper.getExperimentType("circularisationBqMP", "Circularisation et digestion ADN linéaire + fragmentation ADN circulaire par covaris","Libraries"
				,null,DescriptionHelper.arrayToListType(InstrumentUsedType.class,new String[]{"covaris"}),null,null,null,true, true,true,true,null ,null ,previousExp);
		experimentTypes.put(circularisationBqP.code,circularisationBqP);

		List<ExperimentType> previousExp2=new ArrayList<ExperimentType>();
		previousExp2.add(circularisationBqP);
		ExperimentType librairieBqMP =DescriptionHelper.getExperimentType("librairieBqMP", "Librairie (rep, ajout du A, ligation adapt)","Libraries"
				,null,null,null,null,null,true, true,true,true,null ,null ,previousExp2); 
		experimentTypes.put(librairieBqMP.code,librairieBqMP);

		
		List<ExperimentType> previousExp3=new ArrayList<ExperimentType>();
		previousExp3.add(librairieBqMP);		
		experimentTypes.put("ampliPCRBqMP",DescriptionHelper.getExperimentType("ampliPCRBqMP", "Amplification (enrich par PCR)","Libraries"
				,null,DescriptionHelper.arrayToListType(InstrumentUsedType.class,new String[]{"thermocycleur"}),null,null,null,true, true,true,true,null ,null ,previousExp3));
		
		return experimentTypes;
	}

	//TODO
	public static Map<String,InstrumentUsedType> getInstrumentUsedTypeAll() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Map<String,InstrumentUsedType> ins=new HashMap<String, InstrumentUsedType>();


		//Thermo
		List<PropertyDefinition> listPropertyDefinitions=new ArrayList<PropertyDefinition>();
		listPropertyDefinitions.add(DescriptionHelper.getPropertyDefinition("nbCycle", "Nombre cycle", Integer.class));
		List<Instrument> instruments=new ArrayList<Instrument>();
		instruments.add(DescriptionHelper.getInstrument("thermo_s1"));
		instruments.add(DescriptionHelper.getInstrument("thermo_s2"));
		instruments.add(DescriptionHelper.getInstrument("thermo_s3"));
		instruments.add(DescriptionHelper.getInstrument("thermo_dev"));

		InstrumentUsedType instrumentUsedType1=DescriptionHelper.getInstrumentUsedType("thermocycleur", instruments,"thermocycleur",listPropertyDefinitions );


		//Covaris 
		List<PropertyDefinition> listPropertyDefinitions2=new ArrayList<PropertyDefinition>();
		//Add liste programme
		listPropertyDefinitions2.add(DescriptionHelper.getPropertyDefinition("programme", "Programme", String.class));
		List<Instrument> instruments2=new ArrayList<Instrument>();
		instruments2.add(DescriptionHelper.getInstrument("covaris1"));
		InstrumentUsedType instrumentUsedType2=DescriptionHelper.getInstrumentUsedType("covaris",instruments2,"covaris",listPropertyDefinitions2);


		//Agilent 
		List<PropertyDefinition> listPropertyDefinitions3=new ArrayList<PropertyDefinition>();
		//Liste valeur à definir
		listPropertyDefinitions3.add(DescriptionHelper.getPropertyDefinition("typePuce", "Type puce", String.class));
		List<Instrument> instruments3=new ArrayList<Instrument>();
		instruments3.add(DescriptionHelper.getInstrument("bioanalyzer_1"));
		instruments3.add(DescriptionHelper.getInstrument("bioanalyzer_2"));
		instruments3.add(DescriptionHelper.getInstrument("bioanalyzer_3"));
		InstrumentUsedType instrumentUsedType3=DescriptionHelper.getInstrumentUsedType("agilent2100",instruments3,"agilent",listPropertyDefinitions3);

		//Qubit
		List<PropertyDefinition> listPropertyDefinitions4=new ArrayList<PropertyDefinition>();
		//Liste valeur à definir
		listPropertyDefinitions4.add(DescriptionHelper.getPropertyDefinition("kit", "kit utilisé", String.class));
		List<Instrument> instruments4=new ArrayList<Instrument>();
		instruments4.add(DescriptionHelper.getInstrument("qubit_1"));
		InstrumentUsedType instrumentUsedType4=DescriptionHelper.getInstrumentUsedType("qubit",instruments4,"qubit",listPropertyDefinitions4);
		
		
		List<PropertyDefinition> listPropertyDefinitions5=new ArrayList<PropertyDefinition>();
		//Liste valeur à definir
		listPropertyDefinitions5.add(DescriptionHelper.getPropertyDefinition("typePuce", "Type puce", String.class));
		List<Instrument> instruments5=new ArrayList<Instrument>();
		instruments5.add(DescriptionHelper.getInstrument("tecan_1"));
		InstrumentUsedType instrumentUsedType5=DescriptionHelper.getInstrumentUsedType("tecan",instruments5,"tecan",listPropertyDefinitions5);
		
		ins.put(instrumentUsedType1.code,instrumentUsedType1);
		ins.put(instrumentUsedType2.code,instrumentUsedType2);
		ins.put(instrumentUsedType3.code,instrumentUsedType3);
		ins.put(instrumentUsedType4.code,instrumentUsedType4);
		ins.put(instrumentUsedType5.code,instrumentUsedType5);
		return ins;
	}

	
	//Peut-etre pas necessaire à voir ....
	public static Map<String,Protocol> getProtocolAll(){
		return null;
	}
}
