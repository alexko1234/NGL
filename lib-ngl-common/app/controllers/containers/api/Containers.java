package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.modules.mongojack.MongoDB;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.NGLControllerHelper;
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

		return notFound();
	}

	public static Result head(String code) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, code)){			
			return ok();					
		}else{
			return notFound();
		}	
	}

	public static Result updateBatch(){
		List<Form<ContainerBatchElement>> filledForms =  getFilledFormList(batchElementForm, ContainerBatchElement.class);

		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());

		for(Form<ContainerBatchElement> filledForm: filledForms){
			ContainerBatchElement element = filledForm.get();
			Container container = null;
			if(null!=element.data){
				container = findContainer(element.data.code);
			}
			if(null != container){
				State state = element.data.state;
				state.date = new Date();
				state.user = getCurrentUser();
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				Workflows.setContainerState(container.code,null, state, ctxVal);
				if (!ctxVal.hasErrors()) {
					response.add(new DatatableBatchResponseElement(OK,  findContainer(element.data.code), element.index));
				}else {
					response.add(new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(), element.index));
				}
			}else {
				response.add(new DatatableBatchResponseElement(NOT_FOUND, element.index));
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
		//Form<ContainersSearchForm> containerFilledForm = filledFormQueryString(containerForm,ContainersSearchForm.class);
		ContainersSearchForm containersSearch = filledFormQueryString(ContainersSearchForm.class);
		DBQuery.Query query = getQuery(containersSearch);
		if(query != null){
			if(containersSearch.datatable){
				MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query);
				List<Container> containers = results.toList();

				return ok(Json.toJson(new DatatableResponse<Container>(containers, results.count())));
			}else if(containersSearch.count){
				BasicDBObject keys = new BasicDBObject();
				keys.put("_id", 0);//Don't need the _id field
				keys.put("code", 1);
				MongoDBResult<Container> results = mongoDBFinder(InstanceConstants.CONTAINER_COLL_NAME, containersSearch, Container.class, query, keys);							
				int count = results.count();
				Map<String, Integer> m = new HashMap<String, Integer>(1);
				m.put("result", count);
				return ok(Json.toJson(m));
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
		return badRequest();
	}

	public static Result list_supports() throws DAOException{
		//Form<ContainersSearchForm> containerFilledForm = filledFormQueryString(containerForm,ContainersSearchForm.class);
		ContainersSearchForm containersSearch = filledFormQueryString(ContainersSearchForm.class);

		DBQuery.Query query = getQuery(containersSearch);
		if(query != null){
			if(containersSearch.datatable){
				List<LocationOnContainerSupport> containerSupports = new ArrayList<LocationOnContainerSupport>();

				BasicDBObject keysSupport = new BasicDBObject();
				BasicDBObject test = new BasicDBObject();
				keysSupport.put("support.code",true);
				keysSupport.put("support.categoryCode",true);

				BasicDBList supportDBObject = (BasicDBList) MongoDB.getCollection(InstanceConstants.CONTAINER_COLL_NAME, Container.class, String.class).group(keysSupport, test, test,"function ( curr, result ) { }");
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
		return ok();
	}


	/**
	 * Construct the container query
	 * @param containersSearch
	 * @return
	 * @throws DAOException 
	 */
	public static DBQuery.Query getQuery(ContainersSearchForm containersSearch) throws DAOException{
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = DBQuery.empty();

		List<String> processCodes = new ArrayList<String>();
		if(containersSearch.properties.size() > 0){
			List<DBQuery.Query> listProcessQuery = NGLControllerHelper.generateQueriesForProperties(containersSearch.properties, Level.CODE.Process, "properties");
			Query processQuery = DBQuery.and(listProcessQuery.toArray(new DBQuery.Query[queryElts.size()]));

			List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, processQuery).toList();
			for(Process p : processes){
				processCodes.add(p.code);
			}
		}

		if(CollectionUtils.isNotEmpty(processCodes)){
			queryElts.add(DBQuery.in("inputProcessCodes", processCodes));
		}

		if(CollectionUtils.isNotEmpty(containersSearch.projectCodes)){
			queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCodes));
		}else if(StringUtils.isNotBlank(containersSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCode));
		}

		if(StringUtils.isNotBlank(containersSearch.code)){
			queryElts.add(DBQuery.is("code", containersSearch.code));
		}

		if(CollectionUtils.isNotEmpty(containersSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", containersSearch.stateCodes));
		}else if(StringUtils.isNotBlank(containersSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", containersSearch.stateCode));
		}

		if(StringUtils.isNotBlank(containersSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", containersSearch.categoryCode));
		}

		if(CollectionUtils.isNotEmpty(containersSearch.sampleCodes)){
			queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCodes));
		}else if(StringUtils.isNotBlank(containersSearch.sampleCode)){
			queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCode));
		}

		if(StringUtils.isNotBlank(containersSearch.supportCode)){
			queryElts.add(DBQuery.regex("support.code", Pattern.compile(containersSearch.supportCode)));
		}

		if(StringUtils.isNotBlank(containersSearch.containerSupportCategory)){
			queryElts.add(DBQuery.is("support.categoryCode", containersSearch.containerSupportCategory));
		}else if(StringUtils.isNotBlank(containersSearch.nextExperimentTypeCode)){
			List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findByExperimentTypeCode(containersSearch.nextExperimentTypeCode);
			List<String> cs = new ArrayList<String>();
			for(ContainerSupportCategory c:containerSupportCategories){
				cs.add(c.code);
			}
			if(cs.size() > 0){
				queryElts.add(DBQuery.in("support.categoryCode", cs));
			}
		}

		List<String> listePrevious = new ArrayList<String>();
		if(StringUtils.isNotBlank(containersSearch.nextProcessTypeCode)){					
			listePrevious = ExperimentType.find.findVoidProcessExperimentTypeCode(containersSearch.nextProcessTypeCode);
			if(CollectionUtils.isNotEmpty(listePrevious)){			
				ProcessType processType = ProcessType.find.findByCode(containersSearch.nextProcessTypeCode);
				List<ExperimentType> experimentTypes = new ArrayList<ExperimentType>();
				if(processType != null){
					experimentTypes = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCode(processType.firstExperimentType.code);
				}else{
					Logger.error("NGL-SQ bad nextProcessTypeCode: "+containersSearch.nextProcessTypeCode);
					return null;
				}
				for(ExperimentType e:experimentTypes){
					Logger.info(e.code);
					listePrevious.add(e.code);
				}			
				queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", listePrevious),DBQuery.notExists("fromExperimentTypeCodes"),DBQuery.size("fromExperimentTypeCodes", 0)));
			}else{
				throw new RuntimeException("nextProcessTypeCode = "+ containersSearch.nextProcessTypeCode +" does not exist!");
			}
		}

		if(StringUtils.isNotBlank(containersSearch.nextExperimentTypeCode)){
			List<ExperimentType> previous = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCode(containersSearch.nextExperimentTypeCode);
			if(CollectionUtils.isNotEmpty(previous)){
			for(ExperimentType e:previous){
				listePrevious.add(e.code);
			}

			if(CollectionUtils.isNotEmpty(listePrevious)){
				queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", listePrevious)));
			}
			}else{
				//throw new RuntimeException("nextExperimentTypeCode = "+ containersSearch.nextExperimentTypeCode +" does not exist!");
			}
			queryElts.add(DBQuery.nor(DBQuery.notExists("inputProcessCodes"),DBQuery.size("inputProcessCodes", 0)));
		}

		if(CollectionUtils.isNotEmpty(containersSearch.fromExperimentTypeCodes)){
			queryElts.add(DBQuery.or(DBQuery.in("fromExperimentTypeCodes", containersSearch.fromExperimentTypeCodes)));
		}

		if(null != containersSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", containersSearch.fromDate));
		}

		if(null != containersSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(containersSearch.toDate, 1))));
		}

		if(CollectionUtils.isNotEmpty(containersSearch.valuations)){
			queryElts.add(DBQuery.or(DBQuery.in("valuation.valid", containersSearch.valuations)));
		}

		if(StringUtils.isNotBlank(containersSearch.column)){
			queryElts.add(DBQuery.is("support.column", containersSearch.column));
		}

		if(StringUtils.isNotBlank(containersSearch.line)){
			queryElts.add(DBQuery.is("support.line", containersSearch.line));
		}

		if(StringUtils.isNotBlank(containersSearch.processTypeCode)){   
			queryElts.add(DBQuery.is("processTypeCode", containersSearch.processTypeCode));
		}

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}


		return query;
	}
}