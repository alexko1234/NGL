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
// import fr.cea.ig.io.OutToInStreams;
import play.mvc.Result;
import play.http.HttpEntity;


/**
 * Source to HTTP result conversion. Only one method left.
 * 
 * @author vrd
 * 
 */
public class Streamer {

	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(Streamer.class);
	
	/**
	 * Source to chunked HTTP response.
	 * @param source source to send
	 * @return       OK HTTP result
	 */
	public static Result okStream(Source<ByteString, ?> source) {
		 return new Result(200, HttpEntity.chunked(source, Optional.of("application/json")));
	}

}
