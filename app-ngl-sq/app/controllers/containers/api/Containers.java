package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;



public class Containers extends CommonController {
	
	private static final String CONTAINER_COLL_NAME = "Container";
	final static Form<ContainersSearch> containerForm = form(ContainersSearch.class);
	
	public static Result list(){
		Form<ContainersSearch> containerFilledForm = containerForm.bindFromRequest();
		ContainersSearch containersSearch = containerFilledForm.get();
		DBQuery.Query query = getQuery(containersSearch);
	    MongoDBResult<Container> results = MongoDBDAO.find(CONTAINER_COLL_NAME, Container.class, query)
				.sort(DatatableHelpers.getOrderBy(containerFilledForm), getMongoDBOrderSense(containerFilledForm))
				.page(DatatableHelpers.getPageNumber(containerFilledForm), DatatableHelpers.getNumberRecordsPerPage(containerFilledForm)); 
		List<Container> containers = results.toList();
		return ok(Json.toJson(new DatatableResponse(containers, results.count())));
	}

	/**
	 * Construct the container query
	 * @param containersSearch
	 * @return
	 */
	private static DBQuery.Query getQuery(ContainersSearch containersSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Logger.info("Containers Query : "+containersSearch);
		if(StringUtils.isNotEmpty(containersSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCode));
	    }
		
	    if(StringUtils.isNotEmpty(containersSearch.fromExperimentCode)){
	    	queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", containersSearch.fromExperimentCode), DBQuery.is("fromExperimentTypeCodes", null)));
	    }
	    
	    
	    if(StringUtils.isNotEmpty(containersSearch.stateCode)){
	    	queryElts.add(DBQuery.is("stateCode", containersSearch.stateCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.sampleCode)){
	    	queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.processTypeCode)){
	    	List<String> listePrevious = Spring.getBeanOfType(ExperimentTypeDAO.class).findPreviousExperimentTypeCode(containersSearch.processTypeCode);
	    	if(null != listePrevious && listePrevious.size() > 0){
	    		Logger.info("fromExperimentTypeCodes Query : "+listePrevious);
	    		queryElts.add(DBQuery.in("fromExperimentTypeCodes", listePrevious));
	    	}	    		    	
	    }
	    
	    
		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
	
}