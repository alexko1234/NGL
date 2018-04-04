package models.laboratory.common.description;

import models.laboratory.common.description.dao.MeasureCategoryDAO;
import models.utils.dao.AbstractDAO;

public class MeasureCategory extends AbstractCategory<MeasureCategory> {
		
//	public static Finder<MeasureCategory> find = new Finder<MeasureCategory>(MeasureCategoryDAO.class.getName()); 
	public static final Finder<MeasureCategory,MeasureCategoryDAO> find = new Finder<>(MeasureCategoryDAO.class); 
	
	public MeasureCategory() {
		super(MeasureCategoryDAO.class.getName());
	}

	@Override
	protected Class<? extends AbstractDAO<MeasureCategory>> daoClass() {
		return MeasureCategoryDAO.class;
	}

}
