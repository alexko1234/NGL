package controllers.protocols.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
//import controllers.CommonController;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.mongo.DBQueryBuilder;
import fr.cea.ig.play.NGLContext;

//TODO: cleanup implementation
//@SuppressWarnings("deprecation")
public class Protocols extends Protocols2 { //extends CommonController{

//	final Form<ProtocolsSearchForm> protocolForm = form(ProtocolsSearchForm.class);
//	final Form<Protocol> protocolsForm = form(Protocol.class);

	@Inject
	protected Protocols(NGLContext ctx) {
		super(ctx);
	}

	public Result get(String code){
		return super.get(code);
	}
	
//	public static Result get(String code){
//		Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, code);
//		if(protocol==null){
//			return notFound();
//		}
//		return ok(Json.toJson(protocol));
//	}
//
//	public static Result list() throws DAOException{
//		Form<ProtocolsSearchForm> protocolTypeFilledForm = filledFormQueryString(protocolForm,ProtocolsSearchForm.class);
//		ProtocolsSearchForm protocolsSearch = protocolTypeFilledForm.get();		
//		DBQuery.Query query = getQuery(protocolsSearch);
//		List<Protocol> protocols = new ArrayList<Protocol>();
//		protocolsSearch.orderBy="code";				
//							
//		if(protocolsSearch.datatable){
//			MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query);
//			protocols = results.toList();
//			return ok(Json.toJson(new DatatableResponse<Protocol>(protocols, protocols.size()))); 
//		}else if(protocolsSearch.list){	
//			BasicDBObject keys = new BasicDBObject();
//			keys.put("_id", 0);//Don't need the _id field
//			keys.put("code", 1);
//			keys.put("name", 1);
//			MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query, keys);
//			protocols = results.toList();
//			List<ListObject> lop = new ArrayList<ListObject>();
//			for(Protocol p:protocols){
//				lop.add(new ListObject(p.code,p.name));
//			}
//			return Results.ok(Json.toJson(lop));
//		}else{
//			protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, query).toList();
//			return Results.ok(Json.toJson(protocols));
//		}
//
//	}
//	
//
//	/*
//	 * Construct the protocol query
//	 * @param protocolsSearch
//	 * @return
//	 * @throws DAOException 
//	 */
//	public static DBQuery.Query getQuery(ProtocolsSearchForm protocolsSearch){
//		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
//		Query query = null;
//
//		if (CollectionUtils.isNotEmpty(protocolsSearch.experimentTypeCodes)){
//			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCodes));
//		}else if(StringUtils.isNotBlank(protocolsSearch.experimentTypeCode)){
//			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCode));
//		}
//		
//		if(protocolsSearch.isActive!=null){
//			queryElts.add(DBQuery.is("active", protocolsSearch.isActive));
//		}
//		
//		if(queryElts.size() > 0){
//			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));			
//		}
//
//		return query;
//	}	
}

//Standard NGL implementation
class Protocols2 extends DocumentController<Protocol> {

	private final Form<ProtocolsSearchForm> protocolForm;
//	private final Form<Protocol> protocolsForm;

	@Inject
	protected Protocols2(NGLContext ctx) {
		super(ctx, InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class);
		protocolForm = ctx.form(ProtocolsSearchForm.class);
//		protocolsForm = ctx.form(Protocol.class);
	}

	//TODO replace by get method provided by APICommonController
	public Result get(String code) {
		Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, code);
		if (protocol == null)
			return notFound();
		return ok(Json.toJson(protocol));
	}

	public Result list() throws DAOException{
		Form<ProtocolsSearchForm> protocolTypeFilledForm = filledFormQueryString(protocolForm,ProtocolsSearchForm.class);
		ProtocolsSearchForm protocolsSearch = protocolTypeFilledForm.get();		
		DBQuery.Query query = getQuery(protocolsSearch);
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocolsSearch.orderBy="code";				

		if (protocolsSearch.datatable) {
			MongoDBResult<Protocol> results = mongoDBFinder(protocolsSearch, query);
			//				MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query);
			protocols = results.toList();
			return ok(Json.toJson(new DatatableResponse<Protocol>(protocols, protocols.size()))); 
		} else if(protocolsSearch.list) {	
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			keys.put("name", 1);
			MongoDBResult<Protocol> results = mongoDBFinder(protocolsSearch, query);
			//				MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query, keys);
			protocols = results.toList();
			List<ListObject> lop = new ArrayList<ListObject>();
			for(Protocol p:protocols){
				lop.add(new ListObject(p.code,p.name));
			}
			return Results.ok(Json.toJson(lop));
		} else {
			protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, query).toList();
			return Results.ok(Json.toJson(protocols));
		}
	}


	/*
	 * Construct the protocol query
	 * @param protocolsSearch
	 * @return
	 * @throws DAOException 
	 */
	public static DBQuery.Query getQuery(ProtocolsSearchForm protocolsSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();

		if (CollectionUtils.isNotEmpty(protocolsSearch.experimentTypeCodes)) {
			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCodes));
		} else if(StringUtils.isNotBlank(protocolsSearch.experimentTypeCode)) {
			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCode));
		}

		if (protocolsSearch.isActive != null) {
			queryElts.add(DBQuery.is("active", protocolsSearch.isActive));
		}

//		Query query = null;
//		if (queryElts.size() > 0) {
//			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));			
//		}
//		return query;
		return DBQueryBuilder.query(DBQueryBuilder.and(queryElts));
	}	
	
}
