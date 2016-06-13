package services.print;

import java.util.List;

import javax.print.PrintService;

public interface PrintServices {

	public List<Printer> getBarcodePrinters() ;
	
	public Printer getDefaultBarcodePrinter() throws PrintServicesException ; 
	
	public void setDefaultBarcodePrinterName(String name) throws PrintServicesException ;

	public List<Printer> getFlashcodePrinters() ;
	
	public Printer getDefaultFlashcodePrinter() throws PrintServicesException ; 
	
	public void setDefaultFlashcodePrinterName(String name) throws PrintServicesException ;
	
	public PrintService getDefaultPrintService() throws PrintServicesException ;
	
	public void setDefaultPrinterName(String name) throws PrintServicesException ;
	
}
