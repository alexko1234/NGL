package fr.cea.ig.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.function.Function;

// import static fr.cea.ig.util.Streamer.stream;
//import fr.cea.ig.util.Streamer.IStreamer;
//import static fr.cea.ig.util.Streamer.IStreamer.write;
import fr.cea.ig.util.Streamer;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBResult;

import org.jongo.MongoCursor;

import akka.actor.ActorRef;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.mvc.Result;

/**
 * Mongo streaming utility methods. Methods apply JSON to String 
 * conversion.
 * 
 * @author vrd
 *
 */
public class MongoStreamer {
	
	/**
	 * Logger.
	 */
	static ALogger logger = Logger.of(MongoStreamer.class);
	
	// 
	// --------------------- MongoCursor<T> overloads ---------------
	//
	
	/**
	 * Cursor to full json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoCursor<T> all) {
		return stream(Source.from(all));
	}

	
	/**
	 * Cursor to UDT json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoCursor<T> all) {
		return streamUDT(all.count(),Source.from(all));
	}
	
	// 
	// --------------------- MongoDBResult<T> overloads ---------------
	//
	
	/**
	 * MongoDBResult to full json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoDBResult<T> all) {
		return stream(Source.from(all.cursor));
	}
	
	/**
	 * MongoDBResult to full json list, the transform is used to convert the 
	 * dbobjects to something that is Json convertible.
	 * @param all       collection to transform
	 * @param transform element transformation
	 * @return          JSON formatted array 
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> stream(MongoDBResult<T> all, Function<T,R> transform) {
		return stream(Source.from(all.cursor),transform);
	}


	/**
	 * MongoDBResult to UDT json list.
 	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoDBResult<T> all) {
		return streamUDT(all.count(),Source.from(all.cursor));
	}
	
	/**
	 * MongoDBResult are applied a given transform and streamed as UDT json list.
 	 * @param <T>       mongo collection element type
	 * @param data      cursor for the full collection
	 * @param transform transform to apply to result elements
	 * @return          input stream that provide a json list of transformed collection objects
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> streamUDT(MongoDBResult<T> data, Function<T,R> transform) {
		/*return Source.from(data.cursor)
				.map(r -> { return transform.apply(r); }) // why map(transform) doesn't work ?
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("{\"recordsNumber\":" + data.count() + ",\"data\":[", ",", "]}")
				.map(r -> { return ByteString.fromString(r); });*/
		return streamUDT(data.count(),Source.from(data.cursor),transform);
	}
	
	
	//
	// --------------------- Source<T,?> overloads -------------------------------------

	/**
	 * Source conversion from a source of JSON ready objects to a stream ready source.
	 * @param  source source to build output from
	 * @return JSON source
	 */
	public static <T> Source<ByteString, ?> stream(Source<T,?> source) {
		return source
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("[", ",", "]")
				.map(r -> { return ByteString.fromString(r); }); 		
	}

	/**
	 * Create a stream ready source (JSON array formatted) from a source that 
	 * is transformed using the given transform. This is the same as the caller
	 * calling map on the source:
	 * <code>
	 *   stream(source,transform)
	 *   stream(source.map(transform))
	 * </code> 
	 * @param source    source to create JSON output from
	 * @param transform source element transformation
	 * @return          JSON formatted source
	 */
	public static <T,R> Source<ByteString, ?> stream(Source<T,?> source, Function<T,R> transform) {
		return stream(source.map(x -> { return transform.apply(x); }));
	}

	/**
	 * Already counted source to UDT conversion.
	 * @param count  collection size
	 * @param source collection as source
	 * @return       UDT JSON source 
	 */
	public static <T> Source<ByteString, ?> streamUDT(int count, Source<T, ?> source) {
		return source
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("{\"recordsNumber\":" + count + ",\"data\":[", ",", "]}")
				.map(r -> { return ByteString.fromString(r); });
	}

	/**
 	 * Already counted source to UDT conversion using a transformed source.
	 * @param count
	 * @param source
	 * @param transform
	 * @return
	 */
	public static <T,R> Source<ByteString, ?> streamUDT(int count, Source<T, ?> source, Function<T,R> transform) {
		return streamUDT(count,source.map(x -> { return transform.apply(x); }));
	}
	
	
	// Iterator/iterable overloads
	public static <T extends DBObject> Source<ByteString, ?> stream(Iterable<T> all) {
		return stream(Source.from(all));
	}
	
	public static <T extends DBObject> Source<ByteString, ?> stream(Iterator<T> all) {
		return stream(Source.from(() -> { return all; }));
	}
	
	// Yay ! More overloads
	
	public static <T extends DBObject,R> Result okStream(MongoDBResult<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all)); 
	}
	public static <T extends DBObject,R> Result okStream(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all,transform)); 
	}
	public static <T extends DBObject> Result okStreamUDT(MongoDBResult<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all)); 
	}
	public static <T extends DBObject,R> Result okStreamUDT(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all,transform)); 
	}
	public static <T extends DBObject> Result okStreamUDT(MongoCursor<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all)); 
	}
	public static <T extends DBObject,R> Result okStream(MongoCursor<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all)); 
	}
	public static <T extends DBObject> Result okStream(Iterator<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all)); 
	}
	public static <T extends DBObject> Result okStream(Iterable<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all)); 
	}
	
	public static <T,R> Result streamOk(Source<T, ?> source, Function<T,R> transform) {
		return Streamer.okStream(stream(source,transform));
	}
}
