package models.laboratory.container.instance.tree;

import java.util.Set;

public class ParentContainers {

	public String code;
	public String supportCode;
	public Set<String> fromTransformationTypeCodes;
	
	//TODO GA fromTransformationCodes
	//TODO GA processTypeCodes
	//TODO GA processCodes
	
	public ParentContainers() {
		super();		
	}

	public ParentContainers(String code, String supportCode, Set<String> fromTransformationTypeCodes) {
		this.code = code;
		this.supportCode = supportCode;
		this.fromTransformationTypeCodes = fromTransformationTypeCodes;
	}

}
