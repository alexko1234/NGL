package models.laboratory.sample.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.sample.description.dao.SampleCategoryDAO;
//TODO: fix doc generation that produces an error with the unqualified name
// import models.utils.Model.Finder;

public class SampleCategory extends AbstractCategory<SampleCategory> {

//	public static Finder<SampleCategory> find = new Finder<SampleCategory>(SampleCategoryDAO.class.getName());
	public static Finder<SampleCategory> find = new Finder<>(SampleCategoryDAO.class);
	
	public SampleCategory() {
		super(SampleCategoryDAO.class.getName());
	}

}
