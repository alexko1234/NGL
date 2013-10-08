package validation.utils;

import java.util.ArrayList;
import java.util.List;

import play.i18n.Messages;

import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;

public class RunPropertyDefinitionHelper {
	
	private static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Boolean choiceInList, Class<? extends Object> type) {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.code = keyCode;
		propertyDefinition.name = keyName;
		propertyDefinition.required = required;
		propertyDefinition.active = active;
		propertyDefinition.choiceInList = choiceInList;
		propertyDefinition.valueType = type.getName();		
		return propertyDefinition;
	}

	
	
	public static List<PropertyDefinition> getRunNGSRGPropertyDefinitions(){
		/*
		 * properties.nbClusterTotal
		   properties.nbBase
		   properties.flowcellPosition
		   properties.rtaVersion
		   properties.flowcellVersion
		   properties.controlLane
		   properties.mismatch
		 */
		
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("nbClusterTotal","nbClusterTotal",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));		
		propertyDefinitions.add(getPropertyDefinition("nbCycle","nbCycle",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbBase","nbBase",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("flowcellPosition","flowcellPosition",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("rtaVersion","rtaVersion",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("flowcellVersion","flowcellVersion",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("controlLane","controlLane",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("mismatch","mismatch",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.class));
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getRunPropertyDefinitions(){
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		return propertyDefinitions;
	}

	public static List<PropertyDefinition> getLaneNGSRGPropertyDefinitions(){
		/*
			properties.nbCycleRead1
			properties.nbCycleReadIndex1
			properties.nbCycleRead2
			properties.nbCycleReadIndex2
			properties.nbCluster
			properties.nbBaseInternalAndIlluminaFilter
			properties.phasing
			properties.prephasing
			properties.nbClusterInternalAndIlluminaFilter
			properties.percentClusterInternalAndIlluminaFilter
			properties.nbClusterIlluminaFilter
			properties.percentClusterIlluminaFilter

		 */
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("nbCycleRead1","nbCycleRead1",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("nbCycleReadIndex1","nbCycleReadIndex1",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("nbCycleRead2","nbCycleRead2",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("nbCycleReadIndex2","nbCycleReadIndex2",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("nbCluster","nbCluster",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("phasing","phasing",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("prephasing","prephasing",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbClusterIlluminaFilter","nbClusterIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));		
		propertyDefinitions.add(getPropertyDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));
		
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getLanePropertyDefinitions(){
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getReadSetNGSRGPropertyDefinitions(){
		/*
		
			properties.nbClusterInternalAndIlluminaFilter
			properties.nbBaseInternalandIlluminaFilter
			properties.fraction
			properties.insertLength
			properties.nbUsableBase
			properties.nbUsableCluster
			properties.q30
			properties.score
			properties.nbRead


		 */
		/*
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("fraction","fraction",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));		
		//propertyDefinitions.add(getPropertyDefinition("insertLengthTh","insertLengthTh",Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, String.class)); //String because lims not valid
		//propertyDefinitions.add(getPropertyDefinition("nbUsableBase","nbUsableBase",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		//propertyDefinitions.add(getPropertyDefinition("nbUsableCluster","nbUsableCluster",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("q30","q30",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));
		propertyDefinitions.add(getPropertyDefinition("score","score",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));
		propertyDefinitions.add(getPropertyDefinition("nbRead","nbRead",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		 */
		
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("nbCluster","nbCluster",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbBases","nbBases",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("fraction","fraction",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));		
		//propertyDefinitions.add(getPropertyDefinition("insertLengthTh","insertLengthTh",Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, String.class)); //String because lims not valid
		//propertyDefinitions.add(getPropertyDefinition("nbUsableBase","nbUsableBase",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		//propertyDefinitions.add(getPropertyDefinition("nbUsableCluster","nbUsableCluster",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("Q30","Q30",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));
		propertyDefinitions.add(getPropertyDefinition("qualityScore","qualityScore",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Double.class));
		propertyDefinitions.add(getPropertyDefinition("nbReadIllumina","nbReadIllumina",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		
		return propertyDefinitions;
		
	}
	
	public static List<PropertyDefinition> getReadSetGlobalPropertyDefinitions(){
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("usefulSequences","usefulSequences",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("usefulBases","usefulBases",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		return propertyDefinitions;		
	}
	
	public static List<PropertyDefinition> getReadSetPropertyDefinitions(){
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getFilePropertyDefinitions(){
		/*
		 properties.asciiEncoding
		 properties.label
		 */
		
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("asciiEncoding",Messages.get("property.asciiEncoding.label"),Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("label",Messages.get("property.label.label"),Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		return propertyDefinitions;
	}

	public static List<PropertyDefinition> getLaneSAVPropertyDefinitions(){
		
		
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
	
		propertyDefinitions.add(getPropertyDefinition("clusterDensity","clusterDensity",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("clusterDensityStd","clusterDensityStd",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("clusterPFPerc","clusterPFPerc",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("clusterPFPercStd","clusterPFPercStd",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("phasing","phasing",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("prephasing","prephasing",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("reads","reads",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("readsPF","readsPF",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("greaterQ30Perc","greaterQ30Perc",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("cyclesErrRated","cyclesErrRated",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("alignedPerc","alignedPerc",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("alignedPercStd","alignedPercStd",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePerc","errorRatePerc",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercStd","errorRatePercStd",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle35","errorRatePercCycle35",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle35Std","errorRatePercCycle35Std",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle75","errorRatePercCycle75",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle75Std","errorRatePercCycle75Std",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle100","errorRatePercCycle100",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("errorRatePercCycle100Std","errorRatePercCycle100Std",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("intensityCycle1","intensityCycle1",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("intensityCycle1Std","IntensityCycle1Std",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("intensityCycle20Perc","intensityCycle20Perc",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("intensityCycle20PercStd","intensityCycle20PercStd",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		
		
		return propertyDefinitions;
		
	}

	public static List<PropertyDefinition> getTreatmentPropertyDefinitions(String treatmentCode, CODE levelCode) {
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		
		if(Level.CODE.ReadSet.equals(levelCode)){
			if(treatmentCode.equalsIgnoreCase("ngsrg")){
				propertyDefinitions = getReadSetNGSRGPropertyDefinitions();
			}else if(treatmentCode.equalsIgnoreCase("global")){
				propertyDefinitions = getReadSetGlobalPropertyDefinitions();
			}
		}else if(Level.CODE.Lane.equals(levelCode)){
			if(treatmentCode.equalsIgnoreCase("ngsrg")){
				propertyDefinitions = getLaneNGSRGPropertyDefinitions();
			}else if(treatmentCode.equalsIgnoreCase("sav")){
				propertyDefinitions =  getLaneSAVPropertyDefinitions();
			}
		}else if(Level.CODE.Run.equals(levelCode)){
			if(treatmentCode.equalsIgnoreCase("ngsrg")){
				propertyDefinitions = getRunNGSRGPropertyDefinitions();
			}
		}
		return propertyDefinitions;
	}


	static List<String> treatmentCodes = null;

	public static List<String> getTreatmentCodes() {
		if(null == treatmentCodes){
			treatmentCodes = new ArrayList<String>();
			treatmentCodes.add("ngsrg");
			treatmentCodes.add("global");
			treatmentCodes.add("sav");			
		}
		return treatmentCodes;
	}

	
	static List<String> treatmentTypeCodes = null;

	public static List<String> getTreatmentTypeCodes() {
		if(null == treatmentTypeCodes){
			treatmentTypeCodes = new ArrayList<String>();
			treatmentTypeCodes.add("ngsrg-illumina");
			treatmentTypeCodes.add("global");
			treatmentTypeCodes.add("sav");			
		}
		return treatmentTypeCodes;
	}
	
	
	static List<String> treatmentCatTypeCodes = null;

	public static List<String> getTreatmentCatTypeCodes() {
		if(null == treatmentCatTypeCodes){
			treatmentCatTypeCodes = new ArrayList<String>();
			treatmentCatTypeCodes.add("ngsrg");
			treatmentCatTypeCodes.add("global");
			treatmentCatTypeCodes.add("sequencing");			
		}
		return treatmentCatTypeCodes;
	}
	
	static List<String> treatmentContextCodes = null;
	public static List<String> getTreatmentContextCodes() {
		if(null == treatmentContextCodes){
			treatmentContextCodes = new ArrayList<String>();
			treatmentContextCodes.add("default");
			treatmentContextCodes.add("read1");
			treatmentContextCodes.add("read2");
			treatmentContextCodes.add("pairs");
			treatmentContextCodes.add("single");
			
		}
		return treatmentContextCodes;
	}
	
	
	static List<String> runStatesCode = null;
	public static List<String> getRunStateCodes() {
		if(null == runStatesCode){
			runStatesCode = new ArrayList<String>();
			runStatesCode.add("IP-S");
			runStatesCode.add("IP-RG");
			runStatesCode.add("F-RG");
			runStatesCode.add("F");			
		}
		return runStatesCode;
	}
	
	static List<String> readSetStatesCode = null;
	public static List<String> getReadSetStateCodes() {
		if(null == readSetStatesCode){
			readSetStatesCode = new ArrayList<String>();
			readSetStatesCode.add("IP-RG");
			readSetStatesCode.add("F-RG");
			readSetStatesCode.add("IW-QC");
			readSetStatesCode.add("F-QC");
			readSetStatesCode.add("A");
			readSetStatesCode.add("UA");
		}
		return readSetStatesCode;
	}
	
	static List<String> runTypeCode = null;
	public static List<String> getRunTypeCodes() {
		if(null == runTypeCode){
			runTypeCode = new ArrayList<String>();
			runTypeCode.add("RHS2000");
			runTypeCode.add("RHS2500");
			runTypeCode.add("RHS2500R");			
		}
		return runTypeCode;
	}
	
}
