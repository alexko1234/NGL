package services.print;

import java.util.List;

public class PrintConfiguration {

	private List<?> printers, barcodePositions;

	public PrintConfiguration() {}

	public List<?> getBarcodePositions() {
		return barcodePositions;
	}

	public List<?> getPrinters() {
		return printers;
	}

	public void setBarcodePositions(List<?> list) {
		barcodePositions = list;
	}

	public void setPrinters(List<?> list) {
		printers = list;
	}

}
