package services.print.zebra;

public class TLP2844 extends EPL2Printer {
	
	public TLP2844() {
		super("TLP 2844") ;
	}
	
	public TLP2844(String name, String location, String ipAdress, Integer port, String defaultSpeed, String defaultDensity, String defaultBarcodePositionId) {
		super(name, "TLP 2844", location, ipAdress, port, defaultSpeed, defaultDensity, defaultBarcodePositionId);		
	}

}
