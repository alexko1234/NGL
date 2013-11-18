package models.laboratory.run.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.TreatmentTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;


public class TreatmentType extends CommonInfoType{
	public TreatmentCategory category;
	public String names;
	
	public List<TreatmentTypeContext> contexts = new ArrayList<TreatmentTypeContext>();

	public static CommonInfoTypeFinder<TreatmentTypeDAO,TreatmentType> find = new CommonInfoTypeFinder<TreatmentTypeDAO,TreatmentType>(TreatmentTypeDAO.class); 
	
	public TreatmentType() {
		super(TreatmentTypeDAO.class.getName());
	}
	
}
