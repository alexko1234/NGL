package fr.cea.ig.mongo;

import java.util.Iterator;
import java.util.function.Function;

import org.jongo.MongoCursor;

import akka.stream.javadsl.Source;
import akka.util.ByteString;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.util.Streamer;

import play.libs.Json;
import play.mvc.Result;

/**
 * Mongo HTTP streaming utility methods. Methods apply JSON to String 
 * conversion.
 * The T extends DBObject is not needed, the real constraint is that T
 * produces some valid JSON representation.
 * 
 * Conversion from sources to bytestring akka sources that represent 
 * JSON data is probably not much related to Mongo anymore. 
 * 
 * @author vrd
 *
 */

// Direct transform apply does not compile. 
// return stream(source.map(transform));
// Despite what eclipse says, explicit source type compiles
// Source<R,?> rsource = source.map(transform);
// I blame the weak type inference of javac at the moment.

public class MongoStreamer {
	
	/*
	 * Logger.
	 */
	// private static final play.Logger.ALogger logger = play.Logger.of(MongoStreamer.class);
	
	// 
	// --------------------- MongoCursor<T> overloads ---------------
	//
	
	/**
	 * Cursor to full JSON list.
	 * @param <T>    Mongo collection element type
	 * @param cursor cursor for the full collection
	 * @return       JSON array formatted source
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoCursor<T> cursor) {
		return stream(Source.from(cursor));
	}

	/**
	 * Cursor to JSON formatted list, applying the given transformation to each
	 * collection element.
	 * @param <T>       Mongo collection element type
	 * @param <R>       transformed element type
	 * @param cursor    Mongo cursor to get elements from
	 * @param transform transformation to apply to elements
	 * @return          JSON array formatted source 
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> stream(MongoCursor<T> cursor, Function<T,R> transform) {
		return stream(Source.from(cursor),transform);
	}
	
	
	/**
	 * Cursor to JSON UDT formatted source.
	 * @param <T> Mongo collection element type
	 * @param cursor cursor to fetch elements from
	 * @return       JSON array formatted source
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoCursor<T> cursor) {
		return streamUDT(cursor.count(),Source.from(cursor));
	}
	
	// 
	// --------------------- MongoDBResult<T> overloads ---------------
	//
	
	/**
	 * MongoDBResult to JSON formatted source.
	 * @param <T>    mongo collection element type
	 * @param result result to get elements from
	 * @return       JSON array formatted source
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoDBResult<T> result) {
		return stream(Source.from(result.cursor));
	}
	
	/**
	 * MongoDBResult to full JSON formated source applying a transform
	 * to the result elements. 
	 * @param <T>       source element type
	 * @param <R>       transformed element type
	 * @param result    result to get elements from
	 * @param transform element transformation
	 * @return          JSON formatted array 
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> stream(MongoDBResult<T> result, Function<T,R> transform) {
		return stream(Source.from(result.cursor), transform);
	}

	/**
	 * MongoDBResult to UDT json list.
 	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoDBResult<T> all) {
		return streamUDT(all.count(), Source.from(all.cursor));
	}
	
	/**
	 * MongoDBResult are applied a given transform and streamed as UDT json list.
 	 * @param <T>       mongo collection element type
 	 * @param <R>       transformed element type
	 * @param data      cursor for the full collection
	 * @param transform transform to apply to result elements
	 * @return          input stream that provide a json list of transformed collection objects
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> streamUDT(MongoDBResult<T> data, Function<T,R> transform) {
		return streamUDT(data.count(), Source.from(data.cursor), transform);
	}
	
	//
	// --------------------- Source<T,?> overloads -------------------------------------
	// Those methods are actual implementations.
	
	/**
	 * Source conversion from a source of JSON ready objects to a stream ready source.
	 * @param <T>    source element type    
	 * @param source source to build output from
	 * @return       JSON source
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
	 * @param <T>       source element type
	 * @param <R>       result element type
	 * @param source    source to create JSON output from
	 * @param transform source element transformation
	 * @return          JSON formatted source
	 */
	public static <T,R> Source<ByteString, ?> stream(Source<T,?> source, Function<T,R> transform) {
		return stream(source.map(x -> { return transform.apply(x); }));
	}

	/**
	 * Already counted source to UDT conversion.
	 * @param <T>    source element type
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
 	 * @param <T>       source element type
 	 * @param <R>       transformed element type
	 * @param count     element count
	 * @param source    element source
	 * @param transform element transform
	 * @return          HTTP UDT formatted source
	 */
	public static <T,R> Source<ByteString, ?> streamUDT(int count, Source<T, ?> source, Function<T,R> transform) {
		return streamUDT(count, source.map(x -> { return transform.apply(x); }));
	}
		
	// Iterator/iterable overloads
	public static <T> Source<ByteString, ?> stream(Iterable<T> all) {
		return stream(Source.from(all));
	}
	
	public static <T> Source<ByteString, ?> stream(Iterator<T> all) {
		return stream(Source.from(() -> { return all; }));
	}
	
	// ---------------------- HTTP Result overloads ------------------------
	// Yay ! More overloads
	// Those overloads are shortcuts to http ok results with a chunked content.
	
	// TODO: possibly fix names as this is okChunked and not okStream
	
	public static <T extends DBObject> Result okStream(MongoDBResult<T> all) {
		return Streamer.okStream(stream(all)); 
	}
	public static <T extends DBObject,R> Result okStream(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream(stream(all,transform)); 
	}
	public static <T extends DBObject> Result okStreamUDT(MongoDBResult<T> all) {
		return Streamer.okStream(streamUDT(all)); 
	}
	public static <T extends DBObject,R> Result okStreamUDT(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream(streamUDT(all,transform)); 
	}
	public static <T extends DBObject> Result okStreamUDT(MongoCursor<T> all) {
		return Streamer.okStream(streamUDT(all)); 
	}
	public static <T extends DBObject> Result okStream(MongoCursor<T> all) {
		return Streamer.okStream(stream(all)); 
	}
	public static <T> Result okStream(Iterator<T> all) {
		return Streamer.okStream(stream(all)); 
	}
	public static <T> Result okStream(Iterable<T> all) {
		return Streamer.okStream(stream(all)); 
	}
	public static <T,R> Result okStream(Source<T, ?> source, Function<T,R> transform) {
		return Streamer.okStream(stream(source,transform));
	}
	
}
