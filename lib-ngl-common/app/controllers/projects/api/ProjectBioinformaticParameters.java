package controllers.projects.api;


import org.springframework.stereotype.Controller;

import models.laboratory.project.instance.Project;
import models.laboratory.project.instance.BioinformaticParameters;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.SubDocumentController;

@Controller
public class ProjectBioinformaticParameters extends SubDocumentController<Project, BioinformaticParameters>{

	public ProjectBioinformaticParameters() {
		super(InstanceConstants.PROJECT_COLL_NAME, Project.class, BioinformaticParameters.class);
	}
	
	@Override
	protected Object getSubObject(Project objectInDB, String code) {
		return getSubObject(objectInDB);
	}

	@Override
	protected Query getSubObjectQuery(String parentCode, String code) {
		return getSubObjectQuery(parentCode);
	}
	
	@Override
	protected BioinformaticParameters getSubObjects(Project objectInDB) {
		return objectInDB.bioinformaticParameters;
	}
	
	protected Query getSubObjectQuery(String parentCode){
		return DBQuery.and(DBQuery.is("code", parentCode), DBQuery.exists("bioinformaticParameters"));
	}
	
	
	protected BioinformaticParameters getSubObject(Project object){
		return object.bioinformaticParameters;
	}
	
	
	//@Permission(value={"reading"})
	public Result get(String parentCode){
		Project objectInDB = getObject(getSubObjectQuery(parentCode));
		if (objectInDB == null) {
			return notFound();			
		}
		return ok(Json.toJson(getSubObject(objectInDB)));		
	}
	
	//@Permission(value={"reading"})
	public Result head(String parentCode){
		if(!isObjectExist(getSubObjectQuery(parentCode))){
			return notFound();
		}
		return ok();
	}
	
	
	//@Permission(value={"bioinformaticParameters"})
	public Result put(String parentCode){
		Project objectInDB = getObject(parentCode);
		if (objectInDB == null) {
			return notFound();
		}
		Form<BioinformaticParameters> filledForm = getSubFilledForm();
		BioinformaticParameters inputBioinfParams = filledForm.get();
		
		updateObject(DBQuery.is("code", parentCode), 
				DBUpdate.set("bioinformaticParameters", inputBioinfParams)
				.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
		
		return get(parentCode);
	}





	

}
