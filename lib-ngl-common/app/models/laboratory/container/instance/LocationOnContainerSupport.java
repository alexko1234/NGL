package models.laboratory.container.instance;


import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.stock.instance.Stock;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContainerSupportValidationHelper;
import validation.utils.BusinessValidationHelper;



/**
 * 
 * Embedded data in collection Container
 * Associate support and container with a position (column, line)
 * 
 * If container category is  tube, the position is (column,line)=(1,1) and support category is 'VIDE'
 * 
 * A support intance defines by unique supportCode /line/column, a supportCode (ex barCode) can be referenced in many container.support 
 * 
 * @author mhaquell
 *
 */
public class LocationOnContainerSupport implements IValidation {
	
	// ContainerSupport name
//	public String name;
//	public String barCode;
// Replace name and barCode
	public String supportCode;
		
	public String categoryCode;

	public String stockCode;
	
	// Container Position in support
	public String column;
	public String line;
	
	@JsonIgnore
	public ContainerSupportCategory getContainerSupportCategory(){
		return new HelperObjects<ContainerSupportCategory>().getObject(ContainerSupportCategory.class, categoryCode);

	}
	

	@JsonIgnore
	public Stock getStock(){
		return new HelperObjects<Stock>().getObject(Stock.class, stockCode);

	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		ContainerSupportValidationHelper.validateUniqueSupportCodePosition(this, contextValidation);
		ContainerSupportValidationHelper.validateContainerSupportCategoryCode(categoryCode, contextValidation);
		ContainerSupportValidationHelper.validateStockCode(stockCode, contextValidation);
		//ContainerSupportValidationHelper.validateSupportCode(supportCode, contextValidation);
	}


}
