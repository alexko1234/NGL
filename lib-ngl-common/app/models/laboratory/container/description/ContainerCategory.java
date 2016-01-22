package models.laboratory.container.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerCategoryDAO;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.utils.ListObject;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

public class ContainerCategory extends AbstractCategory<ContainerCategory>{

	public static ContainerCategoryFinder find = new ContainerCategoryFinder(); 
	
	public ContainerCategory() {
		super(ContainerCategoryDAO.class.getName());
	}

	
	public static class ContainerCategoryFinder extends Finder<ContainerCategory> {
		
		public ContainerCategoryFinder() {
			super(ContainerCategoryDAO.class.getName());
			
		}

		public ContainerCategory findByContainerSupportCategoryCode(String containerSupportCategoryCode) {
			return ((ContainerCategoryDAO) getInstance()).findByContainerSupportCategoryCode(containerSupportCategoryCode);
		}
		
	}	
}
