package controllers.receptions.api;


// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.utils.InstanceConstants;
import models.utils.ListObject;

import org.mongojack.DBQuery;

//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.ListForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

public class ReceptionConfigurations extends DocumentController<ReceptionConfiguration> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(ReceptionConfigurations.class);
//	private final Form<ReceptionConfiguration> reportConfigForm;// = form(ReceptionConfiguration.class);
	
	@Inject
	public ReceptionConfigurations(NGLContext ctx) {
		super(ctx,InstanceConstants.RECEPTION_CONFIG_COLL_NAME, ReceptionConfiguration.class);	
//		reportConfigForm = ctx.form(ReceptionConfiguration.class);
	}
	
	@Permission(value={"reading"})
	public Result list() {
		ListForm searchForm = filledFormQueryString(ListForm.class);
		DBQuery.Query query = DBQuery.empty();
		if (searchForm.datatable) {
			MongoDBResult<ReceptionConfiguration> results = mongoDBFinder(searchForm, query);
			List<ReceptionConfiguration> configurations = results.toList();
			return ok(Json.toJson(new DatatableResponse<ReceptionConfiguration>(configurations, results.count())));
		} else if(searchForm.list) {
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			keys.put("displayOrder", 1);
			MongoDBResult<ReceptionConfiguration> results = mongoDBFinder(searchForm,query).sort("displayOrder");
			List<ReceptionConfiguration> configurations = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(ReceptionConfiguration p: configurations)
				los.add(new ListObject(p.code, p.name));
			return Results.ok(Json.toJson(los));
		} else {
			MongoDBResult<ReceptionConfiguration> results = mongoDBFinder(searchForm, query);
			List<ReceptionConfiguration> configurations = results.toList();
			return Results.ok(Json.toJson(configurations));
		}		
	}
	
	@Permission(value={"writing"})
	public Result save() {
		Form<ReceptionConfiguration> filledForm = getMainFilledForm();
		ReceptionConfiguration input = filledForm.get();

		if (input._id == null) {
			input.traceInformation = new TraceInformation();
			input.traceInformation.setTraceInformation(getCurrentUser());
			input.code = generateReceptionConfigurationCode();
		} else {
			return badRequest("use PUT method to update the ReceptionConfiguration");
		}

//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.setCreationMode();
		input.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			input = saveObject(input);			
			return ok(Json.toJson(input));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}	
	}
	
	@Permission(value={"writing"})
	public Result update(String code) {
		ReceptionConfiguration objectInDB = getObject(code);
		if (objectInDB == null) {
			return badRequest("ReceptionConfiguration with code "+code+" not exist");
		}
		Form<ReceptionConfiguration> filledForm = getMainFilledForm();
		ReceptionConfiguration input = filledForm.get();
		
		if (code.equals(input.code)) {
			if (input.traceInformation != null) {
				input.traceInformation.setTraceInformation(getCurrentUser());
			} else {
				logger.error("traceInformation is null !!");
			}
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
			ctxVal.setUpdateMode();
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				updateObject(input);
				return ok(Json.toJson(input));
			} else {
				//return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("ReceptionConfiguration codes are not the same");
		}
	}
	
	private static String generateReceptionConfigurationCode(){
		return ("RC-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).toUpperCase();		
	}
	
}
