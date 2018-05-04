package controllers.containers.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import controllers.NGLControllerHelper;
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
import fr.cea.ig.ngl.dao.api.APISemanticException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.containers.ContainersAPI;
import fr.cea.ig.ngl.dao.api.containers.ContainersDAO;
import fr.cea.ig.lfw.utils.Streamer;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.data.Form;
import play.mvc.Result;
import workflows.container.ContWorkflows;


@Historized
public class Containers extends NGLAPIController<ContainersAPI, ContainersDAO, Container> implements StateController {
	
	private final Form<Container> form;
	private final Form<State> stateForm;
	private final Form<ContainerBatchElement> batchElementForm;
	
	@Inject
	public Containers(NGLApplication app, ContainersAPI api, ContWorkflows workflows) {
		super(app, api, ContainersSearchForm.class);
		form = app.formFactory().form(Container.class);
		stateForm = app.formFactory().form(State.class);
		batchElementForm = app.formFactory().form(ContainerBatchElement.class);
	}

	@Override
	public Container saveImpl() throws APIValidationException, APISemanticException {
		Container input = getFilledForm(form, Container.class).get();
		Container c = api().create(input, getCurrentUser());
		return c;
	}

	@Override
	public Container updateImpl(String code) throws Exception, APIException, APIValidationException {
		Container input = getFilledForm(form, Container.class).get();
		if(code.equals(input.code)) { 
			Container containerInDB = api().get(code);
			if (!containerInDB.state.code.equals(input.state.code)) throw new Exception("You can not change the state code. Please use the state url ! ");
			
			QueryFieldsForm queryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class).get();
			Container c = null;
				if(queryFieldsForm.fields == null) { 
					c = api().update(input, getCurrentUser());
				} else {
					c = api().update(input, getCurrentUser(), queryFieldsForm.fields);
				}
				return c;
		} else {
			throw new Exception("Container codes are not the same");
		}
	}

//	@Override
//	@Authenticated
//	@Authorized.Read
//	public Result list() {
//		try {
//			ContainersSearchForm containersSearch = objectFromRequestQueryString(ContainersSearchForm.class);
//			if (containersSearch.reporting) {
//				MongoCursor<Container> data = api().findByQuery(containersSearch.reportingQuery);
//				if (containersSearch.datatable) {
//					return MongoStreamer.okStreamUDT(data);
//				} else if(containersSearch.list) {
//					return MongoStreamer.okStream(data);
//				} else if(containersSearch.count) {
//					int count = api().count(containersSearch.reportingQuery);
//					Map<String, Integer> map = new HashMap<>(1);
//					map.put("result", count);
//					return okAsJson(map);
//				} else {
//					return badRequest();
//				}
//			} else {
//				DBQuery.Query query = getQuery(containersSearch);
//				BasicDBObject keys = null;
//				if(! containersSearch.includes().contains("default")) keys = generateBasicDBObjectFromKeys(containersSearch); 
//				
//				List<Container> results = null;
//				if (containersSearch.datatable) {
//					Source<ByteString, ?> resultsAsStream = null; 
//					if(containersSearch.isServerPagination()){
//						if(keys == null){
//							resultsAsStream = api().streamUDTWithDefaultKeys(query, 
//																			 containersSearch.orderBy, 
//																			 Sort.valueOf(containersSearch.orderSense), 
//																			 containersSearch.pageNumber, 
//																			 containersSearch.numberRecordsPerPage);
//						} else {
//							resultsAsStream = api().streamUDT(query, 
//															  containersSearch.orderBy, 
//															  Sort.valueOf(containersSearch.orderSense), 
//															  keys, 
//															  containersSearch.pageNumber, 
//															  containersSearch.numberRecordsPerPage);
//						}
//					} else {
//						if(keys == null){
//							resultsAsStream = api().streamUDTWithDefaultKeys(query, 
//																			 containersSearch.orderBy, 
//																			 Sort.valueOf(containersSearch.orderSense), 
//																			 containersSearch.limit);
//						} else {
//							resultsAsStream = api().streamUDT(query, 
//															  containersSearch.orderBy, 
//															  Sort.valueOf(containersSearch.orderSense), 
//															  keys, 
//															  containersSearch.limit);
//						}
//					}
//					return Streamer.okStream(resultsAsStream);
//				} else  {
//					if(containersSearch.orderBy == null) containersSearch.orderBy = "code";
//					if(containersSearch.orderSense == null) containersSearch.orderSense = 0;
//
//					if(containersSearch.list) {
//						keys = new BasicDBObject();
//						keys.put("_id", 0);//Don't need the _id field
//						keys.put("code", 1);
//						results = api().list(query, 
//											 containersSearch.orderBy, 
//											 Sort.valueOf(containersSearch.orderSense), 
//											 keys, 
//											 containersSearch.limit);	
//						return MongoStreamer.okStream(convertToListObject(results, x -> x.code, x -> x.code)); // in place of getLOChunk(MongoDBResult<T> all)
//					} else if(containersSearch.count) {
//						int count = api().count(containersSearch.reportingQuery);
//						Map<String, Integer> m = new HashMap<>(1);
//						m.put("result", count);
//						return okAsJson(m);
//					} else {
//						return Streamer.okStream(api().stream(query, 
//															  containersSearch.orderBy, 
//															  Sort.valueOf(containersSearch.orderSense), 
//															  keys, 
//															  containersSearch.limit));
//					}
//				}
//			}
//		} catch (Exception e) {
//			getLogger().error(e.getMessage());
//			return nglGlobalBadRequest();
//		}
//	}
	

	@Override
	public Object updateStateImpl(String code, State state) throws APIValidationException, APIException {
		return api().updateState(code, state, getCurrentUser());
	}
	
//	@Override
//	@Authenticated
//	@Authorized.Write
//	public Result updateState(String code) {
//		try {
//			Form<State> filledForm =  getFilledForm(stateForm, State.class);
//			State state = filledForm.get();
//			state.date = new Date();
//			state.user = getCurrentUser();
//			Container container = api().updateState(code, state, getCurrentUser());
//			return okAsJson(container);
//		} catch (APIValidationException e) {
//			getLogger().error(e.getMessage());
//			if(e.getErrors() != null) {
//				return badRequestAsJson(errorsAsJson(e.getErrors()));
//			} else {
//				return badRequestAsJson(e.getMessage());
//			}
//		} catch (APIException e) {
//			getLogger().error(e.getMessage());
//			return badRequestAsJson(e.getMessage());
//		} catch (Exception e) {
//			getLogger().error(e.getMessage());
//			return nglGlobalBadRequest();
//		}
//	}
//	
//	@Override
//	@Authenticated
//	@Authorized.Write
//	public Result updateStateBatch() {
//		try {
//			List<Form<ContainerBatchElement>> filledForms =  getFilledFormList(batchElementForm, ContainerBatchElement.class);
//			final Lang lang = Http.Context.Implicit.lang();
//			List<DatatableBatchResponseElement> response = filledForms.parallelStream()
//					.map(filledForm -> {
//						ContainerBatchElement element = filledForm.get();
//						State state = element.data.state;
//						state.date = new Date();
//						state.user = getCurrentUser();
//						try {
//							Container container = api().updateState(element.data.code, state, getCurrentUser());
//							return new DatatableBatchResponseElement(OK, container, element.index);
//						} catch (APIValidationException e) {
//							return new DatatableBatchResponseElement(BAD_REQUEST, errorsAsJson(lang, e.getErrors()), element.index);
//						} catch (APIException e) {
//							return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
//						}
//					}).collect(Collectors.toList());
//			return okAsJson(response);
//		} catch (Exception e) {
//			getLogger().error(e.getMessage());
//			return nglGlobalBadRequest();
//		}
//	}	
	
	/*
	 * Construct the container query
	 * @param containersSearch
	 * @return
	 * @throws DAOException 
	 */
	/**
	 * @param containersSearch
	 * @return
	 * @throws DAOException
	 * @see ContainersSearchForm#getQuery()
	 */
	@Deprecated
	public DBQuery.Query getQuery(ContainersSearchForm containersSearch) throws DAOException{		
		List<DBQuery.Query> queryElts = new ArrayList<>();
		Query query = DBQuery.empty();

		
		if(containersSearch.processProperties.size() > 0){	
			List<String> processCodes = new ArrayList<>();
			List<DBQuery.Query> listProcessQuery = NGLControllerHelper.generateQueriesForProperties(containersSearch.processProperties, Level.CODE.Process, "properties");
			Query processQuery = DBQuery.and(listProcessQuery.toArray(new DBQuery.Query[queryElts.size()]));

			List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, processQuery).toList();
			for(Process p : processes){
				processCodes.add(p.code);
			}
			queryElts.add(DBQuery.in("processCodes", processCodes));
		}
		
		if (CollectionUtils.isNotEmpty(containersSearch.sampleTypeCodes)) { //all
			queryElts.add(DBQuery.in("contents.sampleTypeCode", containersSearch.sampleTypeCodes));
		}

		if(StringUtils.isNotBlank(containersSearch.ncbiScientificNameRegex)){
			queryElts.add(DBQuery.regex("contents.ncbiScientificName", Pattern.compile(containersSearch.ncbiScientificNameRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(containersSearch.codes)){
			queryElts.add(DBQuery.in("code", containersSearch.codes));
		}else if(StringUtils.isNotBlank(containersSearch.code)){
			queryElts.add(DBQuery.is("code", containersSearch.code));
		}else if(StringUtils.isNotBlank(containersSearch.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(containersSearch.codeRegex)));
		}
		
		if(StringUtils.isNotBlank(containersSearch.treeOfLifePathRegex)){
			queryElts.add(DBQuery.regex("treeOfLife.paths", Pattern.compile(containersSearch.treeOfLifePathRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(containersSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", containersSearch.stateCodes));
		}else if(StringUtils.isNotBlank(containersSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", containersSearch.stateCode));
		}

		if(StringUtils.isNotBlank(containersSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", containersSearch.categoryCode));
		}
		
		if(containersSearch.sampleCodesFromIWCProcess){
		
			if(StringUtils.isBlank(containersSearch.nextProcessTypeCode))
				throw new RuntimeException("Missing nextProcessTypeCode to search container if sampleCodesFromIWCProcess");
			
			//1 extract all sampleCode from process in IW-C
			Set<String> sampleCodes = new TreeSet<>();
			List<Pattern> samplePathRegex = new ArrayList<>();
			MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.is("state.code", "IW-C").is("typeCode", containersSearch.nextProcessTypeCode))
			.cursor.forEach(p ->{
				sampleCodes.addAll(p.sampleCodes);
				String regexPath = ","+p.sampleCodes.iterator().next();
				samplePathRegex.add(Pattern.compile(regexPath));
			});
			
			if(CollectionUtils.isNotEmpty(sampleCodes)){
				//2 search all sample childs from previous sample
				List<Query> l = samplePathRegex.stream().map(r -> DBQuery.regex("life.path", r)).collect(Collectors.toList());
				MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, 
						DBQuery.or(l.toArray(new Query[0]))).cursor.forEach(s -> sampleCodes.add(s.code));
				
				if(CollectionUtils.isNotEmpty(containersSearch.sampleCodes)){
					containersSearch.sampleCodes.retainAll(sampleCodes);
					if(CollectionUtils.isNotEmpty(containersSearch.sampleCodes)){
						queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCodes));
					}else{
						queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
					}
					
				}else if(CollectionUtils.isNotEmpty(sampleCodes)){
					queryElts.add(DBQuery.in("sampleCodes", sampleCodes));
				}else{
					queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
				}
			}else{
				queryElts.add(DBQuery.in("sampleCodes", "-1")); // none results
			}
		}else {
			if(CollectionUtils.isNotEmpty(containersSearch.projectCodes)){
				queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCodes));
			}else if(StringUtils.isNotBlank(containersSearch.projectCode)){
				queryElts.add(DBQuery.in("projectCodes", containersSearch.projectCode));
			}
			
			if(CollectionUtils.isNotEmpty(containersSearch.sampleCodes)){
				queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCodes));
			}else if(StringUtils.isNotBlank(containersSearch.sampleCode)){
				queryElts.add(DBQuery.in("sampleCodes", containersSearch.sampleCode));
			}
		}
		
		if(CollectionUtils.isNotEmpty(containersSearch.supportCodes)){
			queryElts.add(DBQuery.in("support.code", containersSearch.supportCodes));
		}else if(StringUtils.isNotBlank(containersSearch.supportCode)){
			queryElts.add(DBQuery.is("support.code", containersSearch.supportCode));
		}else if(StringUtils.isNotBlank(containersSearch.supportCodeRegex)){
			queryElts.add(DBQuery.regex("support.code", Pattern.compile(containersSearch.supportCodeRegex)));
		}

		if(StringUtils.isNotBlank(containersSearch.supportStorageCodeRegex)){
			queryElts.add(DBQuery.regex("support.storageCode", Pattern.compile(containersSearch.supportStorageCodeRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(containersSearch.fromPurificationTypeCodes)){
			if(containersSearch.fromPurificationTypeCodes.contains("none")){
				queryElts.add(DBQuery.or(DBQuery.size("fromPurificationTypeCode", 0),DBQuery.notExists("fromPurificationTypeCode")
						,DBQuery.in("fromPurificationTypeCode", containersSearch.fromPurificationTypeCodes)));
			}else{
				queryElts.add(DBQuery.in("fromPurificationTypeCode", containersSearch.fromPurificationTypeCodes));

			}
		}
		
		if(CollectionUtils.isNotEmpty(containersSearch.fromTransfertTypeCodes)){ 
				if(containersSearch.fromTransfertTypeCodes.contains("none")){
					queryElts.add(DBQuery.or(DBQuery.size("fromTransfertTypeCode", 0),DBQuery.notExists("fromTransfertTypeCode")
							,DBQuery.in("fromTransfertTypeCode", containersSearch.fromTransfertTypeCodes)));
				}else{
					queryElts.add(DBQuery.in("fromTransfertTypeCode", containersSearch.fromTransfertTypeCodes));
				}			
		}
				
		if(CollectionUtils.isNotEmpty(containersSearch.containerSupportCategories)){
			queryElts.add(DBQuery.in("support.categoryCode", containersSearch.containerSupportCategories));
		}else if(StringUtils.isNotBlank(containersSearch.containerSupportCategory)){
			queryElts.add(DBQuery.is("support.categoryCode", containersSearch.containerSupportCategory));
		}else if(StringUtils.isNotBlank(containersSearch.nextExperimentTypeCode)){
			List<ContainerSupportCategory> containerSupportCategories = ContainerSupportCategory.find.findInputByExperimentTypeCode(containersSearch.nextExperimentTypeCode);
			List<String> cs = new ArrayList<>();
			for(ContainerSupportCategory c:containerSupportCategories){
				cs.add(c.code);
			}
			if(cs.size() > 0){
				queryElts.add(DBQuery.in("support.categoryCode", cs));
			}
		}



		List<String> listePrevious = new ArrayList<>();
		//used in processes creation
		if(StringUtils.isNotBlank(containersSearch.nextProcessTypeCode)){					
					
			ProcessType processType = ProcessType.find.findByCode(containersSearch.nextProcessTypeCode);
			if(processType != null){
				
				List<ExperimentType> experimentTypes = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(processType.firstExperimentType.code, processType.code);
				
				boolean onlyEx = true;
				for(ExperimentType e:experimentTypes){
					//Logger.info(e.code);
					if(!e.code.startsWith("ex")){
						onlyEx = false;
					}
					listePrevious.add(e.code);
				}			
				
				if(CollectionUtils.isNotEmpty(containersSearch.fromTransformationTypeCodes) && containersSearch.fromTransformationTypeCodes.contains("none")){
						queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
						,DBQuery.in("fromTransformationTypeCodes", containersSearch.fromTransformationTypeCodes)));					
				}else if(CollectionUtils.isNotEmpty(containersSearch.fromTransformationTypeCodes)){
					queryElts.add(DBQuery.in("fromTransformationTypeCodes", containersSearch.fromTransformationTypeCodes));
				}else if(!onlyEx){
					queryElts.add(DBQuery.or(DBQuery.in("fromTransformationTypeCodes", listePrevious), DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")));
				}else{
					queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")));
				}
				
			
			}else{
				logger.error("NGL-SQ bad nextProcessTypeCode: "+containersSearch.nextProcessTypeCode);
				return null;
			}
		//used in experiment creation	
		}else if(StringUtils.isNotBlank(containersSearch.nextExperimentTypeCode)){
			
			List<DBQuery.Query> subQueryElts = new ArrayList<>();
			List<ProcessType> processTypes=ProcessType.find.findByExperimentTypeCode(containersSearch.nextExperimentTypeCode);
			if(CollectionUtils.isNotEmpty(processTypes)){
				for(ProcessType processType:processTypes){
					List<ExperimentType> previousExpType = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(containersSearch.nextExperimentTypeCode,processType.code);
					//Logger.debug("NB Previous exp : "+previousExpType.size());
					Set<String> previousExpTypeCodes = previousExpType.stream().map(et -> et.code).collect(Collectors.toSet());
					
					if(CollectionUtils.isNotEmpty(containersSearch.fromTransformationTypeCodes)){
						previousExpTypeCodes = previousExpTypeCodes
													.stream()
													.filter(petc -> (containersSearch.fromTransformationTypeCodes.contains(petc)
															|| (containersSearch.fromTransformationTypeCodes.contains("none") && petc.startsWith("ext-to-"))))
													.collect(Collectors.toSet());
					}
					
					if(CollectionUtils.isNotEmpty(previousExpTypeCodes)){
						subQueryElts.add(DBQuery.in("processTypeCodes", processType.code).in("fromTransformationTypeCodes", previousExpTypeCodes));						
					}else{
						subQueryElts.add(DBQuery.in("processTypeCodes", "-1")); //force to return zero result;
					}
					
					
				}
				if(subQueryElts.size() > 0){
					queryElts.add(DBQuery.or(subQueryElts.toArray(new DBQuery.Query[0])));
				}
				
			}else{
				//if not processType we not return any container
				queryElts.add(DBQuery.notExists("code"));
			}
			
			
		} else if(CollectionUtils.isNotEmpty(containersSearch.fromTransformationTypeCodes)){
			if(containersSearch.fromTransformationTypeCodes.contains("none")){
					queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes")
					, DBQuery.regex("fromTransformationTypeCodes", Pattern.compile("^ext-to-.+$")),DBQuery.in("fromTransformationTypeCodes", containersSearch.fromTransformationTypeCodes)));
			} else {
				queryElts.add(DBQuery.in("fromTransformationTypeCodes", containersSearch.fromTransformationTypeCodes));
			}
		}
		
		
		if(null != containersSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", containersSearch.fromDate));
		}

		if(null != containersSearch.toDate){
			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(containersSearch.toDate, 1))));
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
			queryElts.add(DBQuery.in("processTypeCodes", containersSearch.processTypeCode));
		}

		
		if(CollectionUtils.isNotEmpty(containersSearch.createUsers)){
			queryElts.add(DBQuery.in("traceInformation.createUser", containersSearch.createUsers));
		}else if(StringUtils.isNotBlank(containersSearch.createUser)){
			queryElts.add(DBQuery.is("traceInformation.createUser", containersSearch.createUser));
		}
		
		
		if (CollectionUtils.isNotEmpty(containersSearch.stateResolutionCodes)) { //all
			queryElts.add(DBQuery.in("state.resolutionCodes", containersSearch.stateResolutionCodes));
		}
		
		if(StringUtils.isNotBlank(containersSearch.commentRegex)){
			queryElts.add(DBQuery.elemMatch("comments", DBQuery.regex("comment", Pattern.compile(containersSearch.commentRegex))));
		}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(containersSearch.contentsProperties,Level.CODE.Content, "contents.properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(containersSearch.properties,Level.CODE.Container, "properties"));

		queryElts.addAll(NGLControllerHelper.generateExistsQueriesForFields(containersSearch.existingFields));
		queryElts.addAll(NGLControllerHelper.generateQueriesForFields(containersSearch.queryFields));
		
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		
		
		return query;
	}

}
