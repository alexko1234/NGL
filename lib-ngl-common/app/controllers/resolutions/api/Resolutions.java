package controllers.resolutions.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.springframework.stereotype.Controller;

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
public class Resolutions extends DocumentController<ResolutionConfiguration> {

	final static Form<ResolutionConfigurationsSearchForm> searchForm = form(ResolutionConfigurationsSearchForm.class); 
	final static Form<ResolutionConfiguration> resolutionConfigurationsForm = form(ResolutionConfiguration.class);

	public Resolutions() {
		super(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class);		
	}


	public Result list() {
		Form<ResolutionConfigurationsSearchForm> filledForm = filledFormQueryString(searchForm, ResolutionConfigurationsSearchForm.class);
		ResolutionConfigurationsSearchForm form = filledForm.get();
		Query q = getQuery(form);
		MongoDBResult<ResolutionConfiguration> results = mongoDBFinder(form, q);			
		List<ResolutionConfiguration> resolutionConfigurations = results.toList();
		
		if(form.distinct){
			Map<String, String> map = new HashMap<String, String>();			
			int[] indexes = null;
			for(ResolutionConfiguration rc: resolutionConfigurations){
				for(int i=0; i<rc.resolutions.size();i++){
					if(!map.containsKey(rc.resolutions.get(i).code)){
						map.put(rc.resolutions.get(i).code, rc.resolutions.get(i).code);					
					}else{						
						rc.resolutions.remove(i);
						i--;
					}
				}	
			}	
		}

		return ok(Json.toJson(toListResolutions(resolutionConfigurations)));

	}


	private List<Resolution> toListResolutions(List<ResolutionConfiguration> resolutionConfigurations){
		List<Resolution> resos = new ArrayList<Resolution>();
		for(ResolutionConfiguration rc : resolutionConfigurations){
			for (Resolution reso : rc.resolutions) {
				resos.add(reso);
			}
		}
		Collections.sort(resos);
		return resos;
	}

	private Query getQuery(ResolutionConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;		

		if (StringUtils.isNotBlank(form.typeCode)) { 
			queries.add(DBQuery.in("typeCodes", form.typeCode));
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