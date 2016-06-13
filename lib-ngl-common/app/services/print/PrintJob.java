package services.print;

public abstract class PrintJob {
	 
	private Printer printer ;
	private PrintRequestAttributeSet attributes ;

	public PrintJob(Printer printer) {
		this.printer = printer ;
		this.attributes = new PrintRequestAttributeSet(printer.getDefaultPrintRequestAttributes()) ;
	}	


	public Printer getPrinter() {
		return printer;
	}
	
	public PrintRequestAttributeSet getPrintRequestAttributes() {
		return attributes ;	
	}

}
