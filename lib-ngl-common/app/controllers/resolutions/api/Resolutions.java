package controllers.resolutions.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfigurations;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
import fr.cea.ig.MongoDBResult;

/**
 * Controller around ResolutionConfigurations object
 *
 */
@Controller
public class Resolutions extends DocumentController<ResolutionConfigurations> {
	
	final static Form<ResolutionConfigurationsSearchForm> searchForm = form(ResolutionConfigurationsSearchForm.class); 
	final static Form<ResolutionConfigurations> resolutionConfigurationsForm = form(ResolutionConfigurations.class);
	
	public Resolutions() {
		super(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfigurations.class);		
	}


	public Result list() {
		Form<ResolutionConfigurationsSearchForm> filledForm = filledFormQueryString(searchForm, ResolutionConfigurationsSearchForm.class);
		ResolutionConfigurationsSearchForm form = filledForm.get();
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);

		if(form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			keys.put("objectTypeCode", 1);
			keys.put("typeCodes", 1);
			keys.put("resolutions", 1);
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<ResolutionConfigurations> results = mongoDBFinder(form, q, keys);			
			List<ResolutionConfigurations> resolutionConfigurations = results.toList();			
			return ok(Json.toJson(toListResolutions(resolutionConfigurations)));
		}
		else {
			//TODO 
			return null;
		}
	}
	
	
	
	private List<Resolution> toListResolutions(List<ResolutionConfigurations> resolutionConfigurations){
		List<Resolution> resos = new ArrayList<Resolution>();
		for(ResolutionConfigurations rc : resolutionConfigurations){
			for (Resolution reso : rc.resolutions) {
				resos.add(reso);
			}
		}
		return resos;
	}
	
	private Query getQuery(ResolutionConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;		
		
		if (StringUtils.isNotBlank(form.typeCode)) { 
			queries.add(DBQuery.is("typeCodes", form.typeCode));
		}else if (CollectionUtils.isNotEmpty(form.typeCodes)) { 
			queries.add(DBQuery.in("typeCodes", form.typeCodes));
		}
		
		if (StringUtils.isNotBlank(form.objectTypeCode)) { 
			queries.add(DBQuery.is("objectTypeCode", form.objectTypeCode));
		}else if (CollectionUtils.isNotEmpty(form.objectTypeCodes)) { 
			queries.add(DBQuery.in("objectTypeCode", form.objectTypeCodes));
		}
				
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}

}