package controllers.reagents.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.utils.ReagentCodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

public class KitCatalogs extends DocumentController<KitCatalog> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(KitCatalogs.class);
	
	private final /*static*/ Form<KitCatalogSearchForm> kitCatalogSearchForm;// = form(KitCatalogSearchForm.class);
	
	@Inject
	public KitCatalogs(NGLContext ctx) {
		super(ctx,InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class);
		kitCatalogSearchForm = ctx.form(KitCatalogSearchForm.class);
	}
	
	public Result get(String code){
		KitCatalog kitCatalog = getObject(code);
		if (kitCatalog != null)
			return ok(Json.toJson(kitCatalog));
		return badRequest();
	}
	
	public Result delete(String code){
		MongoDBDAO.delete(InstanceConstants.REAGENT_CATALOG_COLL_NAME, AbstractCatalog.class, DBQuery.or(DBQuery.is("code", code),DBQuery.is("kitCatalogCode", code)));
		return ok();
	}
	
	public Result save(){
		Form<KitCatalog> kitCatalogFilledForm = getMainFilledForm();
		KitCatalog kitCatalog = kitCatalogFilledForm.get();
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), kitCatalogFilledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), kitCatalogFilledForm);
		contextValidation.setCreationMode();
		if (ValidationHelper.required(contextValidation, kitCatalog.name, "name")) {
			kitCatalog.code = ReagentCodeHelper.getInstance().generateKitCatalogCode();
			kitCatalog = (KitCatalog)InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, kitCatalog, contextValidation);
		}
		if (contextValidation.hasErrors())
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		return ok(Json.toJson(kitCatalog));	
		 // legit, spaghetti above
	}
	
	public Result update(String code){
		Form<KitCatalog> kitCatalogFilledForm = getMainFilledForm();
		KitCatalog kitCatalog = kitCatalogFilledForm.get();
		
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), kitCatalogFilledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), kitCatalogFilledForm);
		contextValidation.setUpdateMode();
		
		kitCatalog = (KitCatalog)InstanceHelpers.save(InstanceConstants.REAGENT_CATALOG_COLL_NAME, kitCatalog, contextValidation);
		if (contextValidation.hasErrors())
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		return ok(Json.toJson(kitCatalog));
		 // legit, spaghetti above
	}
	
	public Result list(){
		Form<KitCatalogSearchForm> kitCatalogFilledForm = filledFormQueryString(kitCatalogSearchForm,KitCatalogSearchForm.class);
		KitCatalogSearchForm kitCatalogSearch = kitCatalogFilledForm.get();
		BasicDBObject keys = getKeys(kitCatalogSearch);
		DBQuery.Query query = getQuery(kitCatalogSearch);
		logger.debug("key kits: " + keys);

		if(kitCatalogSearch.datatable){
			MongoDBResult<KitCatalog> results =  mongoDBFinder(kitCatalogSearch, query);
			List<KitCatalog> kitCatalogs = results.toList();
			
			return ok(Json.toJson(new DatatableResponse<KitCatalog>(kitCatalogs, results.count())));
			
			
	/*	}else if (kitCatalogSearch.list){
			keys = getKeys(kitCatalogSearch);
			keys.put("code", 1);
			keys.put("name", 1);
			keys.put("category", 1);
			
			if(null == kitCatalogSearch.orderBy)kitCatalogSearch.orderBy = "code";
			if(null == kitCatalogSearch.orderSense)kitCatalogSearch.orderSense = 0;				
			
			MongoDBResult<KitCatalog> results = mongoDBFinder(kitCatalogSearch, query, keys);
			List<KitCatalog> kitCatalogs = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(KitCatalog p: kitCatalogs){					
					los.add(new ListObject(p.code, p.name));								
			}
			
			return Results.ok(Json.toJson(los)); */ 
		} else {
			if (kitCatalogSearch.orderBy    == null) kitCatalogSearch.orderBy     = "code";
			if (kitCatalogSearch.orderSense == null) kitCatalogSearch.orderSense = 0;
			
			MongoDBResult<KitCatalog> results = mongoDBFinder(kitCatalogSearch, query);
			List<KitCatalog> kitCatalogs = results.toList();
			
			return ok(Json.toJson(kitCatalogs));
		}
	}
	
	private static Query getQuery(KitCatalogSearchForm kitCatalogSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;
		queryElts.add(DBQuery.is("category", "Kit"));
		if(StringUtils.isNotBlank(kitCatalogSearch.code)){
			queryElts.add(DBQuery.is("code", kitCatalogSearch.code));
		} 
		
		if(CollectionUtils.isNotEmpty(kitCatalogSearch.codes)) {
			logger.debug("Codes: "+kitCatalogSearch.codes);
			queryElts.add(DBQuery.in("code",kitCatalogSearch.codes));
		}
		
		if(StringUtils.isNotBlank(kitCatalogSearch.name)){
			queryElts.add(DBQuery.regex("name", Pattern.compile(kitCatalogSearch.name, Pattern.CASE_INSENSITIVE)));
		}
		
		if(StringUtils.isNotBlank(kitCatalogSearch.providerRefName)){
			queryElts.add(DBQuery.regex("providerRefName", Pattern.compile(kitCatalogSearch.providerRefName,Pattern.CASE_INSENSITIVE)));
		}
		
		if(StringUtils.isNotBlank(kitCatalogSearch.providerCode)){
			queryElts.add(DBQuery.regex("providerCode", Pattern.compile(kitCatalogSearch.providerCode,Pattern.CASE_INSENSITIVE)));
		}
		
		if(StringUtils.isNotBlank(kitCatalogSearch.catalogRefCode)){
			queryElts.add(DBQuery.is("catalogRefCode", kitCatalogSearch.catalogRefCode));
		}
		
		if (CollectionUtils.isNotEmpty(kitCatalogSearch.codesFromBoxCatalog)) {
			queryElts.add(DBQuery.in("code",kitCatalogSearch.codesFromBoxCatalog));
		}
		
		if (CollectionUtils.isNotEmpty(kitCatalogSearch.codesFromReagentCatalog)) {
			queryElts.add(DBQuery.in("code",kitCatalogSearch.codesFromReagentCatalog));
		}
		
		if(kitCatalogSearch.experimentTypeCodes != null){
			queryElts.add(DBQuery.in("experimentTypeCodes", kitCatalogSearch.experimentTypeCodes));
		}
	
		if (null != kitCatalogSearch.isActive) {
			queryElts.add(DBQuery.is("active", kitCatalogSearch.isActive));
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}
		return query;
	}
	
}
