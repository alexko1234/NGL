package models.utils.instance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;

public class ContainerHelper {


	public static void addContent(Container container, Sample sample) throws DAOException{
		addContent(container,sample,null);
	}
	
	public static void addContent(Container container,Sample sample,Map<String,PropertyValue> properties) throws DAOException{

		Content sampleUsed =new Content(sample.code, sample.typeCode, sample.categoryCode);

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){

			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,sampleUsed.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,sampleUsed.properties);
		}

		if(properties!=null)
			sampleUsed.properties.putAll(properties);
		
		container.contents.add(sampleUsed);

		container.projectCodes=InstanceHelpers.addCodesList(sample.projectCodes,container.projectCodes);

		container.sampleCodes=InstanceHelpers.addCode(sample.code,container.sampleCodes);

	}


	public static void addContent(Container inputContainer, Container outputContainer , Experiment experiment) {
		
		//Copy all properties
		outputContainer.contents.addAll(inputContainer.contents);
		outputContainer.projectCodes=InstanceHelpers.addCodesList(inputContainer.projectCodes,outputContainer.projectCodes);
		outputContainer.sampleCodes=InstanceHelpers.addCodesList(inputContainer.sampleCodes,outputContainer.sampleCodes);
		outputContainer.categoryCode = experiment.instrument.outContainerSupportCategoryCode;
		
		if(experiment.categoryCode.equals("transformation")){
			outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCode(experiment.typeCode ,outputContainer.fromExperimentTypeCodes);
		}else{
			outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCodesList(inputContainer.fromExperimentTypeCodes,outputContainer.fromExperimentTypeCodes);

		}
	
	}

	//TODO 
	/*public static void addContainerSupport(Container container,Experiment experiment){

		if(container.support==null){
			container.support=new LocationOnContainerSupport();
		}

		if(experiment.instrument.outContainerSupportCategoryCode==null || experiment.instrument.outContainerSupportCategoryCode.equals("tube")){
			container.support=ContainerSupportHelper.getContainerSupportTube(container.code);
		}else{
			//TODO 
			LocationOnContainerSupport containerSupport=new LocationOnContainerSupport();
			containerSupport.code=getSupportCode(experiment.instrument.outContainerSupportCategoryCode);	
			containerSupport.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
			containerSupport.column="?";
			containerSupport.line="?";
			
			container.support = containerSupport;
			Logger.info("Not implemented");
		}
	}*/


	///public static void addContaineSupport(Container container,)


	/*	//Copy properties values from lists of properties definition and properties values in object Container
	// if the level of the property definition contains "content" the property value is copied in all properties map of container.contents
	// if the level contains "container" the property value is copied in properties map in container
	public static void copyProperties(
			Map<String, PropertyValue> propertyValues, Container outputContainer, ExperimentType experimentType) {


		for(int i=0;i<outputContainer.contents.size();i++){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.Content), propertyValues, outputContainer.contents.get(i).properties);
		}

		InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.ContainerOut), propertyValues, outputContainer.properties);

	}
	 */
	
	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMddHHmmss");
	}

	public static String generateContainerCode(String categoryCode){
		return (categoryCode+"-"+getSimpleDateFormat().format(new Date())).toUpperCase();
	}
	
	public static void createSupportFromContainers(List<Container> containers,ContextValidation contextValidation){
		
		HashMap<String,ContainerSupport> mapSupports = new HashMap<String,ContainerSupport>();
		
		for (Container container : containers) {
			if (container.support != null) {
				ContainerSupport newSupport = ContainerSupportHelper.createSupport(container.support.code, container.support.categoryCode,"ngl");
				newSupport.projectCodes = new  ArrayList<String>(container.projectCodes);
				newSupport.sampleCodes = new  ArrayList<String>(container.sampleCodes);							

				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					InstanceHelpers.addCodesList(newSupport.projectCodes, oldSupport.projectCodes); 
					InstanceHelpers.addCodesList(newSupport.sampleCodes, oldSupport.sampleCodes);
				}
				
			}
		}
	
		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, new ArrayList<ContainerSupport>(mapSupports.values()), contextValidation, true);
	
	}

	public static List<Content> contentFromSampleCode(List<Content> contents,
			String sampleCode) {
		List<Content> contentsFind=new ArrayList<Content>();
		for(Content content:contents){
			if(content.sampleCode.equals(sampleCode)){
				contentsFind.add(content);
			}
		}
		return contentsFind;
	}

}
