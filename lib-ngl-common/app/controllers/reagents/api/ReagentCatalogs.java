package controllers.reagents.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.reagent.description.ReagentCatalog;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;

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
import fr.cea.ig.MongoDBResult;

public class ReagentCatalogs extends DocumentController<ReagentCatalog>{
	
	final static Form<ReagentCatalogSearchForm> ReagentCatalogSearchForm = form(ReagentCatalogSearchForm.class);
	
	public ReagentCatalogs() {
		super(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class);
	}
	
	public Result save(){
		Form<ReagentCatalog> ReagentCatalogFilledForm = getMainFilledForm();
		if(!mainForm.hasErrors()){
			ReagentCatalog reagentCatalog = ReagentCatalogFilledForm.get();
			reagentCatalog.code = CodeHelper.generateReagentCatalogCode(reagentCatalog.name);
			
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), mainForm.errors());
			contextValidation.setCreationMode();
			
			reagentCatalog = (ReagentCatalog)InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, reagentCatalog, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok(Json.toJson(reagentCatalog));
			}
		}
		return badRequest(mainForm.errorsAsJson());
	}
	
	public Result list(){
		Form<ReagentCatalogSearchForm> reagentCatalogFilledForm = filledFormQueryString(ReagentCatalogSearchForm,ReagentCatalogSearchForm.class);
		ReagentCatalogSearchForm reagentCatalogSearch = reagentCatalogFilledForm.get();
		BasicDBObject keys = getKeys(reagentCatalogSearch);
		DBQuery.Query query = getQuery(reagentCatalogSearch);

		if(reagentCatalogSearch.datatable){
			MongoDBResult<ReagentCatalog> results =  mongoDBFinder(reagentCatalogSearch, query);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			
			return ok(Json.toJson(new DatatableResponse<ReagentCatalog>(ReagentCatalogs, results.count())));
		}else if (reagentCatalogSearch.list){
			keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("name", 1);
			keys.put("category", 1);
			
			if(null == reagentCatalogSearch.orderBy)reagentCatalogSearch.orderBy = "code";
			if(null == reagentCatalogSearch.orderSense)reagentCatalogSearch.orderSense = 0;				
			
			MongoDBResult<ReagentCatalog> results = mongoDBFinder(reagentCatalogSearch, query, keys);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(ReagentCatalog p: ReagentCatalogs){					
					los.add(new ListObject(p.code, p.name));								
			}
			
			return Results.ok(Json.toJson(los));
		}else{
			if(null == reagentCatalogSearch.orderBy)reagentCatalogSearch.orderBy = "code";
			if(null == reagentCatalogSearch.orderSense)reagentCatalogSearch.orderSense = 0;
			
			MongoDBResult<ReagentCatalog> results = mongoDBFinder(reagentCatalogSearch, query);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			
			return ok(Json.toJson(ReagentCatalogs));
		}
	}
	
	private static Query getQuery(ReagentCatalogSearchForm ReagentCatalogSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;
		queryElts.add(DBQuery.is("category", "Reagent"));
		
		if(StringUtils.isNotEmpty(ReagentCatalogSearch.kitCatalogCode)){
			queryElts.add(DBQuery.is("kitCatalogCode", ReagentCatalogSearch.kitCatalogCode));
		}
		
		if(StringUtils.isNotEmpty(ReagentCatalogSearch.boxCatalogCode)){
			queryElts.add(DBQuery.is("boxCatalogCode", ReagentCatalogSearch.boxCatalogCode));
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;
	}
}
