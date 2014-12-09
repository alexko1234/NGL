package controllers.commons.api;

import java.util.List;

import models.laboratory.parameter.Index;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;

import play.libs.Json;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class Parameters extends CommonController {

	public static Result list(String typeCode) throws DAOException {
		List<Index> index=MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("typeCode", typeCode)).toList();
		return ok(Json.toJson(index));

    }
	
	public static Result get(String typeCode, String code) throws DAOException {
		Index index=MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("typeCode", typeCode).is("code", code));
		if(index != null){
			return ok(Json.toJson(index));
		}
		else { return notFound(); }

    }
}
