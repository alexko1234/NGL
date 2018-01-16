package controllers.commons.api;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import controllers.ListForm;
import models.administration.authorisation.Role;
import models.utils.ListObject;
import models.utils.ListObjectNumber;
import models.utils.dao.DAOException;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;

/**
 * 
 * @author michieli
 *
 */
public class Roles extends CommonController{
	
	/*
	 * List Method
	 */
	public /*static*/ Result list() throws DAOException{
		try{
			List<Role> roles = Role.find.findAll();
			
			//if(form.list){
			List<ListObjectNumber> lop = new ArrayList<ListObjectNumber>();
			for(Role r:roles){
				lop.add(new ListObjectNumber(r.id, r.label));
			}
			return Results.ok(Json.toJson(lop));
		} catch (DAOException e) {
			Logger.error("DAO error: " + e.getMessage());
			return  Results.internalServerError(e.getMessage());
		}
	}

}
