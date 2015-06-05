package controllers.containers.api;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class Contents extends CommonController {

	@SuppressWarnings("unchecked")
	public static Result list() throws DAOException{
		
		DynamicForm filledForm =  listForm.bindFromRequest();
		List<ListObject> los = new ArrayList<ListObject>();
		
		if(StringUtils.isNotBlank(filledForm.get("objectTypeCode"))){
			List<String> tags = MongoDBDAO.getCollection(InstanceConstants.CONTAINER_COLL_NAME, Container.class).distinct("contents.properties."+filledForm.get("objectTypeCode")+".value");
			
			for(String tag:tags){
				los.add(new ListObject(tag,tag));
			}
			
		}			
		
		return ok(Json.toJson(los));
	}
	
	
}
