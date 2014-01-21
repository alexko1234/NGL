package models.laboratory.container.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.utils.dao.DAOException;

public class ContainerSupportCategory extends AbstractCategory<ContainerSupportCategory>{

	public Integer nbUsableContainer;
	
	public Integer nbLine;
	
	public Integer nbColumn;

	public ContainerCategory containerCategory;
	
	public static ContainerSupportCategoryFinder find = new ContainerSupportCategoryFinder(); 
	
	public ContainerSupportCategory() {
		super(ContainerSupportCategoryDAO.class.getName());
	}
	
	public static class ContainerSupportCategoryFinder extends Finder<ContainerSupportCategory> {
	
		public ContainerSupportCategoryFinder() {
			super(ContainerSupportCategoryDAO.class.getName());
			
		}

		public List<ContainerSupportCategory> findByContainerCategoryCode(String categoryCode) throws DAOException{
			return ((ContainerSupportCategoryDAO) getInstance()).findByContainerCategoryCode(categoryCode);
		}
		
		public List<ContainerSupportCategory> findByExperimentTypeCode(String experimentTypeCode) throws DAOException{
			return ((ContainerSupportCategoryDAO) getInstance()).findByExperimentTypeCode(experimentTypeCode);
		}

	}	
	
	
}
