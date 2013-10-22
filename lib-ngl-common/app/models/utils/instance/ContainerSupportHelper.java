package models.utils.instance;

import java.util.List;

import play.api.modules.spring.Spring;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
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
		
		ContainerSupportCategoryDAO containerSupportCategoryDAO = Spring.getBeanOfType(ContainerSupportCategoryDAO.class);
	
		List<ContainerSupportCategory> containerSupportCategories=containerSupportCategoryDAO.findByContainerCategoryCode(containerCategoryCode);
		
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode=containerSupportCategories.get(0).code;
		containerSupport.column=x;
		containerSupport.line=y;
		return containerSupport;
	}

}
