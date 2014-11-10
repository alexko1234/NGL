package models.utils.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;

public class ContainerSupportHelper {

	public static LocationOnContainerSupport getContainerSupportTube(String supportCode){
		LocationOnContainerSupport containerSupport=new LocationOnContainerSupport();
		containerSupport.code=supportCode;	
		containerSupport.categoryCode="tube";
		containerSupport.column="1";
		containerSupport.line="1";
		return containerSupport;
	}

	public static LocationOnContainerSupport getContainerSupport(
			String containerCategoryCode, int nbUsableContainer, String supportCode, String x, String y) throws DAOException {

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

		containerSupport.code=supportCode;	
		containerSupport.column=x;
		containerSupport.line=y;
		return containerSupport;
	}

	public static ContainerSupport createSupport(String supportCode, PropertyValue sequencingProgramType, String categoryCode, String user){
		ContainerSupport s = new ContainerSupport(); 

		s.code = supportCode;	
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

	public static void save(ContainerSupport support,
			ContextValidation contextValidation) {

		contextValidation.addKeyToRootKeyName("support["+support.code+"]");
		if(support._id!=null){contextValidation.setUpdateMode();}else {contextValidation.setCreationMode();}
		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME,support, contextValidation);			
		contextValidation.removeKeyFromRootKeyName("support["+support.code+"]");	
	}


	public static void updateData(ContainerSupport support,List<ContainerUsed> inputContainerUseds, Experiment experiment, Map<String, PropertyValue> properties) {

		for(ContainerUsed inputContainerUsed:inputContainerUseds){

			Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.code);
			support.projectCodes=InstanceHelpers.addCodesList(container.projectCodes, support.projectCodes);
			support.sampleCodes=InstanceHelpers.addCodesList(container.sampleCodes, support.sampleCodes);
			support.fromExperimentTypeCodes=InstanceHelpers.addCode(experiment.typeCode, support.fromExperimentTypeCodes);

		}
		
		if(support.properties==null){
			support.properties=new HashMap<String, PropertyValue>();
		}
		
		ExperimentType experimentType =BusinessValidationHelper.validateExistDescriptionCode(null, experiment.typeCode, "typeCode", ExperimentType.find,true);
		if(experimentType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.ContainerSupport), properties,support.properties);
		}

		InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateExistDescriptionCode(null, experiment.instrument.typeCode, "typeCode", InstrumentUsedType.find,true);
		if(instrumentUsedType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentUsedType.getPropertyDefinitionByLevel(Level.CODE.ContainerSupport), properties,support.properties);
		}
	}


	public static void updateData(List<Container> containers, Experiment experiment, ContainerSupport support) {
		for(Container container : containers){
			support.projectCodes=InstanceHelpers.addCodesList(container.projectCodes, support.projectCodes);
			support.sampleCodes=InstanceHelpers.addCodesList(container.sampleCodes, support.sampleCodes);
			support.fromExperimentTypeCodes=InstanceHelpers.addCodesList(container.fromExperimentTypeCodes, support.fromExperimentTypeCodes);
		}


	}
}
