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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<ContainerSupportCategory> getInContainerSupportCategories() {
		return inContainerSupportCategories;
	}

	public void setInContainerSupportCategories(
			List<ContainerSupportCategory> inContainerSupportCategories) {
		this.inContainerSupportCategories = inContainerSupportCategories;
	}

	public int getNbInContainerSupportCategories() {
		return nbInContainerSupportCategories;
	}

	public void setNbInContainerSupportCategories(int nbInContainerSupportCategories) {
		this.nbInContainerSupportCategories = nbInContainerSupportCategories;
	}

	public List<ContainerSupportCategory> getOutContainerSupportCategories() {
		return outContainerSupportCategories;
	}

	public void setOutContainerSupportCategories(
			List<ContainerSupportCategory> outContainerSupportCategories) {
		this.outContainerSupportCategories = outContainerSupportCategories;
	}

	public int getNbOutContainerSupportCategories() {
		return nbOutContainerSupportCategories;
	}

	public void setNbOutContainerSupportCategories(
			int nbOutContainerSupportCategories) {
		this.nbOutContainerSupportCategories = nbOutContainerSupportCategories;
	}
	
	
	
}
