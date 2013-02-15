package models.laboratory.container.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerCategoryDAO;

public class ContainerCategory extends AbstractCategory{

	public static Finder<ContainerCategory> find = new Finder<ContainerCategory>(ContainerCategoryDAO.class.getName()); 
	
	public ContainerCategory() {
		super(ContainerCategoryDAO.class.getName());
	}

}
