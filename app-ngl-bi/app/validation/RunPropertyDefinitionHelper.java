package validation;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.PropertyDefinition;

public class RunPropertyDefinitionHelper {
	
	private static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Boolean choiceInList, Class type) {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.code = keyCode;
		propertyDefinition.name = keyName;
		propertyDefinition.required = required;
		propertyDefinition.active = active;
		propertyDefinition.choiceInList = choiceInList;
		propertyDefinition.type = type.getName();		
		return propertyDefinition;
	}

	
	
	public static List<PropertyDefinition> getRunPropertyDefinitions(){
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
	
	
	

	public static List<PropertyDefinition> getLanePropertyDefinitions(){
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
		propertyDefinitions.add(getPropertyDefinition("percentClusterIlluminaFilter","percentClusterIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));		
		propertyDefinitions.add(getPropertyDefinition("percentClusterInternalAndIlluminaFilter","percentClusterInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		
		return propertyDefinitions;
	}
	
	public static List<PropertyDefinition> getReadSetPropertyDefinitions(){
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
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("nbClusterInternalAndIlluminaFilter","nbClusterInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbBaseInternalAndIlluminaFilter","nbBaseInternalAndIlluminaFilter",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("fraction","fraction",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));		
		propertyDefinitions.add(getPropertyDefinition("insertLength","insertLength",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));
		propertyDefinitions.add(getPropertyDefinition("nbUsableBase","nbUsableBase",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("nbUsableCluster","nbUsableCluster",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Long.class));
		propertyDefinitions.add(getPropertyDefinition("q30","q30",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("score","score",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Float.class));
		propertyDefinitions.add(getPropertyDefinition("nbRead","nbRead",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Integer.class));

		return propertyDefinitions;
		
	}
	
	public static List<PropertyDefinition> getFilePropertyDefinitions(){
		/*
		 properties.asciiEncoding
		 properties.label
		 */
		
		List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>();
		propertyDefinitions.add(getPropertyDefinition("asciiEncoding","asciiEncoding",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		propertyDefinitions.add(getPropertyDefinition("label","label",Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, String.class));
		return propertyDefinitions;
	}
}
