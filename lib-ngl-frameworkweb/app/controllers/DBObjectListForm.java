package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import org.mongojack.DBQuery;

import com.fasterxml.jackson.annotation.JsonIgnore;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.DBObject;
import fr.cea.ig.mongo.MongoStreamer;
import play.libs.Json;



public class DBObjectListForm<T extends DBObject> extends ListForm {

	/**
	 * Define the conversion done if the return mode selected is "list"
	 * @return a transform function
	 */
	@JsonIgnore
	public /*<T extends DBObject>*/ Function<T, ListObject> conversion() {
		return o -> { return new ListObject(o.code, o.code); };
	}

	
	/**
	 * (have to be implemented into "concrete" xxSearchForm class).
	 * @return the query object
	 */
	@JsonIgnore
	public DBQuery.Query getQuery() {return null;}

	
	/**
	 * Define how to return results from input form values.
	 * @return the function to transform results according to the selected mode.
	 */
	@JsonIgnore
	public /*<T extends DBObject>*/ Function<Iterable<T>, Source<ByteString, ?>> transform() {
		if(this.datatable) {
			return (iterable -> MongoStreamer.streamUDT_(iterable, (obj -> Json.toJson(obj).toString())));
		} else if(this.list) {
			return (iterable -> MongoStreamer.stream(iterable, conversion()));
		} else if(this.count) {
			return (iterable -> {
				Map<String, Integer> map = new HashMap<>(1);
				Iterator<T> iterator = iterable.iterator();
				int size = 0;
				while(iterator.hasNext()) {
					size++;
					iterator.next();
				}
				map.put("result", size);
				return MongoStreamer.stream(Json.toJson(map));
			} );
		} else {
			return (cursor -> MongoStreamer.stream(cursor));
		}
	}
}
