package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.utils.ListObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class Lists extends Controller {

	public static Result projects(){
		return Results.ok(Json.toJson(getListObjectFromProcedureLims("pl_Projet")));	
	}
	

	public static Result samples(){
		return Results.ok(Json.toJson(getListObjectFromProcedureLims("pl_Materiel")));	
	}
	
	public static Result etmanips(){
		return Results.ok(Json.toJson(getListObjectFromProcedureLims("pl_Etmanip")));	
	}
	
	public static Result etmateriels(){
		return Results.ok(Json.toJson(getListObjectFromProcedureLims("pl_Etmateriel")));	
	}
	
	//TODO deplacer dans Common
	public static List<ListObject> getListObjectFromProcedureLims(String procedure){
		List<ListObject> listObj=new ArrayList<ListObject>();
		try{
			Connection connection=DB.getConnection("lims");
			Statement stm=connection.createStatement();
			
			ResultSet resultSet=stm.executeQuery(procedure);
			System.err.println("Proc " +procedure+ " "+resultSet.next());
			while(resultSet.next()){
				ListObject value =new ListObject();
				value.code=resultSet.getString(2);
				value.name=resultSet.getString(1);
				listObj.add(value);
			}
			stm.close();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listObj;
	}

}
