package models.utils.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;

public class ContainerSupportHelper {

	public static LocationOnContainerSupport getContainerSupportTube(String containerSupportCode){
		LocationOnContainerSupport containerSupport=new LocationOnContainerSupport();
		containerSupport.code=containerSupportCode;	
		containerSupport.categoryCode="tube";
		containerSupport.column="1";
		containerSupport.line="1";
		
		return containerSupport;
	}

	// FDS 13/10/2015 ajouter param string storageCode
	public static LocationOnContainerSupport getContainerSupport(
			String containerCategoryCode, int nbUsableContainer, String containerSupportCode, String x, String y, String storageCode) throws DAOException {

		List<ContainerSupportCategory> containerSupportCategories=ContainerSupportCategory.find.findByContainerCategoryCode(containerCategoryCode);

		LocationOnContainerSupport containerSupport=new LocationOnContainerSupport();

		for(int i=0;i<containerSupportCategories.size();i++){
			if(containerSupportCategories.get(i).nbUsableContainer==nbUsableContainer){
				containerSupport.categoryCode=containerSupportCategories.get(i).code;
			}
		}

		if(containerSupport.categoryCode==null){
			containerSupport.categoryCode=containerSupportCategories.get(0).code;
		}

		containerSupport.code=containerSupportCode;	
		containerSupport.column=x;
		containerSupport.line=y;
		
		if ( storageCode != null ) {
			containerSupport.storageCode=storageCode;
			//Logger.debug ("1) getContainerSupport; support "+ containerSupportCode+": storageCode= "+ storageCode);
		}else {
			// normal ou pas qu'il n'y ait pas de storage code  ??
			Logger.warn("storage code null for support code = "+containerSupportCode);
		}
		
		return containerSupport;
	}
	

	//FDS 13/10/2015 recreer une methode avec la meme signature
	public static LocationOnContainerSupport getContainerSupport(
				String containerCategoryCode, int nbUsableContainer, String containerSupportCode, String x, String y) throws DAOException {
		return getContainerSupport( containerCategoryCode, nbUsableContainer, containerSupportCode, x, y, null);
	}
	

	public static ContainerSupport createContainerSupport(
			String containerSupportCode, PropertyValue sequencingProgramType, String categoryCode, String user){
		
		ContainerSupport s = new ContainerSupport(); 

		s.code = containerSupportCode;	
		s.categoryCode = categoryCode;

		s.state = new State(); 
		s.state.code = "N"; // default value
		s.state.user = user;
		s.state.date = new Date();

		s.traceInformation = new TraceInformation(); 
		s.traceInformation.setTraceInformation(user);
		s.valuation = new Valuation();
		
		s.valuation.valid = TBoolean.UNSET;

		if (sequencingProgramType != null) {
			HashMap<String, PropertyValue> prop = new HashMap<String, PropertyValue>();
			prop.put("sequencingProgramType", sequencingProgramType);
			s.properties = prop;
		}

		return s;
	}
		
	public static void save(ContainerSupport containerSupport,
			ContextValidation contextValidation) {

		contextValidation.addKeyToRootKeyName("containerSupport["+containerSupport.code+"]");
		if(containerSupport._id!=null){contextValidation.setUpdateMode();}else {contextValidation.setCreationMode();}
		InstanceHelpers.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,containerSupport, contextValidation);			
		contextValidation.removeKeyFromRootKeyName("containerSupport["+containerSupport.code+"]");	
	}


	public static void updateData(ContainerSupport containerSupport,List<InputContainerUsed> inputContainerUseds, Experiment experiment, Map<String, PropertyValue> properties) {

		for(InputContainerUsed inputContainerUsed:inputContainerUseds){

			Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.code);
			if(containerSupport.projectCodes == null){
				containerSupport.projectCodes = new HashSet<String>();
			}
			if(containerSupport.sampleCodes == null){
				containerSupport.sampleCodes = new HashSet<String>();
			}
			if(containerSupport.fromExperimentTypeCodes == null){
				containerSupport.fromExperimentTypeCodes = new HashSet<String>();
			}
			containerSupport.projectCodes.addAll(container.projectCodes);
			containerSupport.sampleCodes.addAll(container.sampleCodes);
			
				
			if(experiment.categoryCode.equals("transformation")){
					if(containerSupport.fromExperimentTypeCodes == null){
						containerSupport.fromExperimentTypeCodes = new HashSet<String>();
					}
					containerSupport.fromExperimentTypeCodes.add(experiment.typeCode);
				}else{
					if(CollectionUtils.isNotEmpty(container.fromExperimentTypeCodes)){				
						if(containerSupport.fromExperimentTypeCodes == null){
							containerSupport.fromExperimentTypeCodes = new HashSet<String>();
						}
						containerSupport.fromExperimentTypeCodes.addAll(container.fromExperimentTypeCodes);
					}
				}

		}
		
		if(containerSupport.properties==null){
			containerSupport.properties=new HashMap<String, PropertyValue>();
		}
		
		ExperimentType experimentType =BusinessValidationHelper.validateExistDescriptionCode(null, experiment.typeCode, "typeCode", ExperimentType.find,true);
		if(experimentType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.ContainerSupport), properties,containerSupport.properties);
		}

		InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateExistDescriptionCode(null, experiment.instrument.typeCode, "typeCode", InstrumentUsedType.find,true);
		if(instrumentUsedType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentUsedType.getPropertyDefinitionByLevel(Level.CODE.ContainerSupport), properties,containerSupport.properties);
		}
	}


	public static void updateData(List<Container> containers, Experiment experiment, ContainerSupport containerSupport) {
		for(Container container : containers){
			containerSupport.projectCodes.addAll(container.projectCodes);
			containerSupport.sampleCodes.addAll(container.sampleCodes);
			containerSupport.fromExperimentTypeCodes.addAll(container.fromExperimentTypeCodes);
		}


	}
}
