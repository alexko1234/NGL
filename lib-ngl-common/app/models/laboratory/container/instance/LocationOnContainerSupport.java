package models.laboratory.container.instance;


import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContainerSupportValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

// This link : {@link models.laboratory.container.instance.LocationOnContainerSupport}

/**
 * 
 * Embedded data in collection Container
 * Associate support and container with a position (column, line)
 * 
 * If container category is  tube, the position is (column,line)=(1,1) and support category is 'VIDE'
 * 
 * A support instance defines by unique supportCode /line/column, a supportCode (ex barCode) can be referenced in many container.support 
 * 
 * @author mhaquell
 *
 */
public class LocationOnContainerSupport implements IValidation {
	
	/**
	 * Container code.
	 */
	public String code;
		
	/**
	 * Support category (type of container support) ({@link models.laboratory.container.description ContainerSupportCategory}).
	 */
	public String categoryCode;

	
	public String storageCode;
	
	// Container coordinates in support
	public String column;
	public String line;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		ContainerSupportValidationHelper.validateContainerSupportCode(code, contextValidation, "code");
		ContainerSupportValidationHelper.validateUniqueContainerSupportCodePosition(this, contextValidation);
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(categoryCode, contextValidation);		
	}

}
