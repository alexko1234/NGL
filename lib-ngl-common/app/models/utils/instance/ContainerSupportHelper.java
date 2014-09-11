package models.utils.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import validation.ContextValidation;

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

	
	public static void updateData(Container container,
			ContainerSupport support) {
		support.projectCodes=InstanceHelpers.addCodesList(container.projectCodes, support.projectCodes);
		support.sampleCodes=InstanceHelpers.addCodesList(container.sampleCodes, support.sampleCodes);
		support.fromExperimentTypeCodes=InstanceHelpers.addCodesList(container.fromExperimentTypeCodes, support.fromExperimentTypeCodes);
		
	}

}
