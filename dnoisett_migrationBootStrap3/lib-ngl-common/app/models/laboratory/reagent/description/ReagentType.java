package models.laboratory.reagent.description;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.reagent.description.dao.ReagentTypeDAO;

public class ReagentType extends CommonInfoType{
	
	public ReagentType() {
		super(ReagentTypeDAO.class.getName());
	}

	public static CommonInfoType.AbstractCommonInfoTypeFinder<ReagentType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<ReagentType>(ReagentTypeDAO.class); 
	
}
