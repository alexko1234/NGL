package models.laboratory.common.description;



import models.laboratory.common.description.dao.MeasureCategoryDAO;

public class MeasureCategory extends AbstractCategory<MeasureCategory>{
		
	public static Finder<MeasureCategory> find = new Finder<MeasureCategory>(MeasureCategoryDAO.class.getName()); 
	
	public MeasureCategory() {
		super(MeasureCategoryDAO.class.getName());
	}

	
}
