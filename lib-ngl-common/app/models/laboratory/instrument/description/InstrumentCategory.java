package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;


public class InstrumentCategory extends AbstractCategory<InstrumentCategory>{
	
	public static Finder<InstrumentCategory> find = new Finder<InstrumentCategory>(InstrumentCategoryDAO.class.getName()); 
	
	public InstrumentCategory() {
		super(InstrumentCategoryDAO.class.getName());
	}
	
	
}
