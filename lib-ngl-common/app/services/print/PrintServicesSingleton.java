package services.print;


public class PrintServicesSingleton {

	private static PrintServices sInstance = null;

	private PrintServicesSingleton() throws PrintServicesException {
	}

	public static synchronized PrintServices getInstance() throws PrintServicesException {

		if (sInstance == null) 
			sInstance = new PrintServicesImpl();		

		return sInstance;
	}	

}
