package controllers.reagents.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.instance.Box;
import models.laboratory.reagent.utils.ReagentCodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Boxes extends DocumentController<Box>{
	public Boxes() {
		super(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, Box.class);
	}

	final static Form<BoxSearchForm> boxSearchForm = form(BoxSearchForm.class);

	public Result get(String code){
		Box box = getObject(code);
		if(box != null){
			return ok(Json.toJson(box));
		}

		return badRequest();
	}

	public Result delete(String code){
		MongoDBDAO.delete(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, AbstractCatalog.class, DBQuery.or(DBQuery.is("code", code),DBQuery.is("boxCode", code)));
		return ok();
	}

	public Result save(){
		Form<Box> boxFilledForm = getMainFilledForm();
		if(!mainForm.hasErrors()){
			Box box = boxFilledForm.get();
			box.code = ReagentCodeHelper.getInstance().generateBoxCode(box.kitCode);
			
			
			box.traceInformation = new TraceInformation();
			box.traceInformation.createUser =  getCurrentUser();
			box.traceInformation.creationDate = new Date();
			
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), mainForm.errors());
			contextValidation.setCreationMode();
			/*if(ValidationHelper.required(contextValidation, box.name, "name")){
				boxCatalog.code = CodeHelper.getInstance().generateBoxCatalogCode(boxCatalog.name);
			}*/

			box = (Box)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, box, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok(Json.toJson(box));
			}
		}
		return badRequest(mainForm.errorsAsJson());
	}

	public Result update(String code){
		Form<Box> boxFilledForm = getMainFilledForm();
		if(!mainForm.hasErrors()){
			Box box = boxFilledForm.get();
			
			box.traceInformation.modifyUser =  getCurrentUser();
			box.traceInformation.modifyDate = new Date();

			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), mainForm.errors());
			contextValidation.setUpdateMode();

			box = (Box)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, box, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok(Json.toJson(box));
			}
		}
		return badRequest(mainForm.errorsAsJson());
	}

	public Result list(){
		Form<BoxSearchForm> boxFilledForm = filledFormQueryString(boxSearchForm,BoxSearchForm.class);
		BoxSearchForm boxSearch = boxFilledForm.get();
		BasicDBObject keys = getKeys(boxSearch);
		DBQuery.Query query = getQuery(boxSearch);

		if(boxSearch.datatable){
			MongoDBResult<Box> results =  mongoDBFinder(boxSearch, query);
			List<Box> boxs = results.toList();

			return ok(Json.toJson(new DatatableResponse<Box>(boxs, results.count())));
		}else if (boxSearch.list){
			keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("category", 1);

			if(null == boxSearch.orderBy)boxSearch.orderBy = "code";
			if(null == boxSearch.orderSense)boxSearch.orderSense = 0;				

			MongoDBResult<Box> results = mongoDBFinder(boxSearch, query, keys);
			List<Box> boxs = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Box p: boxs){					
				los.add(new ListObject(p.code, p.code));								
			}

			return Results.ok(Json.toJson(los));
		}else{
			if(null == boxSearch.orderBy)boxSearch.orderBy = "code";
			if(null == boxSearch.orderSense)boxSearch.orderSense = 0;

			MongoDBResult<Box> results = mongoDBFinder(boxSearch, query);
			List<Box> boxs = results.toList();

			return ok(Json.toJson(boxs));
		}
	}

	public static Query getQuery(BoxSearchForm boxSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;
		queryElts.add(DBQuery.is("category", "Box"));

		if(StringUtils.isNotBlank(boxSearch.kitCode)){
			queryElts.add(DBQuery.is("kitCode", boxSearch.kitCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.orderCode)){
			queryElts.add(DBQuery.is("orderCode", boxSearch.orderCode));
		}
		
		
		if(StringUtils.isNotBlank(boxSearch.bundleBarCode)){
			queryElts.add(DBQuery.is("bundleBarCode", boxSearch.bundleBarCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.catalogRefCode)){
			queryElts.add(DBQuery.is("catalogRefCode", boxSearch.catalogRefCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.bundleBarCode)){
			queryElts.add(DBQuery.is("bundleBarCode", boxSearch.bundleBarCode));
		}
		
		if(boxSearch.catalogCodes != null){
			queryElts.add(DBQuery.in("catalogCodes", boxSearch.catalogCodes));
		}
		
		if(StringUtils.isNotEmpty(boxSearch.barCode)){
			queryElts.add(DBQuery.or(DBQuery.regex("barCode", Pattern.compile(boxSearch.barCode+"_|_"+boxSearch.barCode)),DBQuery.regex("reagents.code", Pattern.compile(boxSearch.barCode+"_|_"+boxSearch.barCode))));
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;
	}
}
