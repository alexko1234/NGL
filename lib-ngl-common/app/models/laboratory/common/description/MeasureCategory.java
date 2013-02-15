package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.dao.MeasureCategoryDAO;

public class MeasureCategory extends AbstractCategory{
	
	public List<MeasureValue> measurePossibleValues;
	
	public static Finder<MeasureCategory> find = new Finder<MeasureCategory>(MeasureCategoryDAO.class.getName()); 
	
	public MeasureCategory() {
		super(MeasureCategoryDAO.class.getName());
	}

	
}
