package fr.cea.ig.play.test;

import java.io.IOException;

import play.Application;
import play.libs.ws.WSClient;
import play.test.TestServer;
import play.test.WSTestClient;

/**
 * Complete test server environment. This starts and stop a full application 
 * stack : application, HTTP server, web client.
 *  
 * @see AbstractServerTest
 *  
 * @author vrd
 *
 */
public abstract class CompleteTestServer {

	/**
	 * Running application.
	 */
	protected Application  app;
	
	/**
	 * HTTP server.
	 */
	protected TestServer   server;
	
	/**
	 * Web client.
	 */
	protected WSClient     ws;

	/**
	 * Override to define custom application creation.
	 * @return application to run
	 */
	public abstract Application createApplication();

	/**
	 * Client and server port number.
	 * @return port used for the client and server
	 */
	public int getPort() {
		return 3333;
	}

	/**
	 * Start the full application stack.
	 */
	public void start() {
		app = createApplication();
		server = new TestServer(getPort(),app);
		server.start();
		ws = WSTestClient.newClient(getPort());

	}

	/**
	 * Stop the full application stack.
	 * @throws IOException
	 */
	public void stop() throws IOException {
		app.asScala().stop();
		server.stop();
		ws.close();
	}
	
	/**
	 * Application.
	 * @return application
	 */
	public Application getApplication() { 
		return app; 
	}
	
	/**
	 * HTTP server.
	 * @return HTTP server
	 */
	public TestServer getServer() { 
		return server; 
	}
	
	/**
	 * Web client.
	 * @return web client
	 */
	public WSClient getWSClient() { 
		return ws; 
	}
	
}



