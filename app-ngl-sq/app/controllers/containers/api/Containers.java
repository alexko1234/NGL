package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.modules.mongodb.jackson.MongoDB;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Containers extends CommonController {

	final static Form<ContainersSearchForm> containerForm = form(ContainersSearchForm.class);

	public static Result get(String code){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, code);
		if(container != null){
			return ok(Json.toJson(container));
		}

		return badRequest();
	}

	public static Result list() throws DAOException{
		Form<ContainersSearchForm> containerFilledForm = filledFormQueryString(containerForm,ContainersSearchForm.class);
		ContainersSearchForm containersSearch = containerFilledForm.get();

		DBQuery.Query query = getQuery(containersSearch);
		if(containersSearch.datatable){
			MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query);
			List<Container> containers = results.toList();

			return ok(Json.toJson(new DatatableResponse<Container>(containers, results.count())));
		}else if(containersSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);

			MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query, keys);
			List<Container> containers = results.toList();

			List<ListObject> los = new ArrayList<ListObject>();
			for(Container p: containers){
				los.add(new ListObject(p.code, p.code));
			}

			return ok(Json.toJson(los));
		}else{
			List<Container> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, query).toList();

			return ok(Json.toJson(results));
		}
	}

	public static Result list_supports() throws DAOException{
		Form<ContainersSearchForm> containerFilledForm = filledFormQueryString(containerForm,ContainersSearchForm.class);
		ContainersSearchForm containersSearch = containerFilledForm.get();

		DBQuery.Query query = getQuery(containersSearch);
		if(containersSearch.datatable){
			List<LocationOnContainerSupport> containerSupports = new ArrayList<LocationOnContainerSupport>();
			
			BasicDBObject keysSupport = new BasicDBObject();
			BasicDBObject test = new BasicDBObject();
			keysSupport.put("support.supportCode",true);
			keysSupport.put("support.categoryCode",true);
		    
			BasicDBList supportDBObject = (BasicDBList) MongoDB.getCollection(InstanceConstants.CONTAINER_COLL_NAME, Container.class, String.class).group(keysSupport, query, test,"function ( curr, result ) { }");
			Iterator itr = supportDBObject.iterator();
		    
			while(itr.hasNext()) {
		         BasicDBObject element = (BasicDBObject) itr.next();
		         String supportCode  = (String)element.get("support.supportCode");
		         LocationOnContainerSupport cs = new LocationOnContainerSupport();
		         cs.supportCode = (String)element.get("support.supportCode");
		         cs.categoryCode = (String)element.get("support.categoryCode");
		         containerSupports.add(cs);
		    }
		
			return ok(Json.toJson(new DatatableResponse<LocationOnContainerSupport>(containerSupports, containerSupports.size())));
		}else if(containersSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("support", 1);
			MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query);
			List<Container> containers = results.toList();
			List<LocationOnContainerSupport> containerSupports = new ArrayList<LocationOnContainerSupport>();
			for(Container c: containers){
				if(!containerSupports.contains(c.support)){
					containerSupports.add(c.support);
				}
			}

			List<String> ls = new ArrayList<String>();
			for(LocationOnContainerSupport p: containerSupports){
				if(!containerSupports.contains(p)){
					ls.add(p.supportCode);
				}
			}

			return ok(Json.toJson(ls));
		}else{
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("support", 1);
			MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query);
			List<Container> containers = results.toList();
			List<LocationOnContainerSupport> containerSupports = new ArrayList<LocationOnContainerSupport>();
			for(Container c: containers){
				if(!containerSupports.contains(c.support)){
					containerSupports.add(c.support);
				}
			}
			
			return ok(Json.toJson(containerSupports));
		}
	}
	
	
	/**
	 * Construct the container query
	 * @param containersSearch
	 * @return
	 * @throws DAOException 
	 */
	public static DBQuery.Query getQuery(ContainersSearchForm containersSearch) throws DAOException {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		if(containersSearch.projectCodes != null){
			queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCodes));
		}else if(containersSearch.projectCode != null){
			queryElts.add(DBQuery.is("projectCodes", containersSearch.projectCode));
		}

		if(StringUtils.isNotEmpty(containersSearch.stateCode)){
			queryElts.add(DBQuery.is("stateCode", containersSearch.stateCode));
		}

		if(StringUtils.isNotEmpty(containersSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", containersSearch.categoryCode));
		}

		if(containersSearch.sampleCodes != null){
			queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCodes));
		}else if(containersSearch.sampleCode != null){
			queryElts.add(DBQuery.is("sampleCodes", containersSearch.sampleCode));
		}

		if(StringUtils.isNotEmpty(containersSearch.supportCode)){
			queryElts.add(DBQuery.is("support.supportCode", containersSearch.supportCode));
		}
		
		if(StringUtils.isNotEmpty(containersSearch.containerSupportCategory)){
			queryElts.add(DBQuery.is("support.categoryCode", containersSearch.containerSupportCategory));
		}else if(!StringUtils.isEmpty(containersSearch.experimentTypeCode)){
			List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findByExperimentTypeCode(containersSearch.experimentTypeCode);
			List<String> cs = new ArrayList<String>();
			for(ContainerSupportCategory c:containerSupportCategories){
				cs.add(c.code);
			}
			if(cs.size() > 0){
				queryElts.add(DBQuery.in("support.categoryCode", cs));
			}
		}
		
		if(StringUtils.isNotEmpty(containersSearch.processTypeCode) && StringUtils.isEmpty(containersSearch.experimentTypeCode)){
			List<String> listePrevious = Spring.getBeanOfType(ExperimentTypeDAO.class).findVoidProcessExperimentTypeCode(containersSearch.processTypeCode);
			if(null != listePrevious && listePrevious.size() > 0){
				queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", listePrevious),DBQuery.notExists("fromExperimentTypeCodes"),DBQuery.size("fromExperimentTypeCodes", 0)));
			}	    		    	
		}
		
		if(containersSearch.fromExperimentTypeCodes != null){
			queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", containersSearch.fromExperimentTypeCodes)));
		}

		if(StringUtils.isNotEmpty(containersSearch.experimentTypeCode)){
			try {
				List<ExperimentType> previous = Spring.getBeanOfType(ExperimentTypeDAO.class).findPreviousExperimentTypeForAnExperimentTypeCode(containersSearch.experimentTypeCode);
				List<String> previousString = new ArrayList<String>();
				for(ExperimentType e:previous){
					previousString.add(e.code);
				}

				if(previousString.size() != 0){//If there is no previous, we take all the containers Available
					queryElts.add(DBQuery.in("fromExperimentTypeCodes", previousString));
				}
				
				for(String s:previousString){
					Logger.info("Previous: "+s);
				}

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