package models.laboratory.container.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
import models.utils.dao.DAOException;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

// This link : {@link models.laboratory.container.description ContainerSupportCategory}

public class ContainerSupportCategory extends AbstractCategory<ContainerSupportCategory> {
	
	public static final ContainerSupportCategoryFinder find = new ContainerSupportCategoryFinder(); 
	
	public Integer           nbUsableContainer;
	public Integer           nbLine;
	public Integer           nbColumn;
	public ContainerCategory containerCategory;
	
	public ContainerSupportCategory() {
		super(ContainerSupportCategoryDAO.class.getName());
	}
	
	public static class ContainerSupportCategoryFinder extends Finder<ContainerSupportCategory,ContainerSupportCategoryDAO> {
	
//		public ContainerSupportCategoryFinder() {
//			super(ContainerSupportCategoryDAO.class.getName());
//			
//		}
		public ContainerSupportCategoryFinder() { super(ContainerSupportCategoryDAO.class);	}

		public List<ContainerSupportCategory> findByContainerCategoryCode(String categoryCode) throws DAOException {
//			return ((ContainerSupportCategoryDAO) getInstance()).findByContainerCategoryCode(categoryCode);
			return getInstance().findByContainerCategoryCode(categoryCode);
		}
		
		public List<ContainerSupportCategory> findInputByExperimentTypeCode(String experimentTypeCode) throws DAOException {
//			return ((ContainerSupportCategoryDAO) getInstance()).findInputByExperimentTypeCode(experimentTypeCode);
			return getInstance().findInputByExperimentTypeCode(experimentTypeCode);
		}

	}	

}
