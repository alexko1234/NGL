package services.description.sample;

import static services.description.DescriptionFactory.newImportType;
import static services.description.DescriptionFactory.newPropertiesDefinition;
import static services.description.DescriptionFactory.newValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.Constants;
import services.description.DescriptionFactory;
import services.description.common.LevelService;
import services.description.common.MeasureService;

public class ImportServiceCNS extends AbstractImportService {


	public void saveImportCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportCategory> l = new ArrayList<ImportCategory>();
		l.add(saveImportCategory("Sample Import", "sample-import"));
		DAOHelpers.saveModels(ImportCategory.class, l, errors);
	}

	public static ImportCategory saveImportCategory(String name, String code) {
		ImportCategory ic = DescriptionFactory.newSimpleCategory(ImportCategory.class,name, code);
		return ic;
	}

	
	public void saveImportTypes(
			Map<String, List<ValidationError>> errors) throws DAOException {
		List<ImportType> l = new ArrayList<ImportType>();
		
		
		//import-type for lims import
		l.add(newImportType("Defaut", "default-import", ImportCategory.find.findByCode("sample-import"), getImportDefaultLimsDefinitions(), getInstitutes(Constants.CODE.CNS)));
		l.add(newImportType("Banque", "library", ImportCategory.find.findByCode("sample-import"), getLibraryPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		l.add(newImportType("Tara", "tara-default", ImportCategory.find.findByCode("sample-import"), getTaraPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		l.add(newImportType("Banque tara", "tara-library", ImportCategory.find.findByCode("sample-import"), getLibraryTaraPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		
		//import-type for NGL import
		l.add(newImportType("Reception Tara Pacific", "reception-tara-pacific", ImportCategory.find.findByCode("sample-import"), getTaraReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		l.add(newImportType("Update Tara Pacific", "update-tara-pacific", ImportCategory.find.findByCode("sample-import"), getTaraUpdatePropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		
		l.add(newImportType("Reception d'ADN", "dna-reception", ImportCategory.find.findByCode("sample-import"), getDNAReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		l.add(newImportType("Reception d'Amplicon", "amplicon-reception", ImportCategory.find.findByCode("sample-import"), getAmpliconReceptionPropertyDefinitions(), getInstitutes(Constants.CODE.CNS)));
		
		
		
		DAOHelpers.saveModels(ImportType.class, l, errors);
		
	}

	
	
	private static List<PropertyDefinition> getImportDefaultLimsDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, null, null, "single", 1, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Content,Level.CODE.Sample),Double.class, true,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Taille d'insert", "insertSize", LevelService.getLevels(Level.CODE.Sample),Double.class, false,MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("kb"), MeasureUnit.find.findByCode("kb"), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Souche", "strain", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Site clone", "cloneSite", LevelService.getLevels(Level.CODE.Sample),String.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Fragmenté", "isFragmented", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Adaptateurs", "isAdapters", LevelService.getLevels(Level.CODE.Sample),Boolean.class, false, "single"));
		
		return propertyDefinitions;
	}
	private static List<PropertyDefinition> getLibraryPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getImportDefaultLimsDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie Tag", "tagCategory", LevelService.getLevels(Level.CODE.Content), String.class, true, getTagCategories(), "single"));
		
		return propertyDefinitions;
	}
	
	private static List<Value> getTagCategories(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("SINGLE-INDEX", "SINGLE-INDEX"));
		values.add(DescriptionFactory.newValue("DUAL-INDEX", "DUAL-INDEX"));
		values.add(DescriptionFactory.newValue("MID", "MID"));
		return values;	
	}
	
	private static List<PropertyDefinition> getTaraPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.addAll(getImportDefaultLimsDefinitions());
		propertyDefinitions.add(newPropertiesDefinition("Station TARA", "taraStation", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Integer.class, true, getTaraStationValues(), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Nom Profondeur TARA", "taraDepth", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true,getTaraDepthCodeValues(true), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Profondeur TARA", "taraDepthCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, getTaraDepthCodeValues(false),"single"));
		propertyDefinitions.add(newPropertiesDefinition("Nom Filtre TARA", "taraFilter", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true,getTaraFilterCodeValues(true), "single"));
		propertyDefinitions.add(newPropertiesDefinition("Filtre TARA", "taraFilterCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, getTaraFilterCodeValues(false),"single"));
		propertyDefinitions.add(newPropertiesDefinition("Iteration TARA", "taraIteration", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Materiel TARA", "taraSample", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Code Barre TARA", "taraBarCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, "single"));
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getTaraReceptionPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, null, 
				null, "single", 1, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Code Barre TARA", "taraBarCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, true, null, null, "single", 1, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Destination finale", "finalDestination", LevelService.getLevels(Level.CODE.Container), String.class, false, null, 
				getTaraPacificFinalDestination(), "single", 3, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Protocole TARA Pacific", "taraProtocol", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				getTaraPacificProtocolValues(), "single", 4, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("META", "meta", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Boolean.class, true, null,
				null, "single", 1, true, null, null));
		
		
		return propertyDefinitions;
	}
	
	
	private static List<PropertyDefinition> getTaraUpdatePropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, null,
				null, "single", 1, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Station TARA", "taraStation", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Integer.class, true, null, 
				getTaraPacificStationValues(), "single", 1, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Filtre TARA", "taraFilterCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				getTaraPacificFilterValues(), "single", 2, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Environnement TARA Pacific", "taraEnvironment", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				getTaraPacificEnvironmentValues(), "single", 3, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Protocole TARA Pacific", "taraProtocol", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				getTaraPacificProtocolValues(), "single", 4, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Destination finale", "finalDestination", LevelService.getLevels(Level.CODE.Container), String.class, false, null, 
				null, "single", 5, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("META", "meta", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Boolean.class, true, null, 
				null, "single", 1, true, null, null));
		
		return propertyDefinitions;
	}
	
	
	private static List<Value> getTaraPacificFinalDestination() {
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("Genoscope", "Genoscope"));
		values.add(DescriptionFactory.newValue("Roscoff", "Roscoff"));
		values.add(DescriptionFactory.newValue("Monaco", "Monaco"));
		values.add(DescriptionFactory.newValue("Criobe", "Criobe"));
		values.add(DescriptionFactory.newValue("Monaco/Nice", "Monaco/Nice"));
		values.add(DescriptionFactory.newValue("New York", "New York"));
		values.add(DescriptionFactory.newValue("Oregon", "Oregon"));
		values.add(DescriptionFactory.newValue("Italy", "Italy"));
		values.add(DescriptionFactory.newValue("Villefranche", "Villefranche"));
		values.add(DescriptionFactory.newValue("Ohio", "Ohio"));
		
		return values;	
	}

	
	private static List<Value> getTaraPacificProtocolValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("CORE", "IMG-(core)_Coral"));
		values.add(DescriptionFactory.newValue("CS10", "SEQ-(10g)a_Coral"));
		values.add(DescriptionFactory.newValue("CS40", "SEQ-(40g)_Coral"));
		values.add(DescriptionFactory.newValue("CS10", "SEQ-(10g)b_Coral"));
		values.add(DescriptionFactory.newValue("CS4L", "SEQ-(4g-lysing)_Coral"));
		values.add(DescriptionFactory.newValue("CS4", "SEQ-(4g)_Coral"));
		values.add(DescriptionFactory.newValue("CTAX", "IMG-(5g)_Coral"));
		values.add(DescriptionFactory.newValue("CREP", "IMG-(10g)_Coral"));
		values.add(DescriptionFactory.newValue("CTEM", "IMG-(1g)_Coral"));
		values.add(DescriptionFactory.newValue("MUC", "SEQ_Fish-Mucus"));
		values.add(DescriptionFactory.newValue("GT", "SEQ_Fish-Tract"));
		values.add(DescriptionFactory.newValue("GIL", "SEQ_Fish-Gill"));
		values.add(DescriptionFactory.newValue("FIN", "SEQ_Fish-Fin"));
		values.add(DescriptionFactory.newValue("OTO", "IMG_Fish-Otolith"));
		values.add(DescriptionFactory.newValue("SGS", "SEQ-IMG_SeaGrass"));
		values.add(DescriptionFactory.newValue("SGI", "IMG_SeaGrass"));
		values.add(DescriptionFactory.newValue("BDI", "IMG_BenthicDino"));
		values.add(DescriptionFactory.newValue("BDS", "SEQ_BenthicDino"));
		values.add(DescriptionFactory.newValue("S<02>", "SEQ_CW<0.22"));
		values.add(DescriptionFactory.newValue("S<02", "SEQ-(FeCl3)_W<0.22"));
		values.add(DescriptionFactory.newValue("FCM", "IMG-(gluta+poloxamer)_W<20"));
		values.add(DescriptionFactory.newValue("LIVE", "IMG-(live)_CW<20"));
		values.add(DescriptionFactory.newValue("SCG", "SEQ-IMG-(GB)_W<20"));
		values.add(DescriptionFactory.newValue("SEM", "IMG-(dry)_W0.22-20"));
		values.add(DescriptionFactory.newValue("FISH", "IMG-FISH-(dry)_W0.22-20"));
		values.add(DescriptionFactory.newValue("S023", "SEQ-(50L-or-15min)_W0.22-3"));
		values.add(DescriptionFactory.newValue("S320", "SEQ-(50L-or-15min)_W3-20"));
		values.add(DescriptionFactory.newValue("H20", "IMG-(gluta+pfa)_N20-2000"));
		values.add(DescriptionFactory.newValue("LIVE20", "IMG-(live)_N20-2000"));
		values.add(DescriptionFactory.newValue("S20", "SEQ-(500mL-or-15min)_N20-2000"));
		values.add(DescriptionFactory.newValue("E20", "SEQ-IMG-(ethanol)_N20-2000"));
		values.add(DescriptionFactory.newValue("SCG20", "SEQ-IMG-(GB)_N20-2000"));
		values.add(DescriptionFactory.newValue("HPLC", "BGC-PIGMENTS_W>0.7"));
		values.add(DescriptionFactory.newValue("CARB", "BGC-CARBONATE_W<>"));
		values.add(DescriptionFactory.newValue("NUT", "BGC-NUTRIENTS_W<>"));
		values.add(DescriptionFactory.newValue("SSED", "SEQ-SED"));
		values.add(DescriptionFactory.newValue("F20", "IMG-(formol)_N20-2000"));
		values.add(DescriptionFactory.newValue("L20", "IMG-(lugol)_N20-2000"));
		values.add(DescriptionFactory.newValue("F200", "IMG-(formol)_N>200"));
		values.add(DescriptionFactory.newValue("S300", "SEQ-(500mL-or-15min)_N300-2000"));
		values.add(DescriptionFactory.newValue("E300", "SEQ-IMG-(ethanol)_N300-2000"));
		values.add(DescriptionFactory.newValue("F300", "IMG-(formol)_N>300"));
		values.add(DescriptionFactory.newValue("F2000", "IMG-(formol)_N>2000"));
		values.add(DescriptionFactory.newValue("IRON", "BGC-IRON_W<>"));
		values.add(DescriptionFactory.newValue("SAL", "BGC-SALINITY_W<>"));
		values.add(DescriptionFactory.newValue("ABS", "BGC-AEROSOL_A>0.7"));
		values.add(DescriptionFactory.newValue("AI", "IMG-AEROSOL_A>0.7"));
		values.add(DescriptionFactory.newValue("AS", "SEQ-AEROSOL_A>0.7"));
		return values;	
	}
	
	
	
	private static List<Value> getTaraPacificEnvironmentValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("AW", "Ambient Water"));
		values.add(DescriptionFactory.newValue("IW", "Interstitial Water"));
		values.add(DescriptionFactory.newValue("OA", "Ocean Atmosphere"));
		return values;	
	}
	
	
	
	private static List<Value> getTaraPacificFilterValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("S<02", "< 0,2 µm"));
		values.add(DescriptionFactory.newValue("S023", "0,2-3 µm"));
		values.add(DescriptionFactory.newValue("S320", "3-20 µm"));
		values.add(DescriptionFactory.newValue("S20", "20-200 µm"));
		values.add(DescriptionFactory.newValue("S300", "> 300µm"));
		values.add(DescriptionFactory.newValue("ASEQ", "ASEQ"));
		return values;	
	}

	private static List<Value> getTaraPacificStationValues(){
		List<Value> values = new ArrayList<Value>();
		//Miami first reception end june 
		//toutes les OA-001 à 027
		
		for(int i = 0; i <= 27; i++){
			if(i < 10){
				values.add(DescriptionFactory.newValue("2000"+i, "OA-00"+i));
			}else{
				values.add(DescriptionFactory.newValue("200"+i, "OA-0"+i));
			}
			
		}
		values.add(DescriptionFactory.newValue("20999", "OA-999"));
		//Panama second recption first august
		//toutes les stations des îles 01 à 20, pour les sites 01 à 03, pour les colonies 01 à 40
		//version 6 et version 9 I01S01 et I01S01C01 à I01S01C40
		
		values.add(DescriptionFactory.newValue("100000000", "I00S00C00"));	
		for(int i = 1; i <= 20; i++){
			String ile = (i < 10)?"0"+i:""+i;
			for(int j = 1; j <= 3; j++){
				String site = "0"+j;
				values.add(DescriptionFactory.newValue("1"+ile+"0"+site, "I"+ile+"S"+site));
				for(int k = 1; k <= 40; k++){
					String colonnie = (k < 10)?"0"+k:""+k;
					values.add(DescriptionFactory.newValue("1"+ile+"0"+site+"0"+colonnie, "I"+ile+"S"+site+"C"+colonnie));					
				}
			}
		}
		
		//toutes les stations des îles 01 à 20, pour les sites 01 à 03, pour les fish 01 à 10
		//version 9
		//I01S01F01 à I01S01F10
		values.add(DescriptionFactory.newValue("100000100", "I00S00F00"));	
		for(int i = 1; i <= 20; i++){
			String ile = (i < 10)?"0"+i:""+i;
			for(int j = 1; j <= 3; j++){
				String site = "0"+j;
				for(int k = 1; k <= 15; k++){
					String fish = (k < 10)?"0"+k:""+k;
					values.add(DescriptionFactory.newValue("1"+ile+"0"+site+"1"+fish, "I"+ile+"S"+site+"F"+fish));					
				}
			}
		}
		
		/*
		values.add(DescriptionFactory.newValue("102001", "I02S01"));
		values.add(DescriptionFactory.newValue("102001001", "I02S01C01"));
		values.add(DescriptionFactory.newValue("102001011", "I02S01C11"));
		values.add(DescriptionFactory.newValue("102001012", "I02S01C12"));
		values.add(DescriptionFactory.newValue("102001002", "I02S01C02"));
		values.add(DescriptionFactory.newValue("102001029", "I02S01C29"));
		values.add(DescriptionFactory.newValue("102001030", "I02S01C30"));
		values.add(DescriptionFactory.newValue("102001031", "I02S01C31"));
		values.add(DescriptionFactory.newValue("102001032", "I02S01C32"));
		values.add(DescriptionFactory.newValue("102003", "I02S03"));
		values.add(DescriptionFactory.newValue("102003001", "I02S03C01"));
		values.add(DescriptionFactory.newValue("102003010", "I02S03C10"));
		values.add(DescriptionFactory.newValue("102003101", "I02S03F01"));
		values.add(DescriptionFactory.newValue("102003102", "I02S03F02"));
		*/
		
		return values;	
	}
	
	private static List<PropertyDefinition> getDNAReceptionPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, null, null, "single", 1, false, null, null));
		propertyDefinitions.add(newPropertiesDefinition("META", "meta", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Boolean.class, true, null, null, "single", 1, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("% GC théorique", "theoricalGCPercent", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Double.class, false, null, null, "single", 1, false, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Taille associée au taxon", "taxonSize", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Double.class, false, null, 
				null, MeasureCategory.find.findByCode(MeasureService.MEASURE_CAT_CODE_SIZE), MeasureUnit.find.findByCode("pb"), MeasureUnit.find.findByCode("pb"), "single", 1, false, null, null));
		
		return propertyDefinitions;
	}
	
	private static List<PropertyDefinition> getAmpliconReceptionPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(newPropertiesDefinition("Date de réception", "receptionDate", LevelService.getLevels(Level.CODE.Container), Date.class, true, null, null, "single", 1, false, null, null));
		propertyDefinitions.add(newPropertiesDefinition("META", "meta", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), Boolean.class, true, null, null, "single", 1, true, null, null));
		
		propertyDefinitions.add(newPropertiesDefinition("Amorces", "amplificationPrimers", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				DescriptionFactory.newValues("Fuhrman primer","V9 primer", "16S primer + Fuhrman primer"), null, null, null,"single", 2, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Région ciblée", "targetedRegion", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				DescriptionFactory.newValues("16S_V4V5","18S_V9", "16S_Full Length + 16S_V4V5"), null, null, null,"single", 3, true, null,null));
		propertyDefinitions.add(newPropertiesDefinition("Taille amplicon attendue", "expectedAmpliconSize", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				null,null,null,null,"single", 16, true, null, null));
		propertyDefinitions.add(newPropertiesDefinition("Issu du type d'échantillon", "fromSampleTypeCode", LevelService.getLevels(Level.CODE.Sample,Level.CODE.Content), String.class, false, null, 
				null,null,null,null,"single", 16, true, null, null));
		
		return propertyDefinitions;
	}
	
	
	private static List<Value> getTaraStationValues(){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("3", "Station3"));
		values.add(DescriptionFactory.newValue("4", "Station4"));
		values.add(DescriptionFactory.newValue("5", "Station5"));
		values.add(DescriptionFactory.newValue("6", "Station6"));
		values.add(DescriptionFactory.newValue("7", "Station7"));
		values.add(DescriptionFactory.newValue("9", "Station9"));
		values.add(DescriptionFactory.newValue("11", "Station11"));
		values.add(DescriptionFactory.newValue("16", "Station16"));
		values.add(DescriptionFactory.newValue("18", "Station18"));
		values.add(DescriptionFactory.newValue("19", "Station19"));
		values.add(DescriptionFactory.newValue("20", "Station20"));
		values.add(DescriptionFactory.newValue("22", "Station22"));
		values.add(DescriptionFactory.newValue("23", "Station23"));
		values.add(DescriptionFactory.newValue("24", "Station24"));
		values.add(DescriptionFactory.newValue("25", "Station25"));
		values.add(DescriptionFactory.newValue("26", "Station26"));
		values.add(DescriptionFactory.newValue("30", "Station30"));
		values.add(DescriptionFactory.newValue("31", "Station31"));
		values.add(DescriptionFactory.newValue("32", "Station32"));
		values.add(DescriptionFactory.newValue("33", "Station33"));
		values.add(DescriptionFactory.newValue("34", "Station34"));
		values.add(DescriptionFactory.newValue("36", "Station36"));
		values.add(DescriptionFactory.newValue("37", "Station37"));
		values.add(DescriptionFactory.newValue("38", "Station38"));
		values.add(DescriptionFactory.newValue("39", "Station39"));
		values.add(DescriptionFactory.newValue("40", "Station40"));
		values.add(DescriptionFactory.newValue("41", "Station41"));
		values.add(DescriptionFactory.newValue("42", "Station42"));
		values.add(DescriptionFactory.newValue("43", "Station43"));
		values.add(DescriptionFactory.newValue("44", "Station44"));
		values.add(DescriptionFactory.newValue("45", "Station45"));
		values.add(DescriptionFactory.newValue("46", "Station46"));
		values.add(DescriptionFactory.newValue("47", "Station47"));
		values.add(DescriptionFactory.newValue("48", "Station48"));
		values.add(DescriptionFactory.newValue("49", "Station49"));
		values.add(DescriptionFactory.newValue("50", "Station50"));
		values.add(DescriptionFactory.newValue("51", "Station51"));
		values.add(DescriptionFactory.newValue("52", "Station52"));
		values.add(DescriptionFactory.newValue("57", "Station57"));
		values.add(DescriptionFactory.newValue("58", "Station58"));
		values.add(DescriptionFactory.newValue("64", "Station64"));
		values.add(DescriptionFactory.newValue("65", "Station65"));
		values.add(DescriptionFactory.newValue("66", "Station66"));
		values.add(DescriptionFactory.newValue("67", "Station67"));
		values.add(DescriptionFactory.newValue("68", "Station68"));
		values.add(DescriptionFactory.newValue("70", "Station70"));
		values.add(DescriptionFactory.newValue("72", "Station72"));
		values.add(DescriptionFactory.newValue("76", "Station76"));
		values.add(DescriptionFactory.newValue("78", "Station78"));
		values.add(DescriptionFactory.newValue("82", "Station82"));
		values.add(DescriptionFactory.newValue("84", "Station84"));
		values.add(DescriptionFactory.newValue("85", "Station85"));
		values.add(DescriptionFactory.newValue("98", "Station98"));
		values.add(DescriptionFactory.newValue("100", "Station100"));
		values.add(DescriptionFactory.newValue("102", "Station102"));
		values.add(DescriptionFactory.newValue("109", "Station109"));
		values.add(DescriptionFactory.newValue("1000", "Moorea"));
		values.add(DescriptionFactory.newValue("10001", "Villefranche"));
		values.add(DescriptionFactory.newValue("10002", "Elat"));
		values.add(DescriptionFactory.newValue("10003", "Toulon"));
		values.add(DescriptionFactory.newValue("111", "Station111"));
		values.add(DescriptionFactory.newValue("122", "Station122"));
		values.add(DescriptionFactory.newValue("123", "Station123"));
		values.add(DescriptionFactory.newValue("124", "Station124"));
		values.add(DescriptionFactory.newValue("125", "Station125"));
		values.add(DescriptionFactory.newValue("8", "Station8"));
		values.add(DescriptionFactory.newValue("10", "Station10"));
		values.add(DescriptionFactory.newValue("14", "Station14"));
		values.add(DescriptionFactory.newValue("15", "Station15"));
		values.add(DescriptionFactory.newValue("17", "Station17"));
		values.add(DescriptionFactory.newValue("21", "Station21"));
		values.add(DescriptionFactory.newValue("93", "Station93"));
		values.add(DescriptionFactory.newValue("95", "Station95"));
		values.add(DescriptionFactory.newValue("97", "Station97"));
		values.add(DescriptionFactory.newValue("106", "Station106"));
		values.add(DescriptionFactory.newValue("110", "Station110"));
		values.add(DescriptionFactory.newValue("113", "Station113"));
		values.add(DescriptionFactory.newValue("114", "Station114"));
		values.add(DescriptionFactory.newValue("115", "Station115"));
		values.add(DescriptionFactory.newValue("116", "Station116"));
		values.add(DescriptionFactory.newValue("117", "Station117"));
		values.add(DescriptionFactory.newValue("118", "Station118"));
		values.add(DescriptionFactory.newValue("128", "Station128"));
		values.add(DescriptionFactory.newValue("131", "Station131"));
		values.add(DescriptionFactory.newValue("135", "Station135"));
		values.add(DescriptionFactory.newValue("112", "Station112"));
		values.add(DescriptionFactory.newValue("53", "Station53"));
		values.add(DescriptionFactory.newValue("54", "Station54"));
		values.add(DescriptionFactory.newValue("56", "Station56"));
		values.add(DescriptionFactory.newValue("62", "Station62"));
		values.add(DescriptionFactory.newValue("71", "Station71"));
		values.add(DescriptionFactory.newValue("80", "Station80"));
		values.add(DescriptionFactory.newValue("81", "Station81"));
		values.add(DescriptionFactory.newValue("83", "Station83"));
		values.add(DescriptionFactory.newValue("86", "Station86"));
		values.add(DescriptionFactory.newValue("87", "Station87"));
		values.add(DescriptionFactory.newValue("89", "Station89"));
		values.add(DescriptionFactory.newValue("90", "Station90"));
		values.add(DescriptionFactory.newValue("92", "Station92"));
		values.add(DescriptionFactory.newValue("94", "Station94"));
		values.add(DescriptionFactory.newValue("88", "Station88"));
		values.add(DescriptionFactory.newValue("91", "Station91"));
		values.add(DescriptionFactory.newValue("12", "Station12"));
		values.add(DescriptionFactory.newValue("1", "Station1"));
		values.add(DescriptionFactory.newValue("2", "Station2"));
		values.add(DescriptionFactory.newValue("96", "Station96"));
		values.add(DescriptionFactory.newValue("99", "Station99"));
		values.add(DescriptionFactory.newValue("119", "Station119"));
		values.add(DescriptionFactory.newValue("120", "Station120"));
		values.add(DescriptionFactory.newValue("121", "Station121"));
		values.add(DescriptionFactory.newValue("126", "Station126"));
		values.add(DescriptionFactory.newValue("127", "Station127"));
		values.add(DescriptionFactory.newValue("129", "Station129"));
		values.add(DescriptionFactory.newValue("132", "Station132"));
		values.add(DescriptionFactory.newValue("133", "Station133"));
		values.add(DescriptionFactory.newValue("134", "Station134"));
		values.add(DescriptionFactory.newValue("136", "Station136"));
		values.add(DescriptionFactory.newValue("137", "Station137"));
		values.add(DescriptionFactory.newValue("138", "Station138"));
		values.add(DescriptionFactory.newValue("139", "Station139"));
		values.add(DescriptionFactory.newValue("140", "Station140"));
		values.add(DescriptionFactory.newValue("141", "Station141"));
		values.add(DescriptionFactory.newValue("142", "Station142"));
		values.add(DescriptionFactory.newValue("143", "Station143"));
		values.add(DescriptionFactory.newValue("144", "Station144"));
		values.add(DescriptionFactory.newValue("145", "Station145"));
		values.add(DescriptionFactory.newValue("146", "Station146"));
		values.add(DescriptionFactory.newValue("147", "Station147"));
		values.add(DescriptionFactory.newValue("148", "Station148"));
		values.add(DescriptionFactory.newValue("149", "Station149"));
		values.add(DescriptionFactory.newValue("151", "Station151"));
		values.add(DescriptionFactory.newValue("152", "Station152"));
		values.add(DescriptionFactory.newValue("153", "Station153"));
		values.add(DescriptionFactory.newValue("150", "Station150"));
		values.add(DescriptionFactory.newValue("130", "Station130"));
		values.add(DescriptionFactory.newValue("155", "Station155"));
		values.add(DescriptionFactory.newValue("158", "Station158"));
		values.add(DescriptionFactory.newValue("163", "Station163"));
		values.add(DescriptionFactory.newValue("168", "Station168"));
		values.add(DescriptionFactory.newValue("173", "Station173"));
		values.add(DescriptionFactory.newValue("175", "Station175"));
		values.add(DescriptionFactory.newValue("178", "Station178"));
		values.add(DescriptionFactory.newValue("180", "Station180"));
		values.add(DescriptionFactory.newValue("188", "Station188"));
		values.add(DescriptionFactory.newValue("189", "Station189"));
		values.add(DescriptionFactory.newValue("191", "Station191"));
		values.add(DescriptionFactory.newValue("193", "Station193"));
		values.add(DescriptionFactory.newValue("194", "Station194"));
		values.add(DescriptionFactory.newValue("196", "Station196"));
		values.add(DescriptionFactory.newValue("201", "Station201"));
		values.add(DescriptionFactory.newValue("205", "Station205"));
		values.add(DescriptionFactory.newValue("206", "Station206"));
		values.add(DescriptionFactory.newValue("208", "Station208"));
		values.add(DescriptionFactory.newValue("209", "Station209"));
		values.add(DescriptionFactory.newValue("210", "Station210"));
		return values;
		
	}
	
	private static List<Value> getTaraDepthCodeValues(boolean reverse){
		List<Value> values = new ArrayList<Value>();
		values.add(DescriptionFactory.newValue("COR", "Coral"));
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
		values.add(DescriptionFactory.newValue("SKT", "Skeleton and Tissues"));
		values.add(DescriptionFactory.newValue("SOD", "Surface OMZ and DCM Pool"));
		values.add(DescriptionFactory.newValue("SOP", "Surface and OMZ Pool"));
		values.add(DescriptionFactory.newValue("SUR", "Surface"));
		values.add(DescriptionFactory.newValue("SWR", "Sweet water rinse"));
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
		propertyDefinitions.add(newPropertiesDefinition("Tag", "tag", LevelService.getLevels(Level.CODE.Content), String.class, true, "single"));
		propertyDefinitions.add(newPropertiesDefinition("Catégorie Tag", "tagCategory", LevelService.getLevels(Level.CODE.Content), String.class, true, getTagCategories(), "single"));
		
		return propertyDefinitions;
	}
	
	public static List<Institute> getInstitutes(Constants.CODE...codes) throws DAOException {
		List<Institute> institutes = new ArrayList<Institute>();
		for(Constants.CODE code : codes){
			institutes.add(Institute.find.findByCode(code.name()));
		}
		return institutes;
	}
	
}
