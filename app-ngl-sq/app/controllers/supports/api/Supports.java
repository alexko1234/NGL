package controllers.supports.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Support;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.containers.api.Containers;
import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Supports extends CommonController {

	final static Form<SupportsSearchForm> supportForm = form(SupportsSearchForm.class);
	
	public static Result get(String code){
		Support support = MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, Support.class, code);
		if(support != null){
			return ok(Json.toJson(support));
		}

		return badRequest();
	}
	
	public static Result list() throws DAOException{
		Form<SupportsSearchForm> supportFilledForm = filledFormQueryString(supportForm,SupportsSearchForm.class);
		SupportsSearchForm supportsSearch = supportFilledForm.get();
		
		DBQuery.Query query = getQuery(supportsSearch);
		if(supportsSearch.datatable){
			MongoDBResult<Support> results =  mongoDBFinder(InstanceConstants.SUPPORT_COLL_NAME, supportsSearch, Support.class, query); 
			List<Support> supports = results.toList();
			
			return ok(Json.toJson(new DatatableResponse<Support>(supports, results.count())));
		}else if(supportsSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			
			MongoDBResult<Support> results =  mongoDBFinder(InstanceConstants.SUPPORT_COLL_NAME, supportsSearch, Support.class, query, keys); 
			List<Support> supports = results.toList();
			
			List<ListObject> los = new ArrayList<ListObject>();
			for(Support s: supports){
				los.add(new ListObject(s.code, s.code));
			}

			return ok(Json.toJson(los));
		}else{
			MongoDBResult<Support> results =  mongoDBFinder(InstanceConstants.SUPPORT_COLL_NAME, supportsSearch, Support.class, query); 
			List<Support> supports = results.toList();
			
			return ok(Json.toJson(supports));
		}
	}
	
	/**
	 * Construct the support query
	 * @param supportsSearch
	 * @return
	 * @throws DAOException 
	 */
	private static DBQuery.Query getQuery(SupportsSearchForm supportsSearch) throws DAOException {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		
		if(StringUtils.isNotEmpty(supportsSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", supportsSearch.categoryCode));
		}else if(!StringUtils.isEmpty(supportsSearch.experimentTypeCode)){
			List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findByExperimentTypeCode(supportsSearch.experimentTypeCode);
			List<String> cs = new ArrayList<String>();
			for(ContainerSupportCategory c:containerSupportCategories){
				cs.add(c.code);
			}
			queryElts.add(DBQuery.in("categoryCode", cs));
		}
		
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("support", 1);
		
		ContainersSearchForm cs = new ContainersSearchForm();
		cs.experimentTypeCode = supportsSearch.experimentTypeCode;
		cs.processTypeCode = supportsSearch.processTypeCode;
		
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, Containers.getQuery(cs), keys).toList(); 
		List<String> supports  =new ArrayList<String>();
		for(Container c: containers){
			supports.add(c.support.supportCode);
		}
		
		if(supports.size() > 0 ){
			queryElts.add(DBQuery.in("code", supports));
		}
		
		if(supportsSearch.projectCodes != null){
			queryElts.add(DBQuery.in("projectCodes", supportsSearch.projectCodes));
		}
		
		if(null != supportsSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", supportsSearch.fromDate));
		}
		
		if(null != supportsSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", supportsSearch.toDate));
		}
		
		if(null != supportsSearch.users){
			queryElts.add(DBQuery.in("traceInformation.createUser", supportsSearch.users));
		}
		
		if(supportsSearch.sampleCodes != null){
			queryElts.add(DBQuery.in("sampleCodes", supportsSearch.sampleCodes));
		}
		
		if(StringUtils.isNotEmpty(supportsSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", supportsSearch.stateCode));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
