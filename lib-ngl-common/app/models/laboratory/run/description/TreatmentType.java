package models.laboratory.run.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.run.description.dao.TreatmentTypeDAO;

public class TreatmentType extends CommonInfoType {

	public TreatmentCategory category;
	public String names;	
	public List<TreatmentTypeContext> contexts = new ArrayList<TreatmentTypeContext>();
	public String displayOrders;

	
	public static CommonInfoType.AbstractCommonInfoTypeFinder<TreatmentType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<TreatmentType>(TreatmentTypeDAO.class); 

	public TreatmentType() {
		super(TreatmentTypeDAO.class.getName());
	}
	
}
