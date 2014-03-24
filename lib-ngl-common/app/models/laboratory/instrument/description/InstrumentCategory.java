package models.laboratory.instrument.description;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;


public class InstrumentCategory extends AbstractCategory<InstrumentCategory>{
	
	public static Finder<InstrumentCategory> find = new Finder<InstrumentCategory>(InstrumentCategoryDAO.class.getName()); 
	
	public InstrumentCategory() {
		super(InstrumentCategoryDAO.class.getName());
	}
	
	
}
