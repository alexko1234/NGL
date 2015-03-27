package controllers.reagents.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.instance.Kit;
import models.laboratory.reagent.utils.ReagentCodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
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

public class Kits extends DocumentController<Kit>{

	public Kits() {
		super(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, Kit.class);
	}

	final static Form<KitSearchForm> kitSearchForm = form(KitSearchForm.class);

	public Result get(String code){
		Kit kit = getObject(code);
		if(kit != null){
			return ok(Json.toJson(kit));
		}

		return badRequest();
	}

	public Result delete(String code){
		MongoDBDAO.delete(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, AbstractCatalog.class, DBQuery.or(DBQuery.is("code", code),DBQuery.is("kitCode", code)));
		return ok();
	}

	public Result save(){
		Form<Kit> kitFilledForm = getMainFilledForm();
		if(!mainForm.hasErrors()){
			Kit kit = kitFilledForm.get();
			kit.code = ReagentCodeHelper.getInstance().generateKitCode();
			kit.traceInformation = new TraceInformation();
			kit.traceInformation.createUser =  getCurrentUser();
			kit.traceInformation.creationDate = new Date();
			
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), mainForm.errors());
			contextValidation.setCreationMode();
			/*if(ValidationHelper.required(contextValidation, kit.name, "name")){
				kitCatalog.code = CodeHelper.getInstance().generateKitCatalogCode(kitCatalog.name);
			}*/

			kit = (Kit)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, kit, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok(Json.toJson(kit));
			}
		}
		return badRequest(mainForm.errorsAsJson());
	}

	public Result update(String code){
		Form<Kit> kitFilledForm = getMainFilledForm();
		if(!mainForm.hasErrors()){
			Kit kit = kitFilledForm.get();

			kit.traceInformation.modifyUser =  getCurrentUser();
			kit.traceInformation.modifyDate = new Date();
			
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), mainForm.errors());
			contextValidation.setUpdateMode();

			kit = (Kit)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, kit, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok(Json.toJson(kit));
			}
		}
		return badRequest(mainForm.errorsAsJson());
	}

	public Result list(){
		Form<KitSearchForm> kitFilledForm = filledFormQueryString(kitSearchForm,KitSearchForm.class);
		KitSearchForm kitSearch = kitFilledForm.get();
		BasicDBObject keys = getKeys(kitSearch);
		DBQuery.Query query = getQuery(kitSearch);

		if(kitSearch.datatable){
			MongoDBResult<Kit> results =  mongoDBFinder(kitSearch, query);
			List<Kit> kits = results.toList();

			return ok(Json.toJson(new DatatableResponse<Kit>(kits, results.count())));
		}else if (kitSearch.list){
			keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("category", 1);

			if(null == kitSearch.orderBy)kitSearch.orderBy = "code";
			if(null == kitSearch.orderSense)kitSearch.orderSense = 0;				

			MongoDBResult<Kit> results = mongoDBFinder(kitSearch, query, keys);
			List<Kit> kits = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Kit p: kits){					
				los.add(new ListObject(p.code, p.code));								
			}

			return Results.ok(Json.toJson(los));
		}else{
			if(null == kitSearch.orderBy)kitSearch.orderBy = "code";
			if(null == kitSearch.orderSense)kitSearch.orderSense = 0;

			MongoDBResult<Kit> results = mongoDBFinder(kitSearch, query);
			List<Kit> kits = results.toList();

			return ok(Json.toJson(kits));
		}
	}

	private static Query getQuery(KitSearchForm kitSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;
		queryElts.add(DBQuery.is("category", "Kit"));

		if(CollectionUtils.isNotEmpty(kitSearch.catalogCodes)){
			queryElts.add(DBQuery.in("catalogCode", kitSearch.catalogCodes));
		}
		
		if(StringUtils.isNotBlank(kitSearch.catalogCode)){
			queryElts.add(DBQuery.is("catalogCode", kitSearch.catalogCode));
		}
		
		if(kitSearch.startToUseDate != null){
			queryElts.add(DBQuery.greaterThanEquals("startToUseDate", kitSearch.startToUseDate));
		}
		
		if(kitSearch.stopToUseDate != null){
			queryElts.add(DBQuery.lessThanEquals("stopToUseDate", (DateUtils.addDays(kitSearch.stopToUseDate, 1))));
		}
		
		if(StringUtils.isNotBlank(kitSearch.barCode)){
			queryElts.add(DBQuery.regex("barCode", Pattern.compile(kitSearch.barCode+"_|_"+kitSearch.barCode)));
		}
		
		if(StringUtils.isNotBlank(kitSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", kitSearch.stateCode));
		}
		
		if(StringUtils.isNotBlank(kitSearch.orderCode)){
			queryElts.add(DBQuery.is("orderCode", kitSearch.orderCode));
		}
		
		if(kitSearch.expirationDate != null){
			queryElts.add(DBQuery.is("expirationDate", kitSearch.expirationDate));
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;
	}
}
