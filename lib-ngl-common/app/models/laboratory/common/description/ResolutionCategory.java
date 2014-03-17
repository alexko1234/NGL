package models.laboratory.common.description;

import models.laboratory.common.description.dao.ResolutionCategoryDAO;


public class ResolutionCategory extends AbstractCategory<ResolutionCategory>{

	public Short displayOrder;

	public static Finder<ResolutionCategory> find = new Finder<ResolutionCategory>(ResolutionCategoryDAO.class.getName()); 
		
	public ResolutionCategory() {
		super(ResolutionCategoryDAO.class.getName());
	}

}
