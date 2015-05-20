package controllers.valuation.api;


import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.valuation.instance.ValuationCriteria;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ValuationCriterias extends CommonController {
	final static Form<ValuationCriteria> valuationCriteriaForm = form(ValuationCriteria.class);
	final static Form<ValuationCriteriasSearchForm> searchForm = form(ValuationCriteriasSearchForm.class);
	
	public static Result list() {
		Form<ValuationCriteriasSearchForm> filledForm = filledFormQueryString(searchForm, ValuationCriteriasSearchForm.class);
		ValuationCriteriasSearchForm form = filledForm.get();
		
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<ValuationCriteria> results = mongoDBFinder(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, form, ValuationCriteria.class, q, keys);				
			List<ValuationCriteria> list = results.toList();
			return ok(Json.toJson(new DatatableResponse<ValuationCriteria>(list, results.count())));
		}else if(form.list){
			MongoDBResult<ValuationCriteria> results = mongoDBFinder(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, form, ValuationCriteria.class, q, keys);							
			List<ValuationCriteria> criterias = results.toList();
			return ok(Json.toJson(criterias));
		}else {
			MongoDBResult<ValuationCriteria> results = mongoDBFinder(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, form, ValuationCriteria.class, q, keys);							
			List<ValuationCriteria> criterias = results.toList();
			return ok(Json.toJson(criterias));
		}
	}
	
	private static Query getQuery(ValuationCriteriasSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (null != form.objectTypeCode) { //all
			queries.add(DBQuery.is("objectTypeCode", form.objectTypeCode.toString()));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCodes", form.typeCodes));
		}else if(StringUtils.isNotBlank(form.typeCode)){
			queries.add(DBQuery.in("typeCodes", form.typeCode));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		
		return query;
	}

	public static Result get(String code) {
		ValuationCriteria reportingConfiguration =  getByCode(code);		
		if(reportingConfiguration != null) {
			return ok(Json.toJson(reportingConfiguration));	
		} 		
		else {
			return notFound();
		}			
	}
	
	public static Result save() {
		Form<ValuationCriteria> filledForm = getFilledForm(valuationCriteriaForm, ValuationCriteria.class);
		ValuationCriteria objectInput = filledForm.get();

		if (null == objectInput._id) {
			objectInput.traceInformation = new TraceInformation();
			objectInput.traceInformation
					.setTraceInformation(getCurrentUser());
			objectInput.code = generateCode();
		} else {
			return badRequest("use PUT method to update the run");
		}

		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		objectInput.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			objectInput = MongoDBDAO.save(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, objectInput);
			return ok(Json.toJson(objectInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result update(String code) {
		ValuationCriteria objectFromDB = getByCode(code);
		if(objectFromDB == null) {
			return badRequest("ReportingConfiguration with code "+objectFromDB+" does not exist");
		}
		Form<ValuationCriteria> filledForm = getFilledForm(valuationCriteriaForm, ValuationCriteria.class);
		ValuationCriteria objectInput = filledForm.get();

		if (objectFromDB.code.equals(code)) {
			if(null != objectInput.traceInformation){
				objectInput.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
			ctxVal.setCreationMode();
			objectFromDB.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, objectInput);
				return ok(Json.toJson(objectInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("readset code are not the same");
		}				
	}
	
	public static Result delete(String code) {
		ValuationCriteria objectFromDB =  getByCode(code);
		if(objectFromDB == null) {
			return badRequest("ReportingConfiguration with code "+objectFromDB+" does not exist");
		}
		MongoDBDAO.deleteByCode(InstanceConstants.VALUATION_CRITERIA_COLL_NAME,  ValuationCriteria.class, objectFromDB.code);
		return ok();
	}
	
	private static String generateCode(){
		return ("VC-"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())).toUpperCase();		
	}
	
	private static ValuationCriteria getByCode(String code) {
		ValuationCriteria reportingConfiguration = MongoDBDAO.findByCode(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class, code);
    	return reportingConfiguration;
	}
}
