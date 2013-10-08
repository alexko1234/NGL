package services.description.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.description.RunType;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import services.description.common.LevelService;

public class RunService {
	
	/*
			treatmentCodes : ngsrg, global, sav		

			treatmentTypeCodes : ngsrg-illumina, global, sav		

			treatmentCatTypeCodes : ngsrg, global, sequencing			

			treatmentContextCodes : default, read1, read2, pairs, single
			
			runStatesCode : IP_S, IP_RG, F_RG, F			

			readSetStatesCode : IP_RG, F_RG, IW_QC, F_QC, A, UA

			runTypeCode : RHS2000, RHS2500, RHS2500R	
			
		//getReadSetGlobalPropertyDefinitions : 
		"usefulSequences","usefulSequences",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
		"usefulBases","usefulBases",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class
	 */

	
	public static void main(Map<String, List<ValidationError>> errors)  throws DAOException{
		
		DAOHelpers.removeAll(ReadSetType.class, ReadSetType.find);
		DAOHelpers.removeAll(RunType.class, RunType.find);
		DAOHelpers.removeAll(RunCategory.class, RunCategory.find);
		DAOHelpers.removeAll(TreatmentContext.class, TreatmentContext.find);
		DAOHelpers.removeAll(TreatmentType.class, TreatmentType.find);
		DAOHelpers.removeAll(TreatmentCategory.class, TreatmentCategory.find);
				

		saveReadSetType(errors);
		
		saveRunCategories(errors);
		
		saveRunType(errors);
		
		saveTreatmentCategory(errors);
		
		saveTreatmentContext(errors);
		
		saveTreatmentType(errors);
		
	}
	
	
	
	public static void saveReadSetType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ReadSetType> l = new ArrayList<ReadSetType>();
		//newReadSetType(name, code, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newReadSetType("default-readset","default-readset",  getReadSetRGropertyDefinitions()));
		DAOHelpers.saveModels(ReadSetType.class, l, errors);
	}



	public static void saveRunCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunCategory> l = new ArrayList<RunCategory>();
		// name, code
		l.add(DescriptionFactory.newSimpleCategory(RunCategory.class, "Illumina", "rhs"));
		DAOHelpers.saveModels(RunCategory.class, l, errors);
	}
	
	public static void saveRunType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<RunType> l = new ArrayList<RunType>();
		//newRunType(String name, String code, Integer nbLanes, RunCategory category, List<PropertyDefinition> propertiesDefinitions)
		l.add(DescriptionFactory.newRunType("RHS2000","RHS2000", 8, RunCategory.find.findByCode("rhs"), getRunRGropertyDefinitions()));
		l.add(DescriptionFactory.newRunType("RHS2500","RHS2500", 8, RunCategory.find.findByCode("rhs"), getRunRGropertyDefinitions()));
		l.add(DescriptionFactory.newRunType("RHS2500R","RHS2500R", 2, RunCategory.find.findByCode("rhs"), getRunRGropertyDefinitions()));
		DAOHelpers.saveModels(RunType.class, l, errors);
	}
	
	public static void saveTreatmentCategory(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentCategory> l = new ArrayList<TreatmentCategory>();
		for (TreatmentCategory.CODE code : TreatmentCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(TreatmentCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(TreatmentCategory.class, l, errors);
	}
	
	
	public static void saveTreatmentContext(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentContext> l = new ArrayList<TreatmentContext>();
		l.add(DescriptionFactory.newTreatmentContext("default","default"));
		l.add(DescriptionFactory.newTreatmentContext("read1","read1"));
		l.add(DescriptionFactory.newTreatmentContext("read2","read2"));
		l.add(DescriptionFactory.newTreatmentContext("pairs","pairs"));
		l.add(DescriptionFactory.newTreatmentContext("single","single"));
		DAOHelpers.saveModels(TreatmentContext.class, l, errors);
	}
	
	
	public static void saveTreatmentType(Map<String, List<ValidationError>> errors) throws DAOException {
		List<TreatmentType> l = new ArrayList<TreatmentType>();
		//newTreatmentType(String name, String code, TreatmentCategory category, String names, List<PropertyDefinition> propertiesDefinitions, List<TreatmentContext>  contexts)
		l.add(DescriptionFactory.newTreatmentType("ngsrg-illumina","ngsrg-illumina", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.ngsrg.name()), "ngsrg-illumina", getNGSRGropertyDefinitions(), getTreatmentContexts("default", "read1", "read2", "pairs", "single")));
		l.add(DescriptionFactory.newTreatmentType("global","global", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.global.name()), "global", getReadSetGlobalPropertyDefinitions(), getTreatmentContexts("read1", "read2")));
		l.add(DescriptionFactory.newTreatmentType("sav","sav", TreatmentCategory.find.findByCode(TreatmentCategory.CODE.sequencing.name()), "sav", getLaneSAVPropertyDefinitions(), getTreatmentContexts("default")));
		DAOHelpers.saveModels(TreatmentType.class, l, errors);
	}
	
	
	private static List<TreatmentContext> getTreatmentContexts(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(TreatmentContext.class, TreatmentContext.find, codes);
	}


/*
	private static List<TreatmentType> getTreatmentTypes(String...codes) throws DAOException {
		return DAOHelpers.getModelByCodes(TreatmentType.class, TreatmentType.find, codes);
	}
*/
	public static List<PropertyDefinition> getNGSRGropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		//String name, String code, List<Level> levels, Class<?> type, Boolean required
        //Run level
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellPosition","flowcellPosition", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycle","nbCycle", LevelService.getLevels(Level.CODE.Run), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("flowcellVersion","flowcellVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Run, Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBase","nbBase", LevelService.getLevels(Level.CODE.Run), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("mismatch","mismatch", LevelService.getLevels(Level.CODE.Run), Boolean.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("controlLane","controlLane", LevelService.getLevels(Level.CODE.Run), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("rtaVersion","rtaVersion", LevelService.getLevels(Level.CODE.Run), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterTotal","nbClusterTotal", LevelService.getLevels(Level.CODE.Run), Long.class, true));
        // Lane level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing", LevelService.getLevels(Level.CODE.Lane), String.class, true));
        //propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing", LevelService.getLevels(Level.CODE.Lane), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex2","nbCycleReadIndex2", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead2","nbCycleRead2", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleRead1","nbCycleRead1", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCycleReadIndex1","nbCycleReadIndex1", LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
        
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter", LevelService.getLevels(Level.CODE.Lane), Long.class, true));
        // ReadSet level		
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbCluster","nbCluster", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("Q30","Q30", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbBases","nbBases", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("fraction","fraction", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("qualityScore","qualityScore", LevelService.getLevels(Level.CODE.ReadSet), Double.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("nbReadIllumina","nbReadIllumina", LevelService.getLevels(Level.CODE.ReadSet), Integer.class, true));
        // file level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("label","label", LevelService.getLevels(Level.CODE.File), String.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("asciiEncoding","asciiEncoding", LevelService.getLevels(Level.CODE.File), String.class, true));
        return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getReadSetGlobalPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
        // just readset level
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulSequences","usefulSequences", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("usefulBases","usefulBases", LevelService.getLevels(Level.CODE.ReadSet), Long.class, true));
        return propertyDefinitions;
	}
	
	
	public static List<PropertyDefinition> getLaneSAVPropertyDefinitions() throws DAOException {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterDensity","clusterDensity",LevelService.getLevels(Level.CODE.Lane), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterDensityStd","clusterDensityStd",LevelService.getLevels(Level.CODE.Lane), Long.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterPFPerc","clusterPFPerc",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("clusterPFPercStd","clusterPFPercStd",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("phasing","phasing",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("prephasing","prephasing",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("reads","reads",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("readsPF","readsPF",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("greaterQ30Perc","greaterQ30Perc",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("cyclesErrRated","cyclesErrRated",LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedPerc","alignedPerc",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("alignedPercStd","alignedPercStd",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePerc","errorRatePerc",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercStd","errorRatePercStd",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle35","errorRatePercCycle35",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle35Std","errorRatePercCycle35Std",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle75","errorRatePercCycle75",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle75Std","errorRatePercCycle75Std",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle100","errorRatePercCycle100",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("errorRatePercCycle100Std","errorRatePercCycle100Std",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle1","intensityCycle1",LevelService.getLevels(Level.CODE.Lane), Integer.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle1Std","intensityCycle1Std",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle20Perc","intensityCycle20Perc",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		propertyDefinitions.add(DescriptionFactory.newPropertiesDefinition("intensityCycle20PercStd","intensityCycle20PercStd",LevelService.getLevels(Level.CODE.Lane), Float.class, true));
		return propertyDefinitions;
	}
	
	
	//Data Test
	public static List<PropertyDefinition> getRunRGropertyDefinitions() throws DAOException {
        return null;
	}
	public static List<PropertyDefinition> getReadSetRGropertyDefinitions() throws DAOException {
        return null;
	}
	
}

