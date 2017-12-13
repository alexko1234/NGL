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
 * Mongo streaming utility methods.
 * 
 * @author vrd
 *
 */
public class MongoStreamer {
	static ALogger logger = Logger.of(MongoStreamer.class);
	/**
	 * Streamer as an input stream. 
	 * @param streamer streamer to use
	 * @return input stream
	 */
	//public static Source<ByteString, ?> stream(IStreamer streamer) {
	//	return Streamer.stream(streamer);
	//}
	
	/**
	 * Cursor to full json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoCursor<T> all) {
		/*return stream(new IStreamer() {
			@Override
			public void streamTo(OutputStream _out) throws IOException {
				logger.debug("start stream.streamTo");
				PrintWriter out = new PrintWriter(_out);
				Iterator<T> iter = all.iterator();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) out.write(",");
		        }					
		        out.write("]");
			    out.close();	
			    logger.debug("end stream.streamTo");
			}
			public void streamTo(ActorRef out) {
				Iterator<T> iter = all.iterator();
		    	write(out,"[");
			    while (iter.hasNext()) {
			    	write(out,Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) write(out,",");
		        }					
		        write(out,"]");					
			}
		});*/
		return Source.from(all)
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("[", ",", "]")
				.map(r -> { return ByteString.fromString(r); }); 
	}

	
	/**
	 * Cursor to UDT json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoCursor<T> all) {
		return Source.from(all)
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("{\"recordsNumber\":" + all.count() + ",\"data\":[", ",", "]}")
				.map(r -> { return ByteString.fromString(r); }); 
	}
	
	/**
	 * MongoDBResult to full json list.
	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> stream(MongoDBResult<T> all) {
		return stream((Iterable<T>)all.cursor);
	}
	
	public static <T extends DBObject> Source<ByteString, ?> stream(Iterable<T> all) {
		return stream(Source.from(all));
	}
	public static <T extends DBObject> Source<ByteString, ?> stream(Iterator<T> all) {
		return stream(Source.from(() -> { return all; }));
	}
	public static <T extends DBObject> Source<ByteString, ?> stream(Source<T,?> source) {
		return source
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("[", ",", "]")
				.map(r -> { return ByteString.fromString(r); }); 		
	}
	
	
	public static <T extends DBObject,R> Source<ByteString, ?> stream(MongoDBResult<T> all, Function<T,R> transform) {
		return Source.from(all.cursor)
				.map(r -> { return Json.toJson(transform.apply(r)).toString(); })
				.intersperse("[", ",", "]")
				.map(r -> { return ByteString.fromString(r); }); 
	}
	

	
	/**
	 * MongoDBResult to UDT json list.
 	 * @param <T> mongo collection element type
	 * @param all cursor for the full collection
	 * @return    input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> Source<ByteString, ?> streamUDT(MongoDBResult<T> all) {
		return Source.from(all.cursor)
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("{\"recordsNumber\":" + all.count() + ",\"data\":[", ",", "]}")
				.map(r -> { return ByteString.fromString(r); }); 
	}
	
	/**
	 * MongoDBResult are applied a given transform and streamed as UDT json list.
 	 * @param <T>       mongo collection element type
	 * @param data      cursor for the full collection
	 * @param transform transform to apply to result elements
	 * @return          input stream that provide a json list of transformed collection objects
	 */
	public static <T extends DBObject,R> Source<ByteString, ?> streamUDT(MongoDBResult<T> data, Function<T,R> transform) {
		return Source.from(data.cursor)
				.map(r -> { return transform.apply(r); }) // why map(transform) doesn't work ?
				.map(r -> { return Json.toJson(r).toString(); })
				.intersperse("{\"recordsNumber\":" + data.count() + ",\"data\":[", ",", "]}")
				.map(r -> { return ByteString.fromString(r); }); 
	}
	
	public static <T extends DBObject,R> Result okStreamUDT(MongoDBResult<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all)); 
	}
	public static <T extends DBObject,R> Result okStreamUDT(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all,transform)); 
	}
	public static <T extends DBObject,R> Result okStreamUDT(MongoCursor<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)streamUDT(all)); 
	}
	public static <T extends DBObject,R> Result okStream(MongoDBResult<T> all) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all)); 
	}
	public static <T extends DBObject,R> Result okStream(MongoDBResult<T> all, Function<T,R> transform) {
		return Streamer.okStream((Source<ByteString, ?>)stream(all,transform)); 
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
	
}
