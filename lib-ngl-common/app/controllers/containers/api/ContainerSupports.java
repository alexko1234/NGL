package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class ContainerSupports extends CommonController {

	final static Form<ContainerSupportsSearchForm> supportForm = form(ContainerSupportsSearchForm.class);
	final static Form<ContainerSupportsUpdateForm> containerSupportUpdateForm = form(ContainerSupportsUpdateForm.class);

	public static Result get(String code){
		ContainerSupport support = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, code);
		if(support != null){
			return ok(Json.toJson(support));
		}

		return notFound();
	}

	public static Result head(String code) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, code)){			
			return ok();					
		}else{
			return notFound();
		}	
	}

	public static Result list() throws DAOException{		
		ContainerSupportsSearchForm supportsSearch = filledFormQueryString(ContainerSupportsSearchForm.class);

		DBQuery.Query query = getQuery(supportsSearch);
		if(query != null){
			if(supportsSearch.datatable){
				MongoDBResult<ContainerSupport> results =  mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query); 
				List<ContainerSupport> supports = results.toList();

				return ok(Json.toJson(new DatatableResponse<ContainerSupport>(supports, results.count())));
			}else if(supportsSearch.list){
				BasicDBObject keys = new BasicDBObject();
				keys.put("_id", 0);//Don't need the _id field
				keys.put("code", 1);

				MongoDBResult<ContainerSupport> results =  mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query, keys); 
				List<ContainerSupport> supports = results.toList();

				List<ListObject> los = new ArrayList<ListObject>();
				for(ContainerSupport s: supports){
					los.add(new ListObject(s.code, s.code));
				}

				return ok(Json.toJson(los));
			}else{
				MongoDBResult<ContainerSupport> results =  mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query); 
				List<ContainerSupport> supports = results.toList();

				return ok(Json.toJson(supports));
			}
		}
		return ok("{}");
	}

	public static Result updateBatch(){
		return ok();
	}
	
	public static Result updateStateCode(String code){
		Form<ContainerSupportsUpdateForm> containerSupportUpdateFilledForm = getFilledForm(containerSupportUpdateForm, ContainerSupportsUpdateForm.class);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(),containerSupportUpdateFilledForm.errors());
		if(!containerSupportUpdateFilledForm.hasErrors()){
			ContainerSupportsUpdateForm containerSupportUpdateForm = containerSupportUpdateFilledForm.get();
			State state = new State();
			state.code = containerSupportUpdateForm.stateCode;
			state.user = getCurrentUser();
			Workflows.setContainerSupportState(code, state, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok();
			}
		}
		return badRequest(containerSupportUpdateFilledForm.errorsAsJson());
		}
		

	/**
	 * Construct the support query
	 * @param supportsSearch
	 * @return
	 * @throws DAOException 
	 */
	private static DBQuery.Query getQuery(ContainerSupportsSearchForm supportsSearch) throws DAOException {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		queryElts.add(DBQuery.exists("_id"));

		if(StringUtils.isNotBlank(supportsSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", supportsSearch.categoryCode));
		}
		
		if(StringUtils.isNotBlank(supportsSearch.containerSupportCategory)){
			queryElts.add(DBQuery.is("categoryCode", supportsSearch.containerSupportCategory));
		}
		
		if(CollectionUtils.isNotEmpty(supportsSearch.containerSupportCategories)){
			queryElts.add(DBQuery.in("categoryCode", supportsSearch.containerSupportCategories));
		}

		if(CollectionUtils.isNotEmpty(supportsSearch.fromExperimentTypeCodes)){
			queryElts.add(DBQuery.in("fromExperimentTypeCodes", supportsSearch.fromExperimentTypeCodes));
		}		

		//These fields are not in the ContainerSupport collection then we use the Container collection
		if(StringUtils.isNotBlank(supportsSearch.nextExperimentTypeCode) || StringUtils.isNotBlank(supportsSearch.processTypeCode)){


			/*Don't need anymore 09/01/2015
			//If the categoryCode is null or empty, we use the ContainerSupportCategory data table to enhance the query
			if(StringUtils.isNotEmpty(supportsSearch.experimentTypeCode) && StringUtils.isEmpty(supportsSearch.categoryCode)){
				List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findByExperimentTypeCode(supportsSearch.experimentTypeCode);
				List<String> ls = new ArrayList<String>();
				for(ContainerSupportCategory c:containerSupportCategories){
					ls.add(c.code);
				}
				if(ls.size() > 0){
					queryElts.add(DBQuery.in("categoryCode", ls));
				}
			}
			 */

			//Using the Container collection for reaching container support
			ContainersSearchForm cs = new ContainersSearchForm();
			cs.nextExperimentTypeCode = supportsSearch.nextExperimentTypeCode;
			cs.processTypeCode = supportsSearch.processTypeCode;		
			cs.properties = supportsSearch.properties;	
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("support", 1);
			Query queryContainer =Containers.getQuery(cs);
			if(queryContainer != null){
				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, queryContainer, keys).toList();
				Logger.debug("Containers "+containers.size());
				List<String> supports  =new ArrayList<String>();
				for(Container c: containers){
					supports.add(c.support.code);
				}

				if(StringUtils.isNotBlank(cs.nextExperimentTypeCode) || StringUtils.isNotBlank(cs.processTypeCode)){
					queryElts.add(DBQuery.in("code", supports));
				}
			}else{
				return null;
			}
		}


		if(CollectionUtils.isNotEmpty(supportsSearch.valuations)){
			queryElts.add(DBQuery.or(DBQuery.in("valuation.valid", supportsSearch.valuations)));
		}

		if(StringUtils.isNotBlank(supportsSearch.stateCode)){
			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCode));
		}
		
		if(CollectionUtils.isNotEmpty(supportsSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCodes));
		}

		if(StringUtils.isNotBlank(supportsSearch.code)){
			queryElts.add(DBQuery.is("code", supportsSearch.code));
		}

		if(CollectionUtils.isNotEmpty(supportsSearch.projectCodes)){
			queryElts.add(DBQuery.in("projectCodes", supportsSearch.projectCodes));
		}

		if(null != supportsSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", supportsSearch.fromDate));
		}

		if(null != supportsSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", supportsSearch.toDate));
		}

		if(StringUtils.isNotBlank(supportsSearch.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(supportsSearch.codeRegex)));
		}
		
		if(StringUtils.isNotBlank(supportsSearch.createUser)){   
			queryElts.add(DBQuery.is("traceInformation.createUser", supportsSearch.createUser));
		}

		if(CollectionUtils.isNotEmpty(supportsSearch.users)){
			queryElts.add(DBQuery.in("traceInformation.createUser", supportsSearch.users));
		}

		if(CollectionUtils.isNotEmpty(supportsSearch.sampleCodes)){
			queryElts.add(DBQuery.in("sampleCodes", supportsSearch.sampleCodes));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}

