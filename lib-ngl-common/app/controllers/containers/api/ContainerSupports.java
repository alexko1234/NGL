package controllers.containers.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jongo.MongoCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.NGLAPIController;
import controllers.QueryFieldsForm;
import controllers.StateController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.containers.ContainerSupportsAPI;
import fr.cea.ig.ngl.dao.api.containers.ContainerSupportsDAO;
import fr.cea.ig.util.Streamer;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Http;
import play.mvc.Result;
import views.components.datatable.DatatableBatchResponseElement;
import workflows.container.ContSupportWorkflows;

@Historized
public class ContainerSupports extends NGLAPIController<ContainerSupportsAPI, ContainerSupportsDAO, ContainerSupport> implements StateController {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainerSupports.class);
	
	private final Form<ContainerSupport>             containerSupportForm;
	private final Form<QueryFieldsForm>              updateForm; 
	private final Form<ContainerSupportBatchElement> batchElementForm;
	private final Form<State>                        stateForm; 
	
	@Inject
	public ContainerSupports(NGLApplication app, ContainerSupportsAPI api, ContSupportWorkflows workflows) {
		super(app, api);
		containerSupportForm       = app.formFactory().form(ContainerSupport.class);
		updateForm                 = app.formFactory().form(QueryFieldsForm.class);
		batchElementForm           = app.formFactory().form(ContainerSupportBatchElement.class);
		stateForm                  = app.formFactory().form(State.class);
	}
	
	@Override
	@Authenticated
	@Authorized.Write
	public Result updateState(String code) {
		try {
			Form<State> filledForm =  getFilledForm(stateForm, State.class);
			State state = filledForm.get();
			state.date = new Date();
			state.user = getCurrentUser();
			ContainerSupport support = api().updateState(code, state, getCurrentUser());
			return okAsJson(support);
		} catch (APIValidationException e) {
			getLogger().error(e.getMessage());
			if(e.getErrors() != null) {
				return badRequestAsJson(errorsAsJson(e.getErrors()));
			} else {
				return badRequestAsJson(e.getMessage());
			}
		} catch (APIException e) {
			getLogger().error(e.getMessage());
			return badRequestAsJson(e.getMessage());
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}


	@Override
	@Authenticated
	@Authorized.Write
	public Result updateStateBatch() {
		try {
			List<Form<ContainerSupportBatchElement>> filledForms =  getFilledFormList(batchElementForm, ContainerSupportBatchElement.class);
			final Lang lang = Http.Context.Implicit.lang();
			List<DatatableBatchResponseElement> response = filledForms.parallelStream()
					.map(filledForm -> {
						ContainerSupportBatchElement element = filledForm.get();
						State state = element.data.state;
						state.date = new Date();
						state.user = getCurrentUser();
						try {
							ContainerSupport support = api().updateState(element.data.code, state, getCurrentUser());
							return new DatatableBatchResponseElement(OK, support, element.index);
						} catch (APIValidationException e) {
							return new DatatableBatchResponseElement(BAD_REQUEST, errorsAsJson(lang, e.getErrors()), element.index);
						} catch (APIException e) {
							return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
						}
					}).collect(Collectors.toList());
			return okAsJson(response);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}


	@Override
	@Authenticated
	@Authorized.Read
	public Result list() {
		try {
			ContainerSupportsSearchForm containersSupportSearch = objectFromRequestQueryString(ContainerSupportsSearchForm.class);
			if (containersSupportSearch.reporting) {
				MongoCursor<ContainerSupport> data = api().findByQuery(containersSupportSearch.reportingQuery);
				if (containersSupportSearch.datatable) {
					return MongoStreamer.okStreamUDT(data);
				} else if(containersSupportSearch.list) {
					return MongoStreamer.okStream(data);
				} else if(containersSupportSearch.count) {
					int count = api().count(containersSupportSearch.reportingQuery);
					Map<String, Integer> map = new HashMap<String, Integer>(1);
					map.put("result", count);
					return okAsJson(map);
				} else {
					return badRequest();
				}
			} else {
				DBQuery.Query query = getQuery(containersSupportSearch);
				BasicDBObject keys = null;
				if(! containersSupportSearch.includes().contains("default")) keys = getKeys(containersSupportSearch); 
				
				List<ContainerSupport> results = null;
				if (containersSupportSearch.datatable) {
					Source<ByteString, ?> resultsAsStream = null; 
					if(containersSupportSearch.isServerPagination()){
						if(keys == null){
							resultsAsStream = api().streamUDTWithDefaultKeys(query, 
																			 containersSupportSearch.orderBy, 
																			 Sort.valueOf(containersSupportSearch.orderSense), 
																			 containersSupportSearch.pageNumber, 
																			 containersSupportSearch.numberRecordsPerPage);
						} else {
							resultsAsStream = api().streamUDT(query, 
									   						  containersSupportSearch.orderBy, 
									   						  Sort.valueOf(containersSupportSearch.orderSense), 
									   						  keys, containersSupportSearch.pageNumber, 
									   						  containersSupportSearch.numberRecordsPerPage);
						}
					} else {
						if(keys == null){
							resultsAsStream = api().streamUDTWithDefaultKeys(query, 
																			 containersSupportSearch.orderBy, 
																			 Sort.valueOf(containersSupportSearch.orderSense), 
																			 containersSupportSearch.limit);
						} else {
							resultsAsStream = api().streamUDT(query, 
															  containersSupportSearch.orderBy, 
															  Sort.valueOf(containersSupportSearch.orderSense), 
															  keys, 
															  containersSupportSearch.limit);
						}
					}
					return Streamer.okStream(resultsAsStream);
				} else  {
					if(containersSupportSearch.orderBy == null) containersSupportSearch.orderBy = "code";
					if(containersSupportSearch.orderSense == null) containersSupportSearch.orderSense = 0;

					if(containersSupportSearch.list) {
						keys = new BasicDBObject();
						keys.put("_id", 0);//Don't need the _id field
						keys.put("code", 1);
						results = api().list(query, 
											 containersSupportSearch.orderBy, 
											 Sort.valueOf(containersSupportSearch.orderSense), 
											 keys, 
											 containersSupportSearch.limit);	
						return MongoStreamer.okStream(convertToListObject(results, x -> x.code, x -> x.code)); // in place of getLOChunk(MongoDBResult<T> all)
					} else if(containersSupportSearch.count) {
						int count = api().count(containersSupportSearch.reportingQuery);
						Map<String, Integer> m = new HashMap<String, Integer>(1);
						m.put("result", count);
						return okAsJson(m);
					} else {
						return Streamer.okStream(api().stream(query, 
															  containersSupportSearch.orderBy, 
															  Sort.valueOf(containersSupportSearch.orderSense), 
															  keys, 
															  containersSupportSearch.limit));
					}
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}


	@Override
	public ContainerSupport saveImpl()throws APIValidationException, APIException {
		ContainerSupport input = getFilledForm(containerSupportForm, ContainerSupport.class).get();
		ContainerSupport cs = api().create(input, getCurrentUser());
		return cs;
	}


	@Override
	public ContainerSupport updateImpl(String code) throws Exception, APIException, APIValidationException {
		ContainerSupport input = getFilledForm(containerSupportForm, ContainerSupport.class).get();
		if(code.equals(input.code)) { 
			ContainerSupport containerSupportInDB = api().get(code);
			if (!containerSupportInDB.state.code.equals(input.state.code)) throw new Exception("You can not change the state code. Please use the state url ! ");
			
			QueryFieldsForm queryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class).get();
			ContainerSupport cs = null;
				if(queryFieldsForm.fields == null) { 
					cs = api().update(input, getCurrentUser());
				} else {
					cs = api().update(input, getCurrentUser(), queryFieldsForm.fields);
				}
				return cs;
		} else {
			throw new Exception("Container support codes are not the same");
		}
	}
	
	@Authenticated
	@Authorized.Write
	public Result saveCode(Integer numberOfCode) {
		try {
			List<String> codes = new ArrayList<String>(numberOfCode);
			IntStream.range(0, numberOfCode).forEach(i -> {
				codes.add(CodeHelper.getInstance().generateContainerSupportCode());
			});
			return okAsJson(codes);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}

	
	/**
	 * Construct the support query
	 * @param supportsSearch
	 * @return
	 * @throws DAOException 
	 */
	private static DBQuery.Query getQuery(ContainerSupportsSearchForm supportsSearch) throws DAOException {
		List<DBQuery.Query> queryElts = new ArrayList<>();
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
				List<String> supports  =new ArrayList<>();
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
}

