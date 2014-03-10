package models.utils.instance;

import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.dao.DAOException;

public class ContainerSupportHelper {

	public static LocationOnContainerSupport getContainerSupportTube(String supportCode){
		LocationOnContainerSupport containerSupport=new LocationOnContainerSupport();
		containerSupport.supportCode=supportCode;	
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

		containerSupport.supportCode=supportCode;	
		containerSupport.column=x;
		containerSupport.line=y;
		return containerSupport;
	}

}
