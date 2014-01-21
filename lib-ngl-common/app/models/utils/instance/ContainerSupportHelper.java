package models.utils.instance;

import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.dao.DAOException;

public class ContainerSupportHelper {

	public static ContainerSupport getContainerSupportTube(String barCode){
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode="tube";
		containerSupport.column="1";
		containerSupport.line="1";
		return containerSupport;
	}

	public static ContainerSupport getContainerSupport(
			String containerCategoryCode, int nbUsableContainer, String barCode, String x, String y) throws DAOException {
			
		List<ContainerSupportCategory> containerSupportCategories=ContainerSupportCategory.find.findByContainerCategoryCode(containerCategoryCode);

		ContainerSupport containerSupport=new ContainerSupport();
		
		for(int i=0;i<containerSupportCategories.size();i++){
			if(containerSupportCategories.get(i).nbUsableContainer==nbUsableContainer){
				containerSupport.categoryCode=containerSupportCategories.get(i).code;
			}
		}
		
		if(containerSupport.categoryCode==null){
			containerSupport.categoryCode=containerSupportCategories.get(0).code;
		}

		containerSupport.barCode=barCode;	
		containerSupport.column=x;
		containerSupport.line=y;
		return containerSupport;
	}

}
