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


    
    public OutToInStreams() {
    	this(DEFAULT_BUFFER_SIZE);
    }
    
    public OutToInStreams(int bufferSize) {
    	buffer = new byte[bufferSize];
    	// This will disappear
    	// status = Status.Waiting;
    	output = new Output();
    	input  = new Input();
    	start = end = 0;
    }
    
    /**
     * Output stream that writes to the OutToInStreams instance.
     * 
     * @author vrd
     *
     */
    class Output extends OutputStream {

    	/**
    	 * Is the stream closed ?
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
    		OutToInStreams.this.write(b);
    	}
    	/*
    	public void write(byte[] b, int off, int len) throws IOException {
    		for (int i=0; i<len; i++)
    			OutToInStreams.this.write(b,off,len);
    	}
   	*/
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
    	 * Is the stream closed ?
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

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
        	if (closed)
        		throw new IOException("stream closed");
        	return OutToInStreams.this.read(buf,off,len);
        }
        */
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

    
    public OutputStream getOutputStream() { return output; }
    public InputStream  getInputStream()  { return input; }
    
    public int available() {
    	/*if (status == Status.Ready)
    		return 1;
    	return 0;*/
    	return end - start;
    }
    
    // Those are in fact reads and write on a single value that is
    // tutorial level stuff. The status representation way be quite a bad idea.
    // This must be synchronized otherwise the read and write may conflict
    // when accessing the buffer.
    /*
    public synchronized void write(int i) throws IOException {
    	byte b = (byte)(i&0xFF); // The byte is passed directly.
    	try {
    		while (true) {
    			// This supplies the value and notify the waiter.
    			switch (status) {
    			// Easy case
    			case Waiting:
    				value = b;
    				status = Status.Ready;
    				notify();
    				// System.out.println("bridge write " + value + " -> " + status);
    				return;
    			case Ready:
    				wait();
    				break;
    			case Closed:
    				throw new IOException("write on closed stream");
    			}
    		}
    	} catch (InterruptedException e) {
    		throw new IOException("internal synch failed",e);
    	}
    }
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
    
    public synchronized void write(byte[] buf, int off, int len) throws IOException {
    	try {
    		while (true) {
    			// check if the buffer has some available space
				int available = buffer.length - end + start; 
    			if (available > 0) {
    				// How much can we write til the buffer end ? We start the
    				// write at end % buffer.length.
    			    int wStart = end % buffer.length;
    			    int toEnd = Math.min(available,buffer.length - wStart);
    			    int wLen  = Math.min(toEnd, len);
    			    // logger.debug("writing " + off + "/" + len + " to " + end + ":" + wStart + "/" + wLen);
    			    System.arraycopy(buf, off, buffer, wStart, wLen);
    			    notify();
    			    end += wLen;
    			    len -= wLen;
    			    if (len > 0) 
    			    	// Write the rest
    			    	write(buf,off+wLen,len);
    			    else
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
    
    public synchronized int read(byte[] buf, int off, int len) throws IOException {
    	try {
    		while (true) {
    			// Something to read
    			if (end > start) {
    				
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
