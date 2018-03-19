package controllers.reagents.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.ReagentCatalog;
import models.laboratory.reagent.utils.ReagentCodeHelper;
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
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

public class ReagentCatalogs extends DocumentController<ReagentCatalog> {
	
	private final /*static*/ Form<ReagentCatalogSearchForm> ReagentCatalogSearchForm; // = form(ReagentCatalogSearchForm.class);
	
	@Inject
	public ReagentCatalogs(NGLContext ctx) {
		super(ctx,InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class);
		ReagentCatalogSearchForm = ctx.form(ReagentCatalogSearchForm.class);
	}
	
	public Result save() {
		Form<ReagentCatalog> ReagentCatalogFilledForm = getMainFilledForm();
		
		ReagentCatalog reagentCatalog = ReagentCatalogFilledForm.get();
		reagentCatalog.code = ReagentCodeHelper.getInstance().generateReagentCatalogCode(reagentCatalog.name);
		
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), ReagentCatalogFilledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), ReagentCatalogFilledForm);
		contextValidation.setCreationMode();
		
//		reagentCatalog = (ReagentCatalog)InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, reagentCatalog, contextValidation);
		reagentCatalog = InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, reagentCatalog, contextValidation);
		if (contextValidation.hasErrors())
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		return ok(Json.toJson(reagentCatalog));
		// legit, spaghetti above
	}
	
	public Result update(String code) {
		Form<ReagentCatalog> reagentCatalogFilledForm = getMainFilledForm();
		ReagentCatalog reagentCatalog = reagentCatalogFilledForm.get();
		
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), reagentCatalogFilledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), reagentCatalogFilledForm);
		contextValidation.setUpdateMode();

//		reagentCatalog = (ReagentCatalog)InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, reagentCatalog, contextValidation);
		reagentCatalog = InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, reagentCatalog, contextValidation);
		if (contextValidation.hasErrors())
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		return ok(Json.toJson(reagentCatalog));
		// legit, spaghetti above
	}
	
	public Result delete(String code) {
		MongoDBDAO.delete(InstanceConstants.REAGENT_CATALOG_COLL_NAME, AbstractCatalog.class, DBQuery.or(DBQuery.is("code", code)));
		return ok();
	}
	
	public Result list() {
		Form<ReagentCatalogSearchForm> reagentCatalogFilledForm = filledFormQueryString(ReagentCatalogSearchForm,ReagentCatalogSearchForm.class);
		ReagentCatalogSearchForm reagentCatalogSearch = reagentCatalogFilledForm.get();
		BasicDBObject keys = getKeys(reagentCatalogSearch);
		DBQuery.Query query = getQuery(reagentCatalogSearch);

		if (reagentCatalogSearch.datatable) {
			MongoDBResult<ReagentCatalog> results =  mongoDBFinder(reagentCatalogSearch, query);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			
			return ok(Json.toJson(new DatatableResponse<ReagentCatalog>(ReagentCatalogs, results.count())));
		} else if (reagentCatalogSearch.list) {
			keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("name", 1);
			keys.put("category", 1);
			keys.put("kitCatalogCode",1);
			
			if (reagentCatalogSearch.orderBy    == null) reagentCatalogSearch.orderBy    = "code";
			if (reagentCatalogSearch.orderSense == null) reagentCatalogSearch.orderSense = 0;				
			
			MongoDBResult<ReagentCatalog> results = mongoDBFinder(reagentCatalogSearch, query, keys);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for (ReagentCatalog p: ReagentCatalogs)
					los.add(new ListObject(p.code, p.name));								
			return Results.ok(Json.toJson(los));
		} else {
			if (reagentCatalogSearch.orderBy    == null) reagentCatalogSearch.orderBy    = "code";
			if (reagentCatalogSearch.orderSense == null) reagentCatalogSearch.orderSense = 0;
			
			MongoDBResult<ReagentCatalog> results = mongoDBFinder(reagentCatalogSearch, query);
			List<ReagentCatalog> ReagentCatalogs = results.toList();
			
			return ok(Json.toJson(ReagentCatalogs));
		}
	}
	
	private static Query getQuery(ReagentCatalogSearchForm ReagentCatalogSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;
		queryElts.add(DBQuery.is("category", "Reagent"));
		
		if(StringUtils.isNotEmpty(ReagentCatalogSearch.kitCatalogCode)){
			queryElts.add(DBQuery.is("kitCatalogCode", ReagentCatalogSearch.kitCatalogCode));
		}
		
		if(StringUtils.isNotEmpty(ReagentCatalogSearch.catalogRefCode)){
			queryElts.add(DBQuery.is("catalogRefCode", ReagentCatalogSearch.catalogRefCode));
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
