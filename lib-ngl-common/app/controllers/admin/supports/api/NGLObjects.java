package controllers.admin.supports.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.laboratory.common.description.Level;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

import com.mongodb.BasicDBObject;

import controllers.APICommonController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

public class NGLObjects extends APICommonController<NGLObject>{

	final static Form<NGLObjectsSearchForm> searchForm = form(NGLObjectsSearchForm.class);
	
	
	public NGLObjects() {
		super(NGLObject.class);		
	}

	@Permission(value={"reading"})
	public Result list() {
		NGLObjectsSearchForm form = filledFormQueryString(NGLObjectsSearchForm.class);
		
		Form d = Form.form(); //just used to have errors
		ContextValidation cv = new ContextValidation(getCurrentUser(), d.errors());
		form.validate(cv);
		if(cv.hasErrors()){
			return badRequest(Json.toJson(d.errorsAsJson()));
		}else{
			List<NGLObject> results = form.collectionNames
				.stream()
				.map(collectionName -> getNGLObjects(form, collectionName))
				.flatMap(List::stream)
				.collect(Collectors.toList());			
			return ok(Json.toJson(results));
		}
	}
	
	
	public Result update(){
		Form<NGLObject> filledForm = getMainFilledForm();
		NGLObject input = filledForm.get();
		ContextValidation cv = new ContextValidation(getCurrentUser(), filledForm.errors());
		input.validate(cv);
		if(cv.hasErrors()){
			return badRequest(filledForm.errorsAsJson());
		}else{
			return ok();
		}
		
	}

	private List<NGLObject> getNGLObjects(NGLObjectsSearchForm form, String collectionName) {
		
		Query q = getQuery(form, collectionName);
		if(null != q){
			List<NGLObject> r = MongoDBDAO.find(collectionName, NGLObject.class, q, getKeys()).toList();
			r.forEach(o -> {
				o.collectionName = collectionName;
				o.contentPropertyNameUpdated = form.contentPropertyNameUpdated;
				o.currentValue = form.contentProperties.get(form.contentPropertyNameUpdated).get(0);
				o.projectCode = form.projectCode;
				o.sampleCode = form.sampleCode;				
			});
			return r;
		}else{
			return Collections.emptyList();
		}
		
	}

	private Query getQuery(NGLObjectsSearchForm form, String collectionName) {
		Query query = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		switch (collectionName) {
			case "ngl_sq.Container":
				queryElts.add(getProjectCodeQuery(form, ""));
				queryElts.add(getSampleCodeQuery(form, ""));
				queryElts.addAll(getContentPropertiesQuery(form, ""));
				query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
				query = DBQuery.elemMatch("contents", query);
				break;
				
			case "ngl_sq.Process":
				queryElts.add(getProjectCodeQuery(form, "sampleOnInputContainer."));
				queryElts.add(getSampleCodeQuery(form, "sampleOnInputContainer."));
				queryElts.addAll(getContentPropertiesQuery(form, "sampleOnInputContainer."));
				query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
				break;
				
			case "ngl_bi.ReadSetIllumina":
				queryElts.add(getProjectCodeQuery(form, "sampleOnContainer."));
				queryElts.add(getSampleCodeQuery(form, "sampleOnContainer."));
				queryElts.addAll(getContentPropertiesQuery(form, "sampleOnContainer."));
				query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));	
				break;
				
			case "ngl_sq.Experiment":
				queryElts.add(getProjectCodeQuery(form, ""));
				queryElts.add(getSampleCodeQuery(form, ""));
				queryElts.addAll(getContentPropertiesQuery(form, ""));
				query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
				query = DBQuery.elemMatch("atomicTransfertMethods.outputContainerUseds.contents", query);	
				break;
		}		
		
		return query;
	}

	private List<Query> getContentPropertiesQuery(NGLObjectsSearchForm form, String prefix) {
		return NGLControllerHelper.generateQueriesForProperties(form.contentProperties,Level.CODE.Content, prefix+"properties");
	}
	
	
	private Query getSampleCodeQuery(NGLObjectsSearchForm form, String prefix) {
		if(StringUtils.isNotBlank(form.sampleCode)){
			return DBQuery.in(prefix+"sampleCode", form.sampleCode);
		}else{
			return DBQuery.empty();
		}
	}

	private Query getProjectCodeQuery(NGLObjectsSearchForm form, String prefix) {		
		if(StringUtils.isNotBlank(form.projectCode)){
			return DBQuery.in(prefix+"projectCode", form.projectCode);
		}else{
			return DBQuery.empty();
		}
	}

	private BasicDBObject getKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("code",1);
		keys.put("typeCode",1);
		
		return keys;
	}
}
