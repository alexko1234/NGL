package services.print.zebra;

public class TLP3842 extends EPL2Printer {
	
	public TLP3842() {
		super("TLP 3842") ;
	}
	
	public TLP3842(String name, String location, String ipAdress, Integer port, String defaultSpeed, String defaultDensity, String defaultBarcodePositionId) {
		super(name, "TLP 3842", location, ipAdress, port, defaultSpeed, defaultDensity, defaultBarcodePositionId);		
	}

}
