package models.laboratory.container.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.dao.ContainerSupportCategoryDAO;

public class ContainerSupportCategory extends AbstractCategory{

	public int nbUsableContainer;
	
	public int nbLine;
	
	public int nbColumn;

	public static Finder<ContainerSupportCategory> find = new Finder<ContainerSupportCategory>(ContainerSupportCategoryDAO.class.getName()); 
	
	public ContainerSupportCategory() {
		super(ContainerSupportCategoryDAO.class.getName());
	}
	
}
