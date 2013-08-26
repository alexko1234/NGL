package models.laboratory.reagent.description;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.reagent.description.dao.ReagentTypeDAO;

public class ReagentType extends CommonInfoType{
	
	public ReagentType() {
		super(ReagentTypeDAO.class.getName());
	}

	public static Finder<ReagentType> find = new Finder<ReagentType>(ReagentTypeDAO.class.getName()); 
	
}
