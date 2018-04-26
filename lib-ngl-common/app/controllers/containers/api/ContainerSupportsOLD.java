package controllers.containers.api;


// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
// import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

// import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
// import controllers.history.UserHistory;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.StorageHistory;
// import models.laboratory.run.instance.ReadSet;
// import models.laboratory.run.instance.Run;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
// import play.Logger;
// import play.api.modules.spring.Spring;
import play.data.Form;
import play.i18n.Lang;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
// import play.mvc.With;
// import scala.collection.generic.BitOperations.Int;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
// import validation.run.instance.ReadSetValidationHelper;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableResponse;
import workflows.container.ContSupportWorkflows;

//@With({fr.cea.ig.authentication.Authenticate.class, UserHistory.class})
// public class ContainerSupports extends CommonController {
public class ContainerSupportsOLD extends DocumentController<ContainerSupport> {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainerSupportsOLD.class);
	
	private final static List<String> authorizedUpdateFields = Arrays.asList("storageCode"); //liste des champs qui peuvent etre mis a jour

	// private final Form<ContainerSupportsSearchForm>  supportForm; // = form(ContainerSupportsSearchForm.class);
	// private final Form<ContainerSupportsUpdateForm>  containerSupportUpdateForm; // = form(ContainerSupportsUpdateForm.class);
	private final Form<ContainerSupport>             containerSupportForm; // = form(ContainerSupport.class);
	private final Form<QueryFieldsForm>              updateForm; // = form(QueryFieldsForm.class); //dans cas "update" il peut y avoir une query string
	private final Form<ContainerSupportBatchElement> batchElementForm; // = form(ContainerSupportBatchElement.class);
	private final Form<State>                        stateForm; // = form(State.class);
	// private final static ContSupportWorkflows workflows = Spring.get BeanOfType(ContSupportWorkflows.class);
	private final  ContSupportWorkflows              workflows;
	
	@Inject
	public ContainerSupportsOLD(NGLContext ctx,  ContSupportWorkflows workflows) {
		super(ctx, InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class);
		// supportForm                = ctx.form(ContainerSupportsSearchForm.class);
		// containerSupportUpdateForm = ctx.form(ContainerSupportsUpdateForm.class);
		containerSupportForm       = ctx.form(ContainerSupport.class);
		updateForm                 = ctx.form(QueryFieldsForm.class);
		batchElementForm           = ctx.form(ContainerSupportBatchElement.class);
		stateForm                  = ctx.form(State.class);
		this.workflows             = workflows;
	}
	
	/*@Permission(value={"reading"})
	public static Result get(String code){
		ContainerSupport support = getSupport(code);
		if (support != null) {
			return ok(Json.toJson(support));
		}
		return notFound();
	}*/
	@Permission(value={"reading"})
	public Result get(String code) { return super.get(code); } 
	
	/*@Permission(value={"reading"})
	public static Result head(String code) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, code)){			
			return ok();					
		}else{
			return notFound();
		}	
	}*/
	
	@Permission(value={"reading"})
	public Result head(String code) { return super.head(code); }
	
	@Permission(value={"reading"})
	public Result list() throws DAOException {		
		ContainerSupportsSearchForm supportsSearch = filledFormQueryString(ContainerSupportsSearchForm.class);

		DBQuery.Query query = getQuery(supportsSearch);
		BasicDBObject keys = getKeys(supportsSearch);
		
		if (query != null) {
			if (supportsSearch.datatable) {
				MongoDBResult<ContainerSupport> results = // mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query, keys); 
						mongoDBFinder(supportsSearch, query, keys); 
				List<ContainerSupport> supports = results.toList();

				return ok(Json.toJson(new DatatableResponse<ContainerSupport>(supports, results.count())));
			} else if(supportsSearch.list) {
				keys.put("_id", 0);//Don't need the _id field
				keys.put("code", 1);

				MongoDBResult<ContainerSupport> results = // mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query, keys); 
						mongoDBFinder(supportsSearch, query, keys); 
				List<ContainerSupport> supports = results.toList();

				List<ListObject> los = new ArrayList<ListObject>();
				for (ContainerSupport s: supports) {
					los.add(new ListObject(s.code, s.code));
				}
				return ok(Json.toJson(los));
			} else {
				MongoDBResult<ContainerSupport> results = // mongoDBFinder(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, supportsSearch, ContainerSupport.class, query, keys);
						mongoDBFinder(supportsSearch, query, keys); 
				List<ContainerSupport> supports = results.toList();

				return ok(Json.toJson(supports));
			}
		}
		return ok("{}");
	}

	@Permission(value={"writing"})	
	public Result updateState(String code){
		ContainerSupport support = getSupport(code);
		if(support == null){
			return badRequest();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "controllers");
		ctxVal.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_STATE, Boolean.TRUE);
		workflows.setState(ctxVal, support, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getSupport(code)));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			// return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}

	private ContainerSupport getSupport(String code) {
		// return MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, code);
		return getObject(code);
	}
		
	@Permission(value={"writing"})
	public Result updateStateBatch(){
		List<Form<ContainerSupportBatchElement>> filledForms =  getFilledFormList(batchElementForm, ContainerSupportBatchElement.class);
		
		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
			.map(filledForm -> {
				ContainerSupportBatchElement element = filledForm.get();
				ContainerSupport support = getSupport(element.data.code);
				if (null != support) {
					State state = element.data.state;
					state.date = new Date();
					state.user = user;
					ContextValidation ctxVal = new ContextValidation(user, filledForm.errors());
					ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "controllers");
					ctxVal.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_STATE, Boolean.TRUE);
					workflows.setState(ctxVal, support, state);
					if (!ctxVal.hasErrors()) {
						return new DatatableBatchResponseElement(OK,  getSupport(support.code), element.index);
					} else {
						return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(lang), element.index);
					}
				} else {
					return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
				}
			}).collect(Collectors.toList());
		return ok(Json.toJson(response));
	}
	
	// 26/05/2015 fds ++++GA NGL-825; storageCode must be updatable
	@Permission(value={"writing"})
    public Result update(String code) {
		ContainerSupport dbSupport = getSupport(code);
		//vérifier que le code support a été trouvé dans la base
		if (dbSupport == null) {
			return badRequest("Container support with code " + code + " does not exist");
		}
		//recuperer les eventuels parametres de la query string de l'url
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);	
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		
		//recuperer le corps de la requete HTTP
		Form<ContainerSupport> filledForm = getFilledForm(containerSupportForm, ContainerSupport.class);
		ContainerSupport formSupport = filledForm.get();

		if (queryFieldsForm.fields == null) {
			// il n'a pas de query string==> mettre a jour tout l'object
			//     garder ce mode pour des actions en mode non graphique, pour du support...
			if (dbSupport.code.equals(code)) {
				// on a bien récupéré ce qu'on a demandé....
				if (null != formSupport.traceInformation) {
					formSupport.traceInformation.setTraceInformation(getCurrentUser());
				} else {
					logger.error("traceInformation is null for Container support " + code);	
				}
				//mise a jour de TOUT l'objet
				MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, formSupport);
				return ok(Json.toJson(formSupport));			
			} else {
				return badRequest("container code are not the same");
			}
		} else { 
			// il y a une query string ==> mettre a jour les champs dont le nom est dans la query string:   ?fields=XXXX&fields=YYY
			if (dbSupport.code.equals(code)) {
				// on a bien récupéré ce qu'on a demandé....
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
				ctxVal.setUpdateMode();
				validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);	
				// verifier si les champs de la query string font partie des champs modifiables
				validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm); 
				// if (!filledForm.hasErrors()) {
				if (!ctxVal.hasErrors()) {
					if (null != dbSupport.traceInformation) {
						dbSupport.traceInformation.setTraceInformation(getCurrentUser());
					} else {
						logger.error("traceInformation is null for Container support " + code);	
					}
					//-1- mise a jour DES champs de l'objet (getBuilder construit la requete MongoDB adequate) et de traceInformation
					MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, 
							DBQuery.and(DBQuery.is("code", code)), 
							getBuilder(formSupport, queryFieldsForm.fields, ContainerSupport.class).set("traceInformation", dbSupport.traceInformation));
					//-2- cas particulier pour storageCode => il est aussi present dans tous les containers du support 
					//    si on le trouve dans la query string mettre a jour les containers avec la valeur  formSupport.storageCode	
					if (queryFieldsForm.fields.contains("storageCode")) {
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, ContainerSupport.class, 
								DBQuery.and(DBQuery.is("support.code", code)), 
								DBUpdate.set("support.storageCode", formSupport.storageCode).set("traceInformation", dbSupport.traceInformation));
						updateStorages(dbSupport, formSupport);
					}
					return ok(Json.toJson(getSupport(code)));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					// return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}		
			} else {
				return badRequest("container code are not the same");
			}
		}
	}

	private void updateStorages(ContainerSupport dbSupport,
			ContainerSupport formSupport) {
		if (dbSupport.storages == null) {
			dbSupport.storages = new ArrayList<StorageHistory>();
			if (null != dbSupport.storageCode) {
				StorageHistory sh = getStorageHistory(dbSupport.storageCode, dbSupport.storages.size());
				dbSupport.storages.add(sh);
			}
		}
		StorageHistory sh = getStorageHistory(formSupport.storageCode, dbSupport.storages.size());
		dbSupport.storages.add(sh);
		MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, 
				DBQuery.and(DBQuery.is("code", dbSupport.code)), 
				DBUpdate.set("storages", dbSupport.storages));
	}

	private StorageHistory getStorageHistory(String storageCode, Integer index) {
		StorageHistory sh = new StorageHistory();
		sh.code  = storageCode;
		sh.date  = new Date();
		sh.user  = getCurrentUser();
		sh.index = index;
		return sh;
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
		if (StringUtils.isNotBlank(supportsSearch.categoryCode)) {
			queryElts.add(DBQuery.is("categoryCode", supportsSearch.categoryCode));
		}
		if (StringUtils.isNotBlank(supportsSearch.containerSupportCategory)) {
			queryElts.add(DBQuery.is("categoryCode", supportsSearch.containerSupportCategory));
		}
		if (CollectionUtils.isNotEmpty(supportsSearch.containerSupportCategories)) {
			queryElts.add(DBQuery.in("categoryCode", supportsSearch.containerSupportCategories));
		}
		if (CollectionUtils.isNotEmpty(supportsSearch.fromTransformationTypeCodes)) {
			if (supportsSearch.fromTransformationTypeCodes.contains("none")) {
				queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
				,DBQuery.in("fromTransformationTypeCodes", supportsSearch.fromTransformationTypeCodes)));
			} else {
				queryElts.add(DBQuery.in("fromTransformationTypeCodes", supportsSearch.fromTransformationTypeCodes));
			}			
		}		
		//These fields are not in the ContainerSupport collection then we use the Container collection
		
		//TODO GA allways used ?????
		if (StringUtils.isNotBlank(supportsSearch.nextExperimentTypeCode) || StringUtils.isNotBlank(supportsSearch.processTypeCode)) {
			logger.error("Allready used nextExperimentTypeCode in search container support. Please find where in java code");

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
			cs.processProperties = supportsSearch.properties;	
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("support", 1);
			Query queryContainer =ContainersOLD.getQuery(cs);
			if (queryContainer != null) {
				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, queryContainer, keys).toList();
				logger.debug("Containers " + containers.size());
				List<String> supports  =new ArrayList<String>();
				for(Container c: containers){
					supports.add(c.support.code);
				}
				if (StringUtils.isNotBlank(cs.nextExperimentTypeCode) || StringUtils.isNotBlank(cs.processTypeCode)) {
					queryElts.add(DBQuery.in("code", supports));
				}
			} else {
				return null;
			}
		}

		/*23/05/2016  NGL-825 FDS : this criteria is meaningless for supports with multiple containers ( plates..)
		if(CollectionUtils.isNotEmpty(supportsSearch.valuations)){
			queryElts.add(DBQuery.or(DBQuery.in("valuation.valid", supportsSearch.valuations)));
		}
		*/
		
		/* 23/05/2016 FDS NGL-825: add search by storageCode */
		if (StringUtils.isNotBlank(supportsSearch.storageCode)) {
			queryElts.add(DBQuery.in("storageCode", supportsSearch.storageCode));
		} else if(StringUtils.isNotBlank(supportsSearch.storageCodeRegex)) {
			queryElts.add(DBQuery.regex("storageCode", Pattern.compile(supportsSearch.storageCodeRegex)));
		}

		if (StringUtils.isNotBlank(supportsSearch.stateCode)) {
			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCode));
		}
		
		if (CollectionUtils.isNotEmpty(supportsSearch.stateCodes)) {
			queryElts.add(DBQuery.in("state.code", supportsSearch.stateCodes));
		}

		if (CollectionUtils.isNotEmpty(supportsSearch.codes)) {
			queryElts.add(DBQuery.in("code", supportsSearch.codes));
		} else if(StringUtils.isNotBlank(supportsSearch.code)) {
			queryElts.add(DBQuery.is("code", supportsSearch.code));
		} else if(StringUtils.isNotBlank(supportsSearch.codeRegex)) {
			queryElts.add(DBQuery.regex("code", Pattern.compile(supportsSearch.codeRegex)));
		}
		
		if (CollectionUtils.isNotEmpty(supportsSearch.projectCodes)) {
			queryElts.add(DBQuery.in("projectCodes", supportsSearch.projectCodes));
		}

		if (null != supportsSearch.fromDate) {
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", supportsSearch.fromDate));
		}

		if (null != supportsSearch.toDate) {
			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(supportsSearch.toDate, 1))));
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
	
	@Permission(value={"writing"})
	public Result saveCode(Integer numberOfCode) {
		List<String> codes = new ArrayList<String>(numberOfCode);
		IntStream.range(0, numberOfCode).forEach(i -> {
			codes.add(CodeHelper.getInstance().generateContainerSupportCode());
		});
		
		return ok(Json.toJson(codes));
	}
	
}

