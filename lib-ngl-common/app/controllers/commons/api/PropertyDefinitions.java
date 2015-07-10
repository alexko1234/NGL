package controllers.commons.api;

import play.mvc.Result;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.dao.DAOException;
import controllers.APICommonController;

public class PropertyDefinitions extends APICommonController<PropertyDefinition> {

	public PropertyDefinitions(Class<PropertyDefinition> type) {
		super(type);		
	}

	public  Result list() throws DAOException {
		return ok();
	}
	
	
}
