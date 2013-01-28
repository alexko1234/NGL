package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;


public class InstrumentCategory{

	public Long id;
	
	public String name;
	
	public String code;
	
	public List<ContainerSupportCategory> inContainerSupportCategories;
	
	public int nbInContainerSupportCategories;
	
	public List<ContainerSupportCategory> outContainerSupportCategories;

	public int nbOutContainerSupportCategories;
	
	
	
	
}
