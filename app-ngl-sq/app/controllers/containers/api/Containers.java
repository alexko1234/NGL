package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.modules.mongodb.jackson.MongoDB;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Containers extends CommonController {
	
	final static Form<Container> containersForm = form(Container.class);
	final static Form<ContainersSearchForm> containerForm = form(ContainersSearchForm.class);
	final static Form<ContainerBatchElement> batchElementForm = form(ContainerBatchElement.class);

	public static Result get(String code){
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, code);
		if(container != null){
			return ok(Json.toJson(container));
		}

		return badRequest();
	}

	public static Result updateBatch(){
		List<Form<ContainerBatchElement>> filledForms =  getFilledFormList(batchElementForm, ContainerBatchElement.class);
		
		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		
		for(Form<ContainerBatchElement> filledForm: filledForms){
			ContainerBatchElement element = filledForm.get();
			Container container = findContainer(element.data.code);
			if(null != container){
				State state = element.data.state;
				state.date = new Date();
				state.user = getCurrentUser();
				ContextValidation ctxVal = new ContextValidation(filledForm.errors());
				Workflows.setContainerState(container.code, state, ctxVal);
				if (!ctxVal.hasErrors()) {
					response.add(new DatatableBatchResponseElement(OK,  findContainer(element.data.code), element.index));
				}else {
					response.add(new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(), element.index));
				}
			}else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, element.index));
			}
			
		}		
		return ok(Json.toJson(response));
	}
	
	private static Container findContainer(String containerCode){
		return  MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code",containerCode));
	}
	
	public static Result update(String containerCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", containerCode))){
			Form<Container> containerFilledForm = getFilledForm(containersForm,Container.class);
			
			if(!containerFilledForm.hasErrors()){
				Container container = containerFilledForm.get();
				if(container.code.equals(containerCode)){
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);
					return ok(Json.toJson(container));
				}
			}
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
			keysSupport.put("support.code",true);
			keysSupport.put("support.categoryCode",true);
		    
			BasicDBList supportDBObject = (BasicDBList) MongoDB.getCollection(InstanceConstants.CONTAINER_COLL_NAME, Container.class, String.class).group(keysSupport, query, test,"function ( curr, result ) { }");
			Iterator itr = supportDBObject.iterator();
		    
			while(itr.hasNext()) {
		         BasicDBObject element = (BasicDBObject) itr.next();
		         String supportCode  = (String)element.get("support.code");
		         LocationOnContainerSupport cs = new LocationOnContainerSupport();
		         cs.code = (String)element.get("support.code");
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
					ls.add(p.code);
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
			queryElts.add(DBQuery.is("state.code", containersSearch.stateCode));
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
			queryElts.add(DBQuery.is("support.code", containersSearch.supportCode));
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
			List<String> listePrevious = ExperimentType.find.findVoidProcessExperimentTypeCode(containersSearch.processTypeCode);
			if(null != listePrevious && listePrevious.size() > 0){
				queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", listePrevious),DBQuery.notExists("fromExperimentTypeCodes"),DBQuery.size("fromExperimentTypeCodes", 0)));
			}	    		    	
		}
		
		if(containersSearch.fromExperimentTypeCodes != null){
			queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", containersSearch.fromExperimentTypeCodes)));
		}

		if(StringUtils.isNotEmpty(containersSearch.experimentTypeCode)){
			try {
				List<ExperimentType> previous = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCode(containersSearch.experimentTypeCode);
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