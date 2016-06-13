package services.print;

public interface Printer {
	public String getName();
	public String getLocation();
	public String getIpAdress();
	public Integer getPort();
	
	public void reset() throws PrintServicesException;
	
	public PrintJob newPrintJob() ;
	
	public void print(PrintJob job) throws PrintServicesException;

	public PrintRequestAttributeSet getDefaultPrintRequestAttributes();
}