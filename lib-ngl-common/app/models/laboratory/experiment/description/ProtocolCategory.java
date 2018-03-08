package models.laboratory.experiment.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.experiment.description.dao.ProtocolCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;

public class ProtocolCategory extends AbstractCategory<ProtocolCategory> {

//	public static Finder<ProtocolCategory> find = new Finder<ProtocolCategory>(ProtocolCategoryDAO.class.getName()); 
	public static Finder<ProtocolCategory> find = new Finder<ProtocolCategory>(ProtocolCategoryDAO.class); 
	
	public ProtocolCategory() {
		super(ProtocolCategoryDAO.class.getName());
	}
	
}
