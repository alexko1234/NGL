package services.print.zebra;

public class BBP11 extends EPL2Printer {
	
	public BBP11() {
		super("BBP11") ;
	}
	
	public BBP11(String name, String location, String ipAdress, Integer port, String defaultSpeed, String defaultDensity, String defaultBarcodePositionId) {
		super(name, "BBP11", location, ipAdress, port, defaultSpeed, defaultDensity, defaultBarcodePositionId);		
	}

}
