package fr.cea.ig.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.function.Function;

// import static fr.cea.ig.util.Streamer.stream;
import fr.cea.ig.util.Streamer.IStreamer;
import fr.cea.ig.util.Streamer;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBResult;

import org.jongo.MongoCursor;

import play.libs.Json;

/**
 * Mongo streaming utility methods.
 * 
 * @author vrd
 *
 */
public class MongoStreamer {

	// TODO: The original method should be used
	/**
	 * Streamer as an input stream. 
	 * @param streamer streamer to use
	 * @return input stream
	 */
	public static InputStream stream(IStreamer streamer) {
		return Streamer.stream(streamer);
	}
	
	/**
	 * Cursor to full json list.
	 * @param all cursor for the full collection
	 * @return input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> InputStream stream(MongoCursor<T> all) {
		return stream(new IStreamer() {
			@Override
			public void streamTo(OutputStream _out) throws IOException {
				PrintWriter out = new PrintWriter(_out);
				Iterator<T> iter = all.iterator();
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		});
	}

	
	/**
	 * Cursor to UDT json list.
	 * @param all cursor for the full collection
	 * @return input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> InputStream streamUDT(MongoCursor<T> all) {
		return stream(new IStreamer() {
			@Override
			public void streamTo(OutputStream _out) throws IOException {
				PrintWriter out = new PrintWriter(_out);
				out.write("{\"recordsNumber\":"+all.count()+",");
			    out.write("\"data\":[");
			    Iterator<T> iter = all.iterator();
			    while(iter.hasNext()){
			    	out.write(Json.toJson(iter.next()).toString());
		            if (iter.hasNext()) out.write(",");    	
			    }
			    out.write("]}");
			    out.close();				
			}
		});
	}
	
	/**
	 * MongoDBResult to full json list.
	 * @param all cursor for the full collection
	 * @return input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> InputStream stream(MongoDBResult<T> all) {
		return stream(new IStreamer() {
			@Override
			public void streamTo(OutputStream _out) throws IOException {
				PrintWriter out = new PrintWriter(_out);
				Iterator<T> iter = all.cursor;
		    	out.write("[");
			    while (iter.hasNext()) {
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");
		        }					
		        out.write("]");
			    out.close();					
			}
		});
	}
	
	/**
	 * MongoDBResult to UDT json list.
	 * @param all cursor for the full collection
	 * @return input stream that provide a json list of collection objects
	 */
	public static <T extends DBObject> InputStream streamUDT(MongoDBResult<T> all) {
		return stream(new IStreamer() {
			@Override
			public void streamTo(OutputStream _out) throws IOException {
				PrintWriter out = new PrintWriter(_out); 
				out.write("{\"recordsNumber\":"+all.count()+",");
			    out.write("\"data\":[");
			    Iterator<T> iter = all.cursor;
			    while(iter.hasNext()){
			    	out.write(Json.toJson(iter.next()).toString());
		            if(iter.hasNext())out.write(",");    	
			    }
			    out.write("]}");
			    out.close();				
			}
		});

	}
	
	/**
	 * MongoDBResult are applied a given transform and streamed as UDT json list.
	 * @param all cursor for the full collection
	 * @param transform transform to apply to result elements
	 * @return input stream that provide a json list of transformed collection objects
	 */
	public static <T extends DBObject,R> InputStream streamUDT(MongoDBResult<T> data, Function<T,R> transform) {
		return stream(new IStreamer() {
			public void streamTo(OutputStream _out) {
				PrintWriter out = new PrintWriter(_out);
				out.write("{\"recordsNumber\":"+data.count()+",");
				out.write("\"data\":[");
				while (data.cursor.hasNext()) {
					Object r = transform.apply(data.cursor.next());
					if (null != r) {
						out.write(Json.toJson(r).toString());
						if (data.cursor.hasNext()) out.write(",");
						//Logger.info(Json.toJson(results.cursor.next()).toString()+",");
					}		    	
				}
				out.write("]}");
				out.close();
			}
		});
	}
	
}
