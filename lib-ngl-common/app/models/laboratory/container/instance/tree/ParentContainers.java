package models.laboratory.container.instance.tree;

import java.util.Set;

public class ParentContainers {

	public String code;
	public String supportCode;
	public Set<String> fromTransformationTypeCodes;
	//public Set<String> fromTransformationCodes; TODO GA when refactoring container
	public Set<String> processTypeCodes;
	public Set<String> processCodes;
	
	public ParentContainers() {
		super();		
	}

	
}
