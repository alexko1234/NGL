package utils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.instance.run.Run;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import play.Logger;
import play.modules.mongodb.jackson.MongoDB;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;

import controllers.run.Lanes;
import fr.cea.ig.DBObject;


/**
 * Serialise/deserialise objects to/from Jackson annotated classes in MongoDB collection
 * Type Object who maps mongoDB collection with Jackson annotation must extend DBObject
 * 
 * @author mhaquell
 *
 */
public class Mongo {

	/**
     * Find an object to a collection by the given object
     *
     * @param collectionName : The DB collection
     * @param type : Type of object to map with collection
     * @param object : instance object to find in collection
     * @return The object
     * @throws MongoException If an error occurred
     */
	public static <T extends DBObject> T findById(String collectionName, Class<T> type, T object) throws MongoException{
		return getCollection(collectionName,type).findOneById(object._id);
	}
	
	/**
     * Find an object to a collection by the given id
     *
     * @param collectionName : The DB collection
     * @param _id  : the mongodb id
     * @return The object
     * @throws MongoException If an error occurred
     */
	public static <T> T findById(String collectionName, Class<T> type, String value) throws MongoException{
		return getCollection(collectionName,type).findOneById(value);
	}
	
	/**
     * Find an object to a collection by the given code
     *
     * @param collectionName : The DB collection
     * @param code  : the code
     * @return The object
     * @throws MongoException If an error occurred
     */
	public static <T> T findByCode(String collectionName, Class<T> type, String code) throws MongoException{
		Map<String,Object> props = new HashMap<String,Object>();
		props.put("code", code);
		
		List<T> results = find(collectionName, type, props);
		if(results.size() == 1){
			return results.get(0);
		}else if(results.size() > 1){
			throw new MongoException("more than one result for "+type+" with code "+code);
		}else{
			return null;
		}
	}
	

	/**
     * Return all objects of one collection
     *
     * @param collectionName : The DB collection
     * @param type : Type of object
     * @return  List of objects
     * @throws MongoException If an error occurred
     */
	public static  <T> List<T> all(String collectionName, Class<T> type) throws MongoException{
		return (List<T>) getCollection(collectionName,type).find().toArray();
	}

	
	/**
     * Return objects of one collection from list of search criteria 
	 * 
	 * @param collectionName : The DB collection
	 * @param type : Type of object to map with collection
	 * @param args : Map of search criteria, criteria are defined by key/value
	 * @return List of objects result
	 * @throws MongoException If an error occurred
	 * 
	 */
	public static  <T> List<T> find(String collectionName, Class<T> type,Map<String,Object> args) throws MongoException{
		Query query = null;

		for (String key :args.keySet()){

			if (args.get(key)!=null){

				if (query==null) {
					query=DBQuery.is(key, args.get(key));
				}else
					query=query.is(key,args.get(key));

			}

		}
		Logger.debug("DBQuery :"+ query.toString());
		return (List<T>) getCollection(collectionName,type).find(query).toArray();
	}
	
	/**
	 * Return objects of one collection from query
	 * 
	 * @param collectionName : The DB collection
	 * @param type : Type of object to map with collection
	 * @param query : query build
	 * @return List of objects result
 	 * @throws MongoException If an error occurred
	 */
	public static <T> List<T> find(String collectionName, Class<T> type,Query query) throws MongoException{
		
		Logger.debug("DBQuery :"+ query.toString());
		return (List<T>) getCollection(collectionName,type).find(query).toArray();
	}


	/**
	 * 
	 * Saves an object to this collection (does insert or update based on the object _id)
	 * 
	 * @param collectionName : The DB collection
	 * @param object : Object to save in collection
	 * 					 will add <code>_id</code> field if needed
	 * @return Object saved
	 * @throws MongoException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject> T save(String collectionName, T object) throws MongoException{
		Logger.debug("Insert object :"+ object.toString());
		return getCollection(collectionName,(Class<T>) object.getClass()).save(object).getSavedObject();

	}
	
	/**
     * Performs an update operation on the totality of object in a collection
	 * 
	 * @param collectionName : The DB collection
	 * @param object : The object to update
	 * @return : The Write result who get object(s) saved or error messages
	 * @throws MongoException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject,K> WriteResult<T, K>  update(String collectionName,T object) throws MongoException {
		Logger.debug("Update object :"+ object.toString());
		return (WriteResult<T, K>) getCollection(collectionName,(Class<T>) object.getClass()).updateById(object._id,object);

	}

	
	/**
	 * 
	 *  Performs a partial update operation to a object
	 *  Add key/value if key doesn't exist
	 *  Modify key/value if key exists 
	 *  Does not update other key
	 *  
	 * @param collectionName : The DB collection
	 * @param object : The object to update
	 * @param args : The map of key/value to update
	 * @return The Write result who gets object(s) saved or error messages
	 * @throws MongoException If an error occurred
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject, K> WriteResult<T, K> update(String collectionName,T object,Map<String,Object> args) throws MongoException{
		Logger.debug("Update oject :"+ object.toString());	
		
		Builder builder = new DBUpdate.Builder();

		for (String key :args.keySet()){

			if (args.get(key)!=null && !key.equals("id")){

					builder=builder.set(key, args.get(key));
			}

		}
		return (WriteResult<T, K>) getCollection(collectionName,(Class<T>) object.getClass()).updateById(object._id,builder);

	}
	
	/**
	 * 
	 * Performs an update operation on a object
	 * Add list objects to field
	 * Does not update other key
	 * 
	 * @param collectionName :The DB collection 
	 * @param object : The object to update
	 * @param field : Field to modify
	 * @param value : list objects to push on field
	 * @return The Write result who gets object(s) saved or error messages
	 * @throws MongoException  If an error occurred
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject, K> WriteResult<T, K> updateAddSet(String collectionName,T object,String field,Object... value) throws MongoException{
		Logger.debug("Update oject :"+ object.toString());	
		
		Builder builder = new DBUpdate.Builder();

		builder=builder.addToSet(field,value);

		return (WriteResult<T, K>) getCollection(collectionName,(Class<T>) object.getClass()).updateById(object._id,builder);

	}
	
	/**
	 * 
	 * Wraps a DB collection in a JacksonDBCollection from an object type 
	 * 
	 * @param collectionName : The DB collection
	 * @param type 				: The type of objects to map with DB collection 
	 * @return The Jackson wrapped collection 
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T, K> JacksonDBCollection<T, K> getCollection(String collectionName, Class<T> type){
		//Jackson à déjà un cache de collections
		return  (JacksonDBCollection<T, K>) MongoDB.getCollection(collectionName, type, String.class);
		
	}


	/**
	 * 
	 * Delete document in a MongoDB collection from an object 
	 * 
	 * @param collectionName : The DB collection
	 * @param object : The type of objects to map with DB collection 
	 * @throws MongoException If an error occurred
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <T extends DBObject> void delete(String collectionName, T object) throws MongoException {

			getCollection(collectionName,(Class<T>) object.getClass()).removeById(object._id);
	}

	/**
     * Delete document in a MongoDB collection from a id
     *
     * @param collectionName : The DB collection
 	 * @param type 	: The type of objects to map with DB collection 
     * @param id  : the mongodb id
     * @throws MongoException If an error occurred
     */
	public static <T extends DBObject> void delete(String collectionName, Class<T> type, String id) throws MongoException {
				getCollection(collectionName,type).removeById(id);
	}

	
	public static <T,K,J> void createOrUpdateInArray(String collectionName,T type,String refId ,String idValue, String arrayName, String arrayField, J arrayValue,K newValue)
	{
		@SuppressWarnings("unchecked")
		JacksonDBCollection<T, Object> coll = getCollection(collectionName,(Class<T>) type.getClass());
		Query object = DBQuery.is(refId, idValue).and(DBQuery.is(arrayName+"."+arrayField, arrayValue));
		if(coll.find(object).count()>=1){
			Builder build = DBUpdate.set(arrayName+".$", newValue); 
			coll.update(object, build);
		}
		else
		{
			Builder build = DBUpdate.addToSet(arrayName, newValue); 
			coll.update(object, build);
		}
	}
	
	public static void test(){
		JacksonDBCollection<Run, Object> coll = getCollection("cng.run.illumina2Yann",Run.class);
		//BasicDBObject query = new BasicDBObject("$set", new BasicDBObject("lanes.nbRead",4)); 
		coll.remove(new BasicDBObject());
		/*BasicDBObject object = new BasicDBObject("code","YANN_TEST2").append("lanes.number", 2);
		Builder build = new Builder();
		build.set("lanes.$.number", 222);*/
	
		/*Query object = DBQuery.is("code", "YANN_TEST2").and(DBQuery.is("lanes.readsets.code", "THECODEiiii"));
		Builder build = DBUpdate.set("lanes.$.readsets.$", null); 
		
		coll.update(object, build);
		DBCursor<Run> cur =  coll.find(object);
		Logger.info("TEST QUERY ::  "+cur.toArray().toString());
		for(int i=0;i<cur.toArray().size();i++)
			Logger.info("code= "+cur.toArray().get(i).code);*/
	}
	
}
