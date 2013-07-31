package models.utils.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import validation.utils.BusinessValidationHelper;

public class ContainerHelper {

	public static ContainerSupport getContainerSupportTube(String barCode){
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode="tube";
		containerSupport.x="1";
		containerSupport.y="1";
		return containerSupport;
	}


	public static void addContent(Container container,Sample sample) throws DAOException{

		//Create new content
		if(container.contents==null){
			container.contents=new ArrayList<Content>();
		}

		Content content = new Content(new SampleUsed(sample.code, sample.typeCode, sample.categoryCode));

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);
				
		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromLevel(importType.getMapPropertyDefinition(), "Content", sample.properties,content.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromLevel(sampleType.getMapPropertyDefinition(), "Content", sample.properties,content.properties);
		}

		container.contents.add(content);

		container.projectCodes=InstanceHelpers.addCodesList(sample.projectCodes,container.projectCodes);

		container.sampleCodes=InstanceHelpers.addCode(sample.code,container.sampleCodes);

	}
	
	
	public static void addContent(Container inputContainer, Container outputContainer , Experiment experiment) {
		if(outputContainer.contents==null){
			outputContainer.contents=new ArrayList<Content>();
		}
		
		outputContainer.contents.addAll(inputContainer.contents);
		outputContainer.projectCodes=InstanceHelpers.addCodesList(inputContainer.projectCodes,outputContainer.projectCodes);
		outputContainer.sampleCodes=InstanceHelpers.addCodesList(inputContainer.sampleCodes,outputContainer.sampleCodes);

		if(experiment.categoryCode.equals("transformation")){
			outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCode(experiment.typeCode ,outputContainer.fromExperimentTypeCodes);
		}else{
			outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCodesList(inputContainer.fromExperimentTypeCodes,outputContainer.fromExperimentTypeCodes);
		}
		
	}
	
	//TODO 
	public static void addContainerSupport(Container container,Experiment experiment){
		
		if(container.support==null){
			container.support=new ContainerSupport();
		}
		
		if(experiment.instrument.outContainerSupportCategoryCode==null){
			container.support=getContainerSupportTube(Double.toString(Math.random()));
		}else{
			//TODO 
			ContainerSupport containerSupport=new ContainerSupport();
			containerSupport.barCode=getBarCode(experiment.instrument.outContainerSupportCategoryCode);	
			containerSupport.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
			containerSupport.x="?";
			containerSupport.y="?";
		}
	}


	//TODO
	private static String getBarCode(String outContainerSupportCategoryCode) {
		return null;
	}


	//Copy properties values from lists of properties definition and properties values in object Container
	// if the level of the property definition contains "content" the property value is copied in all properties map of container.contents
	// if the level contains "container" the property value is copied in properties map in container
	public static void copyProperties(
			Map<String, PropertyDefinition> propertyDefinitions,
			Map<String, PropertyValue> propertyValues, Container outputContainer) {
		
		for(int i=0;i<outputContainer.contents.size();i++){
			InstanceHelpers.copyPropertyValueFromLevel(propertyDefinitions, Content.LEVEL_SEARCH, propertyValues, outputContainer.contents.get(i).properties);
		}
		
		InstanceHelpers.copyPropertyValueFromLevel(propertyDefinitions, Container.LEVEL_SEARCH, propertyValues, outputContainer.properties);
		
	}


	public static void generateCode(Container outputContainer) {
		if(outputContainer.code==null){
			if(outputContainer.projectCodes.size()==1 && outputContainer.sampleCodes.size()==1){
				outputContainer.code=outputContainer.projectCodes.get(0)+"/"+outputContainer.sampleCodes.get(0)+"/"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()).toUpperCase();		
			} else  {
				outputContainer.code="MULTI"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()).toUpperCase();
			}
		}
		
	}

}
