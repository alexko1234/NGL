package fr.cea.ig.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.NGLControllerHelper;
import controllers.samples.api.SamplesSearchForm;
import models.laboratory.common.description.Level;

// Try to provide static methods shortcuts to provide declarative support
// for the query form to mongojack query.
// Could possibly extends Optional<DBQuery.Query>. Yet, moving
// from functional to non functional is porbably a lost cause.
public class QueryBuilder { 
	
	// This is some implicit and
	// private List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
	// Use non static, non fluent stuff.
	
	private DBQuery.Query query;
	
	public QueryBuilder(DBQuery.Query query) {
		this.query = query;
	}
	
	public DBQuery.Query query() {
		return query();
	}
	// public void qadd(DBQuery.Query q) {	queryElts.add(q); }
	// public void and(QueryBuilder q) { qadd(q); }
	
	/*public void firstOf(QueryBuilder... qbs) {
		for (QueryBuilder b : qbs)
			if (b.isEffective()) {
				//queryElts.add(b);
				return;
			}
	}*/
	
	public static Optional<QueryBuilder> in(String key, Collection<String> strs) {
		if (CollectionUtils.isNotEmpty(strs))
			return Optional.of(new QueryBuilder(DBQuery.in(key,strs)));
		return Optional.empty();
	}
	// Seems dubious, should be equals, not in
	public static Optional<QueryBuilder> in(String key, String str) {
		if (StringUtils.isNotBlank(str))
			return Optional.of(new QueryBuilder(DBQuery.in(key,str)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> is(String key, String str) {
		if(StringUtils.isNotBlank(str))
			return Optional.of(new QueryBuilder(DBQuery.is(key,str)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> regex(String key, String regex) {
		if (StringUtils.isNotBlank(regex))
			return Optional.of(new QueryBuilder(DBQuery.regex(key, Pattern.compile(regex))));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> greaterThanEquals(String key, Object value) {
		if (value != null)
			return Optional.of(new QueryBuilder(DBQuery.greaterThanEquals(key, value)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> lessThan(String key, Object value) {
		if (value != null)
			return Optional.of(new QueryBuilder(DBQuery.lessThan(key, value)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> notEquals(String key, Object value) {
		if (value != null)
			return Optional.of(new QueryBuilder(DBQuery.notEquals(key, value)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> elemMatch(String key, Optional<QueryBuilder> q) {
		return q.map(r -> new QueryBuilder(DBQuery.elemMatch("comments",r.query())));
	}
	public static Optional<QueryBuilder> exists(String key) {
		if (key != null)
			return Optional.of(new QueryBuilder(DBQuery.exists(key)));
		return Optional.empty();
	}
	public static Optional<QueryBuilder> notExists(String key) {
		if (key != null)
			return Optional.of(new QueryBuilder(DBQuery.notExists(key)));
		return Optional.empty();
	}
	public static QueryBuilder and(QueryBuilder a, QueryBuilder b) {
		return new QueryBuilder(DBQuery.and(a.query(),b.query()));
	}
	@SafeVarargs
	public static Optional<QueryBuilder> firstOf(Optional<QueryBuilder>... bs) {
		for (Optional<QueryBuilder> b : bs) 
			if (b.isPresent())
				return b;
		return Optional.empty();
	}
	public static Optional<QueryBuilder> and(Optional<QueryBuilder> q0, Optional<QueryBuilder> q1) {
		if (q0.isPresent()) {
			if (q1.isPresent()) {
				return Optional.of(QueryBuilder.and(q0.get(),q1.get()));
			} else {
				return q0;
			}
		} else {
			if (q1.isPresent())
				return q1;
			else 
				return q0; // empty, could be q1
		}
		
	}
	
	public static DBQuery.Query query(Optional<QueryBuilder> b) {
		return b.map(x -> x.query()).orElse(DBQuery.empty());
	}
	
	// Pointless functional fun, plain iteration on the entry set
	// would be simpler and faster.
	public static Optional<QueryBuilder> generateQueriesForExistingProperties(Optional<QueryBuilder> b, Map<String, Boolean> existingFields) {
		return existingFields.entrySet().stream()
		.map(e -> {
			if (e.getValue().booleanValue()) 
				return exists(e.getKey());
			else 
				return notExists(e.getKey());
		}).reduce(b,(q0,q1) -> and(q0,q1));
	}
	
	public static DBQuery.Query getQuery(SamplesSearchForm samplesSearch) {
		// TODO: simply build return value at method end
		Query query = DBQuery.empty();
		
		Optional<QueryBuilder> qb = Optional.empty();
		// QueryBuilder qb = null;
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		
		qb = and(qb,firstOf(in   ("code", samplesSearch.codes),
							is   ("code", samplesSearch.code),
							regex("code", samplesSearch.codeRegex)));
		//if(CollectionUtils.isNotEmpty(samplesSearch.codes)){
		//	queryElts.add(DBQuery.in("code", samplesSearch.codes));
		//}else if(StringUtils.isNotBlank(samplesSearch.code)){
		//	queryElts.add(DBQuery.is("code", samplesSearch.code));
		//}else if(StringUtils.isNotBlank(samplesSearch.codeRegex)){
		//	queryElts.add(DBQuery.regex("code", Pattern.compile(samplesSearch.codeRegex)));
		//}
		
		// return and(in   ("typeCode",       samplesSearch.typeCodes),
		//		   regex("referenceCollab",samplesSearch.referenceCollabRegex),
		//		   in   ("projectCodes",   samplesSearch.projectCode)).query();
		
		qb = and(qb,in("typeCode", samplesSearch.typeCodes));
		//if(CollectionUtils.isNotEmpty(samplesSearch.typeCodes)){
		//	queryElts.add(DBQuery.in("typeCode", samplesSearch.typeCodes));
		//}
		
		qb = and(qb,regex("referenceCollab",samplesSearch.referenceCollabRegex));
		//if(StringUtils.isNotBlank(samplesSearch.referenceCollabRegex)){
		//	queryElts.add(DBQuery.regex("referenceCollab", Pattern.compile(samplesSearch.referenceCollabRegex)));
		//}
		
		qb = and(qb,in("projectCodes", samplesSearch.projectCode));
		//if(StringUtils.isNotBlank(samplesSearch.projectCode)){
		//	queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCode));
		//}

		qb = and(qb,in("projectCodes", samplesSearch.projectCodes));
		//if(CollectionUtils.isNotEmpty(samplesSearch.projectCodes)){ 				//samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0
		//	queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		//}

		qb = and(qb,regex("life.path",samplesSearch.treeOfLifePathRegex));
		//if(StringUtils.isNotBlank(samplesSearch.treeOfLifePathRegex)){
		//	queryElts.add(DBQuery.regex("life.path", Pattern.compile(samplesSearch.treeOfLifePathRegex)));
		//}
		
		// TODO: redundant code, done at method end 
		//if(queryElts.size() > 0){
		//	query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		//}
		
		qb = and(qb,greaterThanEquals("traceInformation.creationDate", samplesSearch.fromDate));
		// if(null != samplesSearch.fromDate){
		//	queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", samplesSearch.fromDate));
		//}

		qb = and(qb,lessThan("traceInformation.creationDate",samplesSearch.toDate));
		//if(null != samplesSearch.toDate){
		//	queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(samplesSearch.toDate, 1))));
		//}
		
		qb = and(qb,
				firstOf(in("traceInformation.createUser", samplesSearch.createUsers),
			 		    is("traceInformation.createUser", samplesSearch.createUser)));
		// if(CollectionUtils.isNotEmpty(samplesSearch.createUsers)){
		// 	queryElts.add(DBQuery.in("traceInformation.createUser", samplesSearch.createUsers));
		//}else if(StringUtils.isNotBlank(samplesSearch.createUser)){
		//	queryElts.add(DBQuery.is("traceInformation.createUser", samplesSearch.createUser));
		//}
		
		qb = and(qb,elemMatch("comments",regex("comment",samplesSearch.commentRegex)));
		//if(StringUtils.isNotBlank(samplesSearch.commentRegex)){
		//	queryElts.add(DBQuery.elemMatch("comments", DBQuery.regex("comment", Pattern.compile(samplesSearch.commentRegex))));
		//}
		
		qb = and(qb,is("taxonCode", samplesSearch.taxonCode));
		// if(StringUtils.isNotBlank(samplesSearch.taxonCode)){
		//	queryElts.add(DBQuery.is("taxonCode", samplesSearch.taxonCode));
		// }
		
		qb = and(qb,regex("ncbiScientificName",samplesSearch.ncbiScientificNameRegex));
		//if(StringUtils.isNotBlank(samplesSearch.ncbiScientificNameRegex)){
		//	queryElts.add(DBQuery.regex("ncbiScientificName", Pattern.compile(samplesSearch.ncbiScientificNameRegex)));
		//}
		
		/*
		Optional<QueryBuilder> existingProcessTypeCode = 
				elemMatch("processes", is("typeCode",samplesSearch.existingProcessTypeCode));
		Optional<QueryBuilder> existingTransformationTypeCode = 
				is("experiments.typeCode",samplesSearch.existingTransformationTypeCode);
		Optional<QueryBuilder> notExistingTransformationTypeCode = 
				notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode);
		
		qb = and(qb,
				firstOf(and(existingProcessTypeCode,existingTransformationTypeCode,notExistingTransformationTypeCode)),
					    and(existingTransformationTypeCode,notExistingTransformationTypeCode)
				);
				*/
		if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode)
					      .and(DBQuery.is("experiments.typeCode",samplesSearch.existingTransformationTypeCode),
					    	   DBQuery.notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode))));
		
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
		
		qb = and(qb,in("processes.experiments.protocolCode",samplesSearch.experimentProtocolCodes));
		// if(CollectionUtils.isNotEmpty(samplesSearch.experimentProtocolCodes)){
		//	queryElts.add(DBQuery.in("processes.experiments.protocolCode",samplesSearch.experimentProtocolCodes));
		//}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.properties,Level.CODE.Sample, "properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.experimentProperties,Level.CODE.Experiment, "processes.experiments.properties"));

		qb = generateQueriesForExistingProperties(qb,samplesSearch.existingFields);
		// queryElts.addAll(NGLControllerHelper.generateQueriesForExistingProperties(samplesSearch.existingFields));
		
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		
		
		// return query;
		
		return query(qb); 
	}
	
}

