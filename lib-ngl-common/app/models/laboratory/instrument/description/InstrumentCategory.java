package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.container.description.ContainerSupportCategory;


public class InstrumentCategory extends AbstractCategory{

	
	public List<ContainerSupportCategory> inContainerSupportCategories;
	
	public int nbInContainerSupportCategories;
	
	public List<ContainerSupportCategory> outContainerSupportCategories;

	public int nbOutContainerSupportCategories;
	
	
	
	
}
