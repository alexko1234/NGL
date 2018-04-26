package controllers.samples.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.NGLAPIController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APISemanticException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.samples.SamplesAPI;
import fr.cea.ig.ngl.dao.samples.SamplesDAO;
import fr.cea.ig.ngl.support.ListFormWrapper;
import fr.cea.ig.lfw.utils.Streamer;
import models.laboratory.common.description.Level;
import models.laboratory.sample.instance.Sample;
import play.data.Form;
import play.mvc.Result;
import views.components.datatable.DatatableForm;
import views.html.searchForm;

@Historized
public class Samples extends NGLAPIController<SamplesAPI, SamplesDAO, Sample> {
	
	private final Form<Sample> sampleForm;


	@Inject
	public Samples(NGLApplication app, SamplesAPI api) {
		super(app, api);
		this.sampleForm = app.formFactory().form(Sample.class);
	}

	@Override
	@Authenticated
	@Authorized.Read
	public Result list() {
		try {
			Source<ByteString, ?> resultsAsStream = api().list(new ListFormWrapper<Sample>(objectFromRequestQueryString(SamplesSearchForm.class), form -> generateBasicDBObjectFromKeys(form)));
			return Streamer.okStream(resultsAsStream);
		} catch (APIException e) {
			getLogger().error(e.getMessage());
			return badRequestAsJson(e.getMessage());
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}
	
//	public Result list() {		
//		try {
//			SamplesSearchForm samplesSearch = objectFromRequestQueryString(SamplesSearchForm.class);
//			if (samplesSearch.reporting) {
//				logger.debug("list : running query {}",samplesSearch.reportingQuery);
//				MongoCursor<Sample> data = api().findByQuery(samplesSearch.reportingQuery);
//				if (samplesSearch.datatable) {
//					return MongoStreamer.okStreamUDT(data);
//				} else if(samplesSearch.list) {
//					return MongoStreamer.okStream(data);
//				} else if(samplesSearch.count) {
//					int count = api().count(samplesSearch.reportingQuery);
//					Map<String, Integer> map = new HashMap<>(1);
//					map.put("result", count);
//					return okAsJson(map);
//				} else {
//					return badRequest();
//				}
//			} else {
//				DBQuery.Query query = getQuery(samplesSearch);
//				BasicDBObject keys = null;
//				if(! samplesSearch.includes().contains("default")){
//					keys = generateBasicDBObjectFromKeys(samplesSearch); 
//				}
//				List<Sample> results = null;
//				if (samplesSearch.datatable) {
//					Source<ByteString, ?> resultsAsStream = null; 
//					if(samplesSearch.isServerPagination()){
//						if(keys == null){
//							resultsAsStream = api().streamUDTWithDefaultKeys(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), samplesSearch.pageNumber, samplesSearch.numberRecordsPerPage);
//						} else {
//							resultsAsStream = api().streamUDT(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), keys, samplesSearch.pageNumber, samplesSearch.numberRecordsPerPage);
//						}
//					} else {
//						if(keys == null){
//							resultsAsStream = api().streamUDTWithDefaultKeys(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), samplesSearch.limit);
//						} else {
//							resultsAsStream = api().streamUDT(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), keys, samplesSearch.limit);
//						}
//					}
//					return Streamer.okStream(resultsAsStream);
//				} else  {
//					if(samplesSearch.orderBy == null) samplesSearch.orderBy = "code";
//					if(samplesSearch.orderSense == null) samplesSearch.orderSense = 0;
//
//					if(samplesSearch.list) {
//						keys = new BasicDBObject();
//						keys.put("_id", 0);//Don't need the _id field
//						keys.put("code", 1);
//						results = api().list(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), keys, samplesSearch.limit);	
//						return MongoStreamer.okStream(convertToListObject(results, x -> x.code, x -> x.code)); // in place of getLOChunk(MongoDBResult<T> all)
//					} else if(samplesSearch.count) {
//						int count = api().count(samplesSearch.reportingQuery);
//						Map<String, Integer> m = new HashMap<>(1);
//						m.put("result", count);
//						return okAsJson(m);
//					} else {
//						return Streamer.okStream(api().stream(query, samplesSearch.orderBy, Sort.valueOf(samplesSearch.orderSense), keys, samplesSearch.limit));
//					}
//				}
//			}
//		} catch (Exception e) {
//			getLogger().error(e.getMessage());
//			return nglGlobalBadRequest();
//		}
//	}
	
	@Override
	@Authenticated
	@Authorized.Read
	public Result get(String code) {
		try {
			DatatableForm form = objectFromRequestQueryString(DatatableForm.class);
			Sample sample = api().getObject(code, generateBasicDBObjectFromKeys(form));
			if (sample == null) {
				return notFound();
			} 
			return okAsJson(sample);
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}	
	}	
	
	/* (non-Javadoc)
	 * @see controllers.NGLAPIController#saveImpl()
	 */
	@Override
	public Sample saveImpl() throws APIValidationException, APISemanticException {
		Sample input = getFilledForm(sampleForm, Sample.class).get();
		Sample s = api().create(input, getCurrentUser());
		return s;
	}

	/* (non-Javadoc)
	 * @see controllers.NGLAPIController#updateImpl(java.lang.String)
	 */
	@Override
	public Sample updateImpl(String code) throws Exception, APIException, APIValidationException {
		getLogger().debug("update Sample with code " + code);
		Form<Sample> filledForm = getFilledForm(sampleForm, Sample.class);
		Sample sampleInForm = filledForm.get();
		if(code.equals(sampleInForm.code)) { 
			QueryFieldsForm queryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class).get();
			Sample s = null;
				if(queryFieldsForm.fields == null) { 
					s = api().update(sampleInForm, getCurrentUser());
				} else {
					s = api().update(sampleInForm, getCurrentUser(), queryFieldsForm.fields);
				}
				return s;
		} else {
			throw new Exception("Sample codes are not the same");
		}
	}

	/**
	 * Construct the sample query.
	 * @param samplesSearch search form
	 * @return              query
	 */
	public DBQuery.Query getQuery(SamplesSearchForm samplesSearch) {
		// TODO: simply build return value at method end
		Query query = DBQuery.empty();

		List<DBQuery.Query> queryElts = new ArrayList<>();

		if(CollectionUtils.isNotEmpty(samplesSearch.codes)){
			queryElts.add(DBQuery.in("code", samplesSearch.codes));
		}else if(StringUtils.isNotBlank(samplesSearch.code)){
			queryElts.add(DBQuery.is("code", samplesSearch.code));
		}else if(StringUtils.isNotBlank(samplesSearch.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(samplesSearch.codeRegex)));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.typeCodes)){
			queryElts.add(DBQuery.in("typeCode", samplesSearch.typeCodes));
		}

		if(StringUtils.isNotBlank(samplesSearch.referenceCollabRegex)){
			queryElts.add(DBQuery.regex("referenceCollab", Pattern.compile(samplesSearch.referenceCollabRegex)));
		}

		if(StringUtils.isNotBlank(samplesSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCode));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.projectCodes)){ 				//samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		}

		if(StringUtils.isNotBlank(samplesSearch.treeOfLifePathRegex)){
			queryElts.add(DBQuery.regex("life.path", Pattern.compile(samplesSearch.treeOfLifePathRegex)));
		}

		// TODO: redundant code, done at method end 
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}


		if(null != samplesSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", samplesSearch.fromDate));
		}

		if(null != samplesSearch.toDate){
			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(samplesSearch.toDate, 1))));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.createUsers)){
			queryElts.add(DBQuery.in("traceInformation.createUser", samplesSearch.createUsers));
		}else if(StringUtils.isNotBlank(samplesSearch.createUser)){
			queryElts.add(DBQuery.is("traceInformation.createUser", samplesSearch.createUser));
		}

		if(StringUtils.isNotBlank(samplesSearch.commentRegex)){
			queryElts.add(DBQuery.elemMatch("comments", DBQuery.regex("comment", Pattern.compile(samplesSearch.commentRegex))));
		}

		if(StringUtils.isNotBlank(samplesSearch.taxonCode)){
			queryElts.add(DBQuery.is("taxonCode", samplesSearch.taxonCode));
		}

		if(StringUtils.isNotBlank(samplesSearch.ncbiScientificNameRegex)){
			queryElts.add(DBQuery.regex("ncbiScientificName", Pattern.compile(samplesSearch.ncbiScientificNameRegex)));
		}

		if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode)
					.and(DBQuery.is("experiments.typeCode",samplesSearch.existingTransformationTypeCode), DBQuery.notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode))));

		}else if(StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.and(DBQuery.is("processes.experiments.typeCode",samplesSearch.existingTransformationTypeCode)
					,DBQuery.notEquals("processes.experiments.typeCode",samplesSearch.notExistingTransformationTypeCode)));		

		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode).is("experiments.typeCode",samplesSearch.existingTransformationTypeCode)));		

		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode).notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode)));		

		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)){
			queryElts.add(DBQuery.is("processes.typeCode",samplesSearch.existingProcessTypeCode));

		}else if(StringUtils.isNotBlank(samplesSearch.notExistingProcessTypeCode)){
			queryElts.add(DBQuery.notEquals("processes.typeCode",samplesSearch.notExistingProcessTypeCode));

		}else if(StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)){
			queryElts.add(DBQuery.is("processes.experiments.typeCode",samplesSearch.existingTransformationTypeCode));

		}else if(StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.notEquals("processes.experiments.typeCode",samplesSearch.notExistingTransformationTypeCode));

		}

		if(CollectionUtils.isNotEmpty(samplesSearch.experimentProtocolCodes)){
			queryElts.add(DBQuery.in("processes.experiments.protocolCode",samplesSearch.experimentProtocolCodes));
		}

		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.properties,Level.CODE.Sample, "properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.experimentProperties,Level.CODE.Experiment, "processes.experiments.properties"));

		queryElts.addAll(NGLControllerHelper.generateExistsQueriesForFields(samplesSearch.existingFields));


		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		

		return query;
	}
}