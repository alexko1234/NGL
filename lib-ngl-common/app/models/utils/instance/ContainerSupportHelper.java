package models.utils.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.dao.DAOException;

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

	public static ContainerSupport createSupport(String supportCode,String categoryCode,String user){
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

		//TODO: a verifier
		s.valuation.valid = TBoolean.UNSET;

		return s;
	}

}
