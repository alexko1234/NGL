package models.laboratory.reagent.description;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.reagent.description.dao.ReagentTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;

public class ReagentType extends CommonInfoType{
	
	public ReagentType() {
		super(ReagentTypeDAO.class.getName());
	}

	public static CommonInfoTypeFinder<ReagentTypeDAO,ReagentType> find = new CommonInfoTypeFinder<ReagentTypeDAO,ReagentType>(ReagentTypeDAO.class); 
	
}
