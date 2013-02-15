package models.laboratory.experiment.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ProtocolCategoryDAO;

public class ProtocolCategory extends AbstractCategory{

	public static Finder<ProtocolCategory> find = new Finder<ProtocolCategory>(ProtocolCategoryDAO.class.getName()); 
	
	public ProtocolCategory()
	{
		super(ProtocolCategoryDAO.class.getName());
	}
	
}
