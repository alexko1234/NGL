package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jongo.MongoCursor;
import org.mongojack.DBQuery;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.mongo.MongoStreamer;
import play.libs.Json;
import views.components.datatable.DatatableForm;



public class ListForm extends DatatableForm {
	public Boolean list = Boolean.FALSE;
	public Boolean count = Boolean.FALSE;
	
	public Integer limit = 5000; //limit the number or element in the result

	public boolean reporting = Boolean.FALSE;
	public String reportingQuery;
	public boolean aggregate = Boolean.FALSE;
	
	/**
	 * Define how to return results from input form values.
	 * @return the function to transform results according to the selected mode.
	 */
	@JsonIgnore
	public <T extends DBObject> Function<MongoCursor<T>, Source<ByteString,?>> transformMongoCursor() {
		if(this.datatable) {
			return (cursor -> MongoStreamer.streamUDT(cursor));
		} else if(this.list) {
			return (cursor -> MongoStreamer.stream(cursor, conversion()));
		} else if(this.count) {
			return (cursor -> {
				Map<String, Integer> map = new HashMap<>(1);
				map.put("result", cursor.count());
				return MongoStreamer.stream(Json.toJson(map));
			} );
		} else {
			return (cursor -> MongoStreamer.stream(cursor));
		}
	}
	
	/**
	 * Define how to return results from input form values.
	 * @return the function to transform results according to the selected mode.
	 */
	@JsonIgnore
	public <T extends DBObject> Function<MongoDBResult<T>, Source<ByteString,?>> transformMongoDBResult() {
		if(this.datatable) {
			return (cursor -> MongoStreamer.streamUDT(cursor));
		} else if(this.list) {
			return (cursor -> MongoStreamer.stream(cursor, conversion()));
		} else if(this.count) {
			return (cursor -> {
				Map<String, Integer> map = new HashMap<>(1);
				map.put("result", cursor.count());
				return MongoStreamer.stream(Json.toJson(map));
			} );
		} else {
			return (cursor -> MongoStreamer.stream(cursor));
		}
	}

	/**
	 * Define the conversion done if the return mode selected is "list"
	 * @return a transform function
	 */
	@JsonIgnore
	public <T extends DBObject> Function<T, ?> conversion() {
		return o -> { return new ListObject(o.code, o.code); };
	}

	
	/**
	 * (have to be implemented into "concrete" xxSearchForm class).
	 * @return the query object
	 */
	@JsonIgnore
	public DBQuery.Query getQuery() {return null;}
}
