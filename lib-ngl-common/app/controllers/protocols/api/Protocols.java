package controllers.protocols.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

@SuppressWarnings("deprecation")
public class Protocols extends CommonController{

	final static Form<ProtocolsSearchForm> protocolForm = form(ProtocolsSearchForm.class);
	final static Form<Protocol> protocolsForm = form(Protocol.class);


	public static Result get(String code){
		Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, code);
		if(protocol==null){
			return notFound();
		}
		return ok(Json.toJson(protocol));
	}

	public static Result list() throws DAOException{
		Form<ProtocolsSearchForm> protocolTypeFilledForm = filledFormQueryString(protocolForm,ProtocolsSearchForm.class);
		ProtocolsSearchForm protocolsSearch = protocolTypeFilledForm.get();		
		DBQuery.Query query = getQuery(protocolsSearch);
		List<Protocol> protocols = new ArrayList<Protocol>();
		protocolsSearch.orderBy="code";				
							
		if(protocolsSearch.datatable){
			MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query);
			protocols = results.toList();
			return ok(Json.toJson(new DatatableResponse<Protocol>(protocols, protocols.size()))); 
		}else if(protocolsSearch.list){	
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			keys.put("name", 1);
			MongoDBResult<Protocol> results = mongoDBFinder(InstanceConstants.PROTOCOL_COLL_NAME, protocolsSearch, Protocol.class, query, keys);
			protocols = results.toList();
			List<ListObject> lop = new ArrayList<ListObject>();
			for(Protocol p:protocols){
				lop.add(new ListObject(p.code,p.name));
			}
			return Results.ok(Json.toJson(lop));
		}else{
			protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, query).toList();
			return Results.ok(Json.toJson(protocols));
		}

	}
	
	public static List<Protocol> findProtocols (String experimentCode){
		
		List<Protocol> lp = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.in("experimentTypeCodes", experimentCode)).toList();
		
		return lp;
	}

	/**
	 * Construct the protocol query
	 * @param protocolsSearch
	 * @return
	 * @throws DAOException 
	 */
	public static DBQuery.Query getQuery(ProtocolsSearchForm protocolsSearch){
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query = null;

		if (CollectionUtils.isNotEmpty(protocolsSearch.experimentTypeCodes)){
			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCodes));
		}else if(StringUtils.isNotBlank(protocolsSearch.experimentTypeCode)){
			queryElts.add(DBQuery.in("experimentTypeCodes",protocolsSearch.experimentTypeCode));
		}


		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));			
		}

		return query;
	}


}
