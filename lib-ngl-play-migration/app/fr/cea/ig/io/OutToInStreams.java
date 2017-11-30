package fr.cea.ig.io;

import java.io.*;

/**
 * Output stream that can be written to connected to an input stream that
 * can be read from.
 * 
 * @author vrd
 *
 */

// TODO: create a implement read/write buffers methods.

// The implementation of the input and ouput streams delegate the write and read calls
// to the enclosing OutToInStreams instance.
public class OutToInStreams {

	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(OutToInStreams.class);
	
	/**
	 * Size of the circular buffer.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	
	/*
	

    private Status status = Status.Waiting;

    private byte value;

    static enum Status {
    	Waiting,
    	Ready,
    	Closed
    }

	*/
	
	/**
	 * Byte buffer for the cilruclar buffer implementation. The valid
	 * offsets are modulo buffer length and greater or equal to start
	 * and less than end.
	 */
	private byte[] buffer;
	
	/**
	 * Index of start data.
	 */
	private int start;
	
	/**
	 * Index after last data.
	 */
	private int end;
	
    /**
     * Output stream. 
     */
    private Output output;
    
    /**
     * Input stream.
     */
    private Input  input;

    /**
     * Constructor. 
     */
    public OutToInStreams() {
    	this(DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Construct an instance with the specified buffer size.
     * @param bufferSize size in bytes of buffer to use
     */
    public OutToInStreams(int bufferSize) {
    	buffer = new byte[bufferSize];
    	output = new Output();
    	input  = new Input();
    	start = end = 0;
    }
    
    /**
     * Output stream.
     * @return output stream
     */
    public OutputStream getOutputStream() { 
    	return output; 	
    }
    
    /**
     * Input stream.
     * @return input stream
     */
    public InputStream  getInputStream()  { 
    	return input; 
    }
    
    /**
     * Available bytes to read from the buffer. 
     * @return available bytes to read from the buffer 
     */
    public int available() {
    	return end - start;
    }
        
    /**
     * Output stream write implementation.
     * @see java.io.OutputStream#write(int) 
     * @param i byte value to write
     * @throws IOException
     */
    public synchronized void write(int i) throws IOException {
    	byte b = (byte)(i&0xFF); // The byte is passed directly.
    	try {
    		while (true) {
    			if (end-start < buffer.length) {
    				buffer[end++ % buffer.length] = b;
    				notify();
    				return;
    			} else {
    				wait();
    			}
    		}
    	} catch (InterruptedException e) {
    		// Close the output stream as this kind of failure is pretty bad
    		output.closed = true;
    		throw new IOException("internal synch failed",e);
    	}
    }
    
    /**
     * Output stream byte buffer write implementation.
     * @see java.io.OutputStream#write(byte[],int,int)
     * @param buf buffer to write from
     * @param off offset of the data start
     * @param len length of the data
     * @throws IOException the stream is closed or a wait is interrupted
     */
    public synchronized void write(byte[] buf, int off, int len) throws IOException {
    	try {
    		while (true) {
				if (len == 0) 
					return;
    			// check if the buffer has some available space
				int available = buffer.length - end + start; 
    			if (available > 0) {
    				// How much can we write til the buffer end ? We start the
    				// write at (end % buffer.length).
    			    int wStart = end % buffer.length;
    			    int toEnd = Math.min(available,buffer.length - wStart);
    			    int wLen  = Math.min(toEnd, len);
    			    logger.debug("writing " + off + "/" + len + " to " + end + ":" + wStart + "/" + wLen);
    			    System.arraycopy(buf, off, buffer, wStart, wLen);
    			    notify();
    			    end += wLen;
    			    len -= wLen;
    			    off += wLen;
    			    available = buffer.length - end + start; 
    			} else {
    				wait();
    			}
    		}
    	} catch (InterruptedException e) {
    		// Close the output stream as this kind of failure is pretty bad
    		output.closed = true;
    		// Unlock the waiting input.
    		OutToInStreams.this.input.notify();
    		throw new IOException("internal synch failed",e);
    	}    	
    }
    
    /**
     * Implementation of the input stream byte read.
     * @see java.io.InputStream#read()
     * @return the read byte
     * @throws IOException stream is closed or a wait is interrupted
     */
    public synchronized int read() throws IOException {
    	try {
    		while (true) {
    			if (end > start) {
    				byte b = buffer[start++ % buffer.length];
    				notify();
    				return b;
    			} else if (output.closed) {
    				return -1;
    			} else {
    				wait();
    			}
    		}
    	} catch (InterruptedException e) {
    		input.closed = true;
    		throw new IOException("internal synch failed",e);
    	}
    }
    
    /**
     * Implementation of the byte buffer read of input streams.
     * @see java.io.InputStream#read(byte[], int, int) 
     * @param buf buffer to write read data to
     * @param off offset in the provided buffer to start the write at
     * @param len length of the data to read 
     * @return    number of read bytes
     * @throws IOException stream is closed or a wait is interrupted
     */
    public synchronized int read(byte[] buf, int off, int len) throws IOException {
    	try {
    		while (true) {
    			// Something to read
    			if (end > start) {
    				int available = end - start;
    				// read no more than len
    				int rLen = Math.min(available,len);
    				// Copy block using the buffer limits
    				int rStart = start % buffer.length;
    				int rEnd   = rStart + rLen;
    				logger.debug("read " + start + "/" + rLen + " to " + off + "/" + len);
    				if (rEnd >= buffer.length) {
    					int s0len = buffer.length - rStart;
    					System.arraycopy(buffer, rStart, buf,         off, s0len);
    					System.arraycopy(buffer,      0, buf, off + s0len, rLen - s0len);
    				} else {
    					System.arraycopy(buffer, rStart, buf, off, rLen);
    				}
    				start += rLen;
    				notify();
					return rLen;
    			} else if (output.closed) {
    				return -1;
    			} else {
    				wait();
    			}
    		}
    	} catch (InterruptedException e) {
    		input.closed = true;
    		// Close outout and wake waiters
    		throw new IOException("internal synch failed",e);
    	}
    }
   
    
    /*
    public synchronized int read() throws IOException {
    	try {
    		while (true) {
    			switch (status) {
    			case Waiting:
    				// logger.debug("read/waiting");
    				wait();
    				// logger.debug("read/wating done");
    				break;
    			case Ready:
    				// logger.info("read/ready");
    				notify();
    				status = Status.Waiting;
    				// 0-255 range
    				// System.out.println("bridge read " + value + " -> " + status);
    				return value + 128;
    			case Closed:
    				// logger.info("read/end");
    				return -1;
    			}
    		}
    	} catch (InterruptedException e) {
    		throw new IOException("internal synch failed",e);
    	}
    }
*/
    
    /*
    // Closes the output stream
    public synchronized void close() throws IOException {
    	try {
    		while (true) {
    			switch (status) {
    			case Waiting:
    				status = Status.Closed;
    				notify();
    				return;
    			case Ready:
    				wait();
    				break;
    			case Closed:
    				notify();
    				return;
    			}
    		}
    	} catch (InterruptedException e) {
    		throw new IOException("internal synch failed",e);
    	}
    }
    */
 
    /**
     * Output stream that writes to the OutToInStreams instance.
     * 
     * @author vrd
     *
     */
    class Output extends OutputStream {

    	/**
    	 * Is this stream closed ?
    	 */
    	private boolean closed;
    	
    	/**
    	 * Constructor.
    	 */
    	private Output() {
    		closed = false;
    	}
    	
    	// Default implementations rely on the write byte method
    	/*
    	public void write(byte[] b) throws IOException {
    		if (closed)
    			throw new IOException("stream closed");
    		write(b,0,b.length);
    	}
    	public void write(byte[] b, int off, int len) throws IOException {
    		for (int i=0; i<len; i++)
    			write(b[off+i]);
    	}
    	*/
    	
    	@Override
    	public void write(int b) throws IOException {
    		if (closed)
    			throw new IOException("stream closed");
    		if (OutToInStreams.this.output.closed)
    			throw new IOException("output has been closed");
    		OutToInStreams.this.write(b);
    	}
    	
    	public void write(byte[] b, int off, int len) throws IOException {
    		OutToInStreams.this.write(b,off,len);
    	}
    	
    	/**
    	 * Close the stream so that no more data can be written.
    	 */
    	@Override
    	public void 	close() throws IOException {
    		// OutToInStreams.this.close();
    		closed = true;
    	}
    	    	
    }

    /**
     * Input stream that reads data from the OutToInStreams instance.
     * 
     * @author vrd
     *
     */
    class Input extends InputStream {
        
    	/**
    	 * Is this stream closed ?
    	 */
    	private boolean closed;
    	
    	/**
    	 * Constructor.
    	 */
        private Input() {
        	closed = false;
        }
        
        /**
         * Returns the number of available byte in the OutToInStreams instance.
         */
        @Override
        public int available() {
        	return OutToInStreams.this.available();
        }
        
        /**
         * Closes the streams so no data can be read from it.
         */
        @Override
        public void close() throws IOException {
        	logger.info("input/close");
        	closed = true;
        }
        
        // see parent class for method comments
        
        @Override
        public int read() throws IOException {
        	if (closed)
        		throw new IOException("stream closed");
        	return OutToInStreams.this.read();
        }
        
        /*
        @Override
        public int read(byte[] b) throws IOException {
        	if (closed)
        		throw new IOException("stream closed");
        	return read(b,0,b.length);
        }
*/
        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
        	if (closed)
        		throw new IOException("stream closed");
        	return OutToInStreams.this.read(buf,off,len);
        }
        
        /*
        // This is the basic implementation that does the byte read like the
        // default method.
        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
        	if (closed)
        		throw new IOException("stream closed");
        	int b;
        	for (int i=0; i<len; i++) {
        		b = read();
        		if (b == -1) {
        			if (i == 0) {
        				logger.info("input/read end -1");
        				return -1;
        			}
        			logger.info("input/read partial " + i);
        			return i;
        		}
        		buf[off + i] = (byte)(b-128);
        	}
        	logger.info("input/read full " + len);
        	return len;
        }
        */
        
        // Use default methods that implements unsupported marking 
        // public boolean 	markSupported() { return false; }
        // public void mark(int readlimit) { throw new UnsupportedOperationException(); }
        // public void 	reset() { throw new UnsupportedOperationException(); }

    }

}




/*
public class Streams {

    public static void main(String[] args) throws Exception {
	System.out.println("running streams");

	// Create the stream pair.
	BridgeStream bridge = new BridgeStream();
	InputStream is = new IS(bridge);
	OutputStream os = new OS(bridge);
	
	// Reader thread
	new Thread(new Runnable() {
		public void run() {
		    try {
			BufferedReader r = new BufferedReader(new InputStreamReader(is),128);
			String s;
			while ((s = r.readLine()) != null)
			    System.out.println("<< " + s);
			System.out.println("no more data to read");
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }).start();

	// Writer thread
	new Thread(new Runnable() {
		public void run() {
		    try {
			PrintWriter p = new PrintWriter(os);
			for (int i=0;i<100; i++) {
			    String s = "message " + i;
			    System.out.println(">> " + s); 
			    p.println(s);
			    p.flush();
			    Thread.sleep(100);
			}
			p.close();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }).start();
	
    }
    
}
*/
