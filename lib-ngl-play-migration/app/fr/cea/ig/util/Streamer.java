package fr.cea.ig.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

// import org.jongo.MongoCursor;

// import play.libs.Json;

// import fr.cea.ig.DBObject;
// import fr.cea.ig.MongoDBResult;

// Logger aside this has no play dependency.

/**
 * Streamer is an output stream writer that provide the written data
 * as an input stream.
 * 
 * @author vrd
 * 
 */
public class Streamer {

	/**
	 * Data streamer.
	 */
	public interface IStreamer {
		
		/**
		 * Stream to a given output stream. 
		 * @param o output stream to stream to
		 * @throws IOException output stream exception
		 */
		public void streamTo(OutputStream o) throws IOException;
		
	}

	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(Streamer.class);
	
	/**
	 * Input stream to return to callers.
	 */
	private PipedInputStream in;
	
	/**
	 * Output stream for streamer to write to.
	 */
	private PipedOutputStream out;
	
	/**
	 * Thread that runs the IStreamer writes. 
	 */
	private Thread thread;
	
	/**
	 * Data streamer that writes to output stream.
	 */
	IStreamer streamer;
	
	/**
	 * Builds a Streamer that uses the provided IStreamer to write data.
	 * @param streamer data writer
	 */
	public Streamer(IStreamer streamer) {
		this.streamer = streamer;
		in = new PipedInputStream();
		try {
			out = new PipedOutputStream(in);
		} catch (IOException e) {
			throw new RuntimeException("streamer initialization failed",e);
		}
	}
	
	/**
	 * Input stream.
	 * @return input stream to read IStreamer writes from
	 */
	public InputStream inputStream() {
		// Do not start the write process twice
		if (thread == null) {
			thread = new Thread(new Runnable() {
				public void run() {
					try {
						streamer.streamTo(out);
					} catch (IOException e) {
						logger.error("stream error",e);
					}
				}
			});
			thread.start();
		}
		return in;
	}

	/**
	 * Not so short shortcut
	 * @param streamer data streamer
	 * @return stream to read streamed data from
	 */
	public static InputStream stream(IStreamer streamer) {
		return new Streamer(streamer).inputStream();
	}

}
