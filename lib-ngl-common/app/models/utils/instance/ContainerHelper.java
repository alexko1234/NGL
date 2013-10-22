package models.utils.instance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import validation.utils.BusinessValidationHelper;

public class ContainerHelper {

	/**
	 * 
	 * Create au niveau Container from a ResultSet
	 * 
	 * The resultset must return fields :code, project, sampleCode, comment, codeSupport, limsCode, receptionDate, mesuredConcentration, mesuredVolume, mesuredQuantity, indexBq, nbContainer
	 * 
	 * @param rs ResulSet from Query
	 * @param containerCategoryCode 
	 * @param containerStatecode
	 * @return
	 * @throws SQLException
	 * @throws DAOException 
	 */
	public static Container createContainerFromResultSet(ResultSet rs, String containerCategoryCode, String containerStatecode, String experimentTypeCode) throws SQLException, DAOException{

		Container container = new Container();
		container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
		//Logger.debug("Container :"+rs.getString("code"));
		container.code=rs.getString("code");
		container.categoryCode=containerCategoryCode;

		container.comments=new ArrayList<Comment>();				
		container.comments.add(new Comment(rs.getString("comment")));
		container.stateCode=containerStatecode;
		container.valid=null;

		//TODO 
		container.support=ContainerSupportHelper.getContainerSupport(containerCategoryCode, rs.getInt("nbContainer"),rs.getString("codeSupport"),rs.getString("column"),rs.getString("line"));

		container.properties= new HashMap<String, PropertyValue>();
		container.properties.put("limsCode",new PropertySingleValue(rs.getInt("limsCode")));
		
		if(rs.getString("receptionDate")!=null){
			container.properties.put("receptionDate",new PropertySingleValue(rs.getString("receptionDate")));
		}

		container.mesuredConcentration=new PropertySingleValue(rs.getFloat("mesuredConcentration"), "ng/µl");
		container.mesuredVolume=new PropertySingleValue(rs.getFloat("mesuredVolume"), "µl");
		container.mesuredQuantity=new PropertySingleValue(rs.getFloat("mesuredQuantity"), "ng");

		container.fromExperimentTypeCodes=InstanceHelpers.addCode(experimentTypeCode, container.fromExperimentTypeCodes);
		
		if(rs.getString("project")!=null)
		{
			container.projectCodes=new ArrayList<String>();					
			container.projectCodes.add(rs.getString("project"));
		}
		
		if(rs.getString("sampleCode")!=null){
			Content content = new Content();
			content.sampleUsed=new SampleUsed();
			content.sampleUsed.sampleCode=rs.getString("sampleCode");
			container.contents=new ArrayList<Content>();
			container.contents.add(content);
			//Todo replace by method in containerHelper who update sampleCodes from contents
			container.sampleCodes=new ArrayList<String>();
			container.sampleCodes.add(rs.getString("sampleCode"));


			if(rs.getString("tag")!=null){
				content.properties = new HashMap<String, PropertyValue>();
				content.properties.put("tag",new PropertySingleValue(rs.getString("tag")));
			}

		}
		return container;

	}

	public static void addContent(Container container, Sample sample) throws DAOException{
		addContent(container,sample,null);
	}
	
	public static void addContent(Container container,Sample sample,Map<String,PropertyValue> properties) throws DAOException{

		//Create new content
		if(container.contents==null){
			container.contents=new ArrayList<Content>();
		}

		Content content = new Content(new SampleUsed(sample.code, sample.typeCode, sample.categoryCode));

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){

			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,content.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,content.properties);
		}

		if(properties!=null)
			content.properties.putAll(properties);
		
		container.contents.add(content);

		container.projectCodes=InstanceHelpers.addCodesList(sample.projectCodes,container.projectCodes);

		container.sampleCodes=InstanceHelpers.addCode(sample.code,container.sampleCodes);

	}


	public static void addContent(Container inputContainer, Container outputContainer , Experiment experiment) {
		if(outputContainer.contents==null){
			outputContainer.contents=new ArrayList<Content>();
		}
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
	public static void addContainerSupport(Container container,Experiment experiment){

		if(container.support==null){
			container.support=new ContainerSupport();
		}

		if(experiment.instrument.outContainerSupportCategoryCode==null){
			container.support=ContainerSupportHelper.getContainerSupportTube(container.code);
		}else{
			//TODO 
			ContainerSupport containerSupport=new ContainerSupport();
			containerSupport.barCode=getBarCode(experiment.instrument.outContainerSupportCategoryCode);	
			containerSupport.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
			containerSupport.column="?";
			containerSupport.line="?";
		}
	}


	///public static void addContaineSupport(Container container,)


	//TODO
	private static String getBarCode(String outContainerSupportCategoryCode) {
		return null;
	}


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

	public static void generateCode(Container outputContainer) {
		if(outputContainer.code==null){
			if(outputContainer.projectCodes.size()==1 && outputContainer.sampleCodes.size()==1){
				outputContainer.code=outputContainer.projectCodes.get(0)+"/"+outputContainer.sampleCodes.get(0)+"/"+(new SimpleDateFormat("yyyyMMddHHmmss.SSS")).format(new Date()).toUpperCase();		
			} else  {
				outputContainer.code="MULTI"+(new SimpleDateFormat("yyyyMMddHHmmss.SSS")).format(new Date()).toUpperCase();
			}
		}

	}

}
