package fr.cea.ig.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
// import java.io.PrintWriter;
import java.util.Iterator;

import fr.cea.ig.io.OutToInStreams;

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
	 * Streams.
	 */
	private OutToInStreams ois;
	
	/**
	 * Thread that runs the IStreamer writes. 
	 */
	private Thread thread;
	
	/**
	 * Data streamer that writes to output stream.
	 */
	IStreamer streamer;
	
	/**
	 * Default buffer size.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 16 * 1024;
	
	/**
	 * Builds a Streamer that uses the provided IStreamer to write data.
	 * @param streamer data writer
	 */
	public Streamer(IStreamer streamer) {
		this(DEFAULT_BUFFER_SIZE,streamer);
	}
	
	/**
	 * Builds a Streamer that uses the provided IStreamer to write data.
	 * @param bufferSize IO buffer size
	 * @param streamer   data writer
	 */
	public Streamer(int bufferSize, IStreamer streamer) {
		this.streamer = streamer;
		ois = new OutToInStreams(bufferSize);
	}
	
	/**
	 * Starts the write thread and returns the input stream to read from.
	 * @return input stream to read IStreamer writes from
	 */
	public InputStream inputStream() {
		// Do not start the write process twice
		if (thread == null) {
			thread = new Thread(new Runnable() {
				public void run() {
					try {
						streamer.streamTo(ois.getOutputStream());
						logger.debug("streaming of " + streamer + " done");
					} catch (IOException e) {
						logger.error("stream error",e);
					}
				}
			});
			thread.start();
		}
		return ois.getInputStream();
	}

	/**
	 * Not so short shortcut
	 * @param streamer data streamer
	 * @return stream to read streamed data from
	 */
	public static InputStream stream(IStreamer streamer) {
		return new Streamer(streamer).inputStream();
	}

	public static InputStream stream(int bufferSize, IStreamer streamer) {
		return new Streamer(bufferSize,streamer).inputStream();
	}

}
