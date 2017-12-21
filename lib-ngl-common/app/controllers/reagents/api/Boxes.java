package controllers.reagents.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.instance.Box;
import models.laboratory.reagent.utils.ReagentCodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
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

public class Boxes extends DocumentController<Box> {
	
	@Inject
	public Boxes(NGLContext ctx) {
		super(ctx,InstanceConstants.REAGENT_INSTANCE_COLL_NAME, Box.class);
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
		
		Box box = boxFilledForm.get();
		box.code = ReagentCodeHelper.getInstance().generateBoxCode(box.kitCode);
		box.code = ReagentCodeHelper.getInstance().generateBoxCode();
		
		
		box.traceInformation = new TraceInformation();
		box.traceInformation.createUser =  getCurrentUser();
		box.traceInformation.creationDate = new Date();
		
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), boxFilledForm.errors());
		contextValidation.setCreationMode();

		//When the user want to declare the box only, the kitCode = the boxCode
		//in order to search it in the interface
		if(box.declarationType.equals("box")){
			box.kitCode = box.code;
		}
		
		box = (Box)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, box, contextValidation);
		if (!contextValidation.hasErrors()) {
			return ok(Json.toJson(box));
		} else {
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		}
			// legit, should modify source to use contextvlidation
	}

	public Result update(String code){
		Form<Box> boxFilledForm = getMainFilledForm();
		Box box = boxFilledForm.get();
		
		box.traceInformation.modifyUser =  getCurrentUser();
		box.traceInformation.modifyDate = new Date();

		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), boxFilledForm.errors());
		contextValidation.setUpdateMode();

		box = (Box)InstanceHelpers.save(InstanceConstants.REAGENT_INSTANCE_COLL_NAME, box, contextValidation);
		if (!contextValidation.hasErrors()) { 
			return ok(Json.toJson(box));
		} else {
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		}
		// legit, should modify source to use contextvalidation
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

		if(StringUtils.isNotBlank(boxSearch.code)){
			queryElts.add(DBQuery.is("code", boxSearch.code));
		}
		
		if(StringUtils.isNotBlank(boxSearch.kitCode)){
			queryElts.add(DBQuery.is("kitCode", boxSearch.kitCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.orderCode)){
			queryElts.add(DBQuery.is("orderCode", boxSearch.orderCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.catalogRefCode)){
			queryElts.add(DBQuery.is("catalogRefCode", boxSearch.catalogRefCode));
		}
		
		if(StringUtils.isNotBlank(boxSearch.createUser)){
			queryElts.add(DBQuery.is("traceInformation.createUser", boxSearch.createUser));
		}
		
		
		if(StringUtils.isNotBlank(boxSearch.providerOrderCode)){
			queryElts.add(DBQuery.is("orderInformations.providerOrderCode", boxSearch.providerOrderCode));
		}
		
		if(boxSearch.toExpirationDate != null){
			Logger.info((DateUtils.addDays(boxSearch.toExpirationDate, 1)).toString());
			queryElts.add(DBQuery.lessThanEquals("expirationDate", (DateUtils.addDays(boxSearch.toExpirationDate, 1))));
		}
		
		if(boxSearch.fromReceptionDate != null){
			queryElts.add(DBQuery.greaterThanEquals("receptionDate", boxSearch.fromReceptionDate));
		}
		
		if(boxSearch.toReceptionDate != null){
			queryElts.add(DBQuery.lessThanEquals("receptionDate", (DateUtils.addDays(boxSearch.toReceptionDate, 1))));
		}
		
		if(boxSearch.catalogCodes != null){
			queryElts.add(DBQuery.in("catalogCode", boxSearch.catalogCodes));
		}
		if(StringUtils.isNotEmpty(boxSearch.providerID) && StringUtils.isNotEmpty(boxSearch.lotNumber)){
			queryElts.add(DBQuery.or(DBQuery.regex("providerID", Pattern.compile(boxSearch.providerID, Pattern.CASE_INSENSITIVE)),DBQuery.regex("lotNumber", Pattern.compile(boxSearch.lotNumber, Pattern.CASE_INSENSITIVE))));
		}else{
			if(StringUtils.isNotEmpty(boxSearch.providerID)){
				queryElts.add(DBQuery.regex("providerID", Pattern.compile(boxSearch.providerID, Pattern.CASE_INSENSITIVE)));
			} 
			
			if(StringUtils.isNotBlank(boxSearch.lotNumber)){
				queryElts.add(DBQuery.regex("lotNumber", Pattern.compile(boxSearch.lotNumber, Pattern.CASE_INSENSITIVE)));
			}
		}
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;
	}
}
