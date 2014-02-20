package validation.container.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import fr.cea.ig.MongoDBDAO;

public class ContainerSupportValidationHelper extends CommonValidationHelper{

	public static void validateUniqueSupportCodePosition(ContainerSupport containerSupport,ContextValidation contextValidation) {

		if(contextValidation.isCreationMode()){
			Query query=DBQuery.and(DBQuery.is("support.line",containerSupport.line),
					DBQuery.is("support.column", containerSupport.column),
					DBQuery.is("support.supportCode",containerSupport.supportCode));
			if (MongoDBDAO.getCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class).count(query)!=0 ) {
				//TODO revoir le message d'erreur
				contextValidation.addErrors("supportCode.line.column", ValidationConstants.ERROR_NOTUNIQUE_MSG, containerSupport.supportCode,containerSupport.line,containerSupport.column);		
			}
		}
	}

	public static void validateContainerSupportCategoryCode(
			String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerSupportCategory.find,false);

	}

	public static void validateStockCode(String stockCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, stockCode, "stockCode",Stock.class,InstanceConstants.STOCK_COLL_NAME ,false);

	}

}
