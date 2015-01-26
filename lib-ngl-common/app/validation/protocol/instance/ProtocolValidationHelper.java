package validation.protocol.instance;

import static validation.utils.ValidationHelper.required;
import models.laboratory.experiment.description.ProtocolCategory;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;

public class ProtocolValidationHelper extends CommonValidationHelper {

	public static void validateProtocolCategoryCode(String categoryCode, ContextValidation contextValidation){
		try{
			if(required(contextValidation, categoryCode, "code")){
				if(!models.laboratory.experiment.description.ProtocolCategory.find.isCodeExist(categoryCode)){
					contextValidation.addErrors("protocoles.categoryCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, categoryCode);
				}
			}
		}catch(DAOException e){
			throw new RuntimeException(e);
		}
		
	}

	
}
