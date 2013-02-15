package models.laboratory.sample.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.sample.description.dao.SampleCategoryDAO;


public class SampleCategory extends AbstractCategory{

	public static Finder<SampleCategory> find = new Finder<SampleCategory>(SampleCategoryDAO.class.getName());
	
	public SampleCategory() {
		super(SampleCategoryDAO.class.getName());
	}

}
