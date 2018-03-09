package models.laboratory.container.description;

// import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerCategoryDAO;
// import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;
// import models.utils.ListObject;
// import models.utils.Model.Finder;
// import models.utils.dao.DAOException;
// import play.api.modules.spring.Spring;
import models.utils.dao.AbstractDAO;

// This link : {@link models.laboratory.container.description.ContainerCategory}

public class ContainerCategory extends AbstractCategory<ContainerCategory> {

	public static final ContainerCategoryFinder find = new ContainerCategoryFinder(); 
	
	public ContainerCategory() {
		super(ContainerCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<ContainerCategory>> daoClass() {
		return ContainerCategoryDAO.class;
	}
	
	public static class ContainerCategoryFinder extends Finder<ContainerCategory,ContainerCategoryDAO> {
		
//		public ContainerCategoryFinder() {
//			super(ContainerCategoryDAO.class.getName());
//			
//		}
		public ContainerCategoryFinder() { super(ContainerCategoryDAO.class); }

		public ContainerCategory findByContainerSupportCategoryCode(String containerSupportCategoryCode) {
//			return ((ContainerCategoryDAO) getInstance()).findByContainerSupportCategoryCode(containerSupportCategoryCode);
			return getInstance().findByContainerSupportCategoryCode(containerSupportCategoryCode);
		}
		
	}

}
