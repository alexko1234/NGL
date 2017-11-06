package fr.cea.ig.io;

import java.io.*;

// Slugfest but this is working.
public class OutToInStreams {

	private static final play.Logger.ALogger logger = play.Logger.of(OutToInStreams.class);
	
	public static final int DEFAULT_BUFFER_SIZE = 4096;
	/*
	
	private byte[] buffer;
	
	private int start;
	
	private int end;
	*/
	
	
    static enum Status {
    	Waiting,
    	Ready,
    	Closed
    }

    // Must allow close operation.
    private Status status = Status.Waiting;

    private byte value;

    private Output output;
    private Input  input;
    
    public OutToInStreams() {
    	this(DEFAULT_BUFFER_SIZE);
    }
    
    public OutToInStreams(int bufferSize) {
    	// buffer = new byte[bufferSize];
    	// This will disappear
    	status = Status.Waiting;
    	output = new Output();
    	input  = new Input();
    }
    
    class Output extends OutputStream {

    	private boolean closed;
    	
    	private Output() {
    		closed = false;
    	}
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
    	// Why would somenoe write an int and not a byte ?
    	@Override
    	public void write(int b) throws IOException {
    		// System.out.println("OS.write byte " + b);
    		OutToInStreams.this.write(b);
    	}

    	// the input stream should not expect anymore data.
    	@Override
    	public void 	close() throws IOException {
    		OutToInStreams.this.close();
    		closed = true;
    	}
    	
    	@Override
    	public void 	flush() throws IOException {}
    }

    class Input extends InputStream {
        
    	
    	private boolean closed;
    	
        private Input() {
        	closed = false;
        }
        
        @Override
        public int available() {
        	return OutToInStreams.this.available();
        }
        
        @Override
        public void close() throws IOException {
        	logger.info("input/close");
        	closed = true;
        }
        
        @Override
        public int 	read() throws IOException {
        	return OutToInStreams.this.read();
        }
        
        
        @Override
        public int 	read(byte[] b) throws IOException {
        	return read(b,0,b.length);
        }
        
        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
        	int b;
        	for (int i=0; i<len; i++) {
        		b = read();
        		// System.out.println("IS.read [" + i + "] : " + b);
        		if (b == -1) {
        			// System.out.println("bridge closed, returning EOF after reading " + i);
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
        
        /*
        @Override
        public long skip(long n) { 
        	return 0; 
        }
         */
        // Unsupported methods
        public boolean 	markSupported() { return false; }
        public void mark(int readlimit) { throw new UnsupportedOperationException(); }
        public void 	reset() { throw new UnsupportedOperationException(); }

    }

    
    public OutputStream getOutputStream() { return output; }
    public InputStream  getInputStream()  { return input; }
    
    public int available() {
    	if (status == Status.Ready)
    		return 1;
    	return 0; 
    }
    
    // Those are in fact reads and write on a single value that is
    // tutorial level stuff. The status representation way be quite a bad idea.
    // This must be synchronized otherwise the read and write may conflict
    // when accessing the buffer.
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
