package fr.cea.ig.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
// import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Optional;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.io.OutToInStreams;
import play.mvc.Result;
import play.http.HttpEntity;


// Logger aside this has no play dependency.

// TODO: move this class to fr.cea.ig.io

/**
 * Streamer is an output stream writer that provide the written data
 * as an input stream.
 * 
 * @author vrd
 * 
 */
public class Streamer {

	
	/*static void f() {
		Source<ByteString, ?> source = Source.<ByteString>actorRef(256, OverflowStrategy.dropNew())
				  .mapMaterializedValue((ActorRef sourceActor) -> {
				    sourceActor.tell(ByteString.fromString("hello"), null);
				    sourceActor.tell(ByteString.fromString("world"), null);
				    sourceActor.tell(new Status.Success(NotUsed.getInstance()), null);
				    return null;
				  });
	}*/
	
	
	/**
	 * Data streamer.
	 */
	//public interface IStreamer {
		
		/**
		 * Stream to a given output stream. 
		 * @param o output stream to stream to
		 * @throws IOException output stream exception
		 */
		/*public void streamTo(ActorRef o);
		
		static void write(ActorRef a, String s) {
			a.tell(ByteString.fromString(s),null);
		}
		
	}*/

	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(Streamer.class);
	
	public static Result okStream(Source<ByteString, ?> source) {
		 return new Result(200, HttpEntity.chunked(source, Optional.of("application/json")));
	}
	//public static Result okStream(Source<String, ?> source) {
	//	 return new Result(200, HttpEntity.chunked(source.map(r -> { return ByteString; }), Optional.of("application/json")));
	//}
	
	/*
	public static Result okStream(IStreamer s) {
		return okStream(stream(s));
	}
	
	public static Source<ByteString, ?> stream(IStreamer streamer) {
		return stream(256,streamer);
	}
	
	public static Source<ByteString, ?> stream(int bufferSize, IStreamer streamer) {
		return Source.<ByteString>actorRef(bufferSize, OverflowStrategy.dropNew())
		  .mapMaterializedValue((ActorRef sourceActor) -> {
		    streamer.streamTo(sourceActor);
		    sourceActor.tell(new Status.Success(NotUsed.getInstance()), null);
		    return null;
		  });
	}
	*/
}
