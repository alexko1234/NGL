package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.dao.InstrumentCategoryDAO;


public class InstrumentCategory extends AbstractCategory{

	public List<ContainerSupportCategory> inContainerSupportCategories;
	
	public int nbInContainerSupportCategories;
	
	public List<ContainerSupportCategory> outContainerSupportCategories;

	public int nbOutContainerSupportCategories;
	
	public static Finder<InstrumentCategory> find = new Finder<InstrumentCategory>(InstrumentCategoryDAO.class.getName()); 
	
	public InstrumentCategory() {
		super(InstrumentCategoryDAO.class.getName());
	}
	
	
}
