package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;



/**
 * 
 * Embedded data in collection Container
 * Associate support and container with a position (x,y)
 * 
 * If container category is  tube, the position is (x,y)=(1,1) and support category is 'VIDE'
 * 
 * A support intance defines by unique barcode or name ?? are in many container support with different position 
 * 
 * @author mhaquell
 *
 */
public class ContainerSupport implements IValidation {
	
	// Support name
	public String name;
	public String barCode;
	
	public String categoryCode;

	public String stockCode;
	
	// Position
	public String x;
	public String y;
	
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
	public void validate(Map<String, List<ValidationError>> errors) {
		BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.categoryCode, "categoryCode", ContainerSupportCategory.find, false);
		BusinessValidationHelper.validateExistInstanceCode(errors, this.stockCode, "stockCode",Stock.class,InstanceConstants.STOCK_COLL_NAME ,false);

	}


}
