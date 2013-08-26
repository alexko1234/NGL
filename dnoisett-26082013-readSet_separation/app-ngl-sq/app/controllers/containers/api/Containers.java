package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebeaninternal.server.persist.Constant;


import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.dao.DAOException;



public class Containers extends CommonController {
	
	final static Form<ContainersSearchForm> containerForm = form(ContainersSearchForm.class);
	
	public static Result list(){
		Form<ContainersSearchForm> containerFilledForm = containerForm.bindFromRequest();
		ContainersSearchForm containersSearch = containerFilledForm.get();
		DBQuery.Query query = getQuery(containersSearch);
	    MongoDBResult<Container> results = MongoDBDAO.find(Constants.CONTAINER_COLL_NAME, Container.class, query)
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
	private static DBQuery.Query getQuery(ContainersSearchForm containersSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		if(StringUtils.isNotEmpty(containersSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCode));
	    }
	   
	    if(StringUtils.isNotEmpty(containersSearch.stateCode)){
	    	queryElts.add(DBQuery.is("stateCode", containersSearch.stateCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.categoryCode)){
	    	queryElts.add(DBQuery.is("categoryCode", containersSearch.categoryCode));
	    }
	   
	    if(StringUtils.isNotEmpty(containersSearch.sampleCode)){
	    	queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.processTypeCode) && StringUtils.isEmpty(containersSearch.experimentTypeCode)){
	    	List<String> listePrevious = Spring.getBeanOfType(ExperimentTypeDAO.class).findVoidProcessExperimentTypeCode(containersSearch.processTypeCode);
	    	if(null != listePrevious && listePrevious.size() > 0){
	    		queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", listePrevious),DBQuery.notExists("fromExperimentTypeCodes")));
	    	}	    		    	
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.experimentTypeCode)){
			try {
				
				List<ExperimentType> previous = Spring.getBeanOfType(ExperimentTypeDAO.class).findPreviousExperimentTypeForAnExperimentTypeCode(containersSearch.experimentTypeCode);
				List<String> previousString = new ArrayList<String>();
				for(ExperimentType e:previous){
					previousString.add(e.code);
				}
				queryElts.add(DBQuery.in("fromExperimentTypeCodes", previousString));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	   if(StringUtils.isNotEmpty(containersSearch.processTypeCode) && StringUtils.isNotEmpty(containersSearch.experimentTypeCode)){
	    	queryElts.add(DBQuery.is("processTypeCode", containersSearch.processTypeCode));
	   }
	   
		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
	
}