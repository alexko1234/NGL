package services.print;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;




import org.json.JSONML;
//import org.exolab.castor.mapping.Mapping;
//import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import play.libs.XML;
import play.libs.XPath;


final class PrintServicesImpl implements PrintServices {

	private static final String BARCODE_PRINTER_KEY = "printer.default.barcode";
	
	private static final String FLASHCODE_PRINTER_KEY = "printer.default.flashcode";
	
	private static final String PATH_TO_MAPPING = "/config/mapping.xml";
	
	private static final String FILENAME_1D = "printers.xml", FILENAME_2D = "printers2D.xml";

	private PrintConfiguration configuration, configuration2D;
	
	PrintServicesImpl() throws PrintServicesException {
		configuration = loadPrintConfiguration("src" + File.separator + FILENAME_1D);
		configuration2D = loadPrintConfiguration("src" + File.separator + FILENAME_2D); 
	}

	@SuppressWarnings("unchecked")
	public List getBarcodePrinters() {
		return configuration.getPrinters() ;
	}
	
	@SuppressWarnings("unchecked")
	public List getFlashcodePrinters() {
		return configuration2D.getPrinters() ;
	}

	private PrintConfiguration loadPrintConfiguration(String propertiesFilename) throws PrintServicesException {
		try {
			/*
			Mapping mapping = new Mapping(getClass().getClassLoader());

			mapping.loadMapping(new InputSource(this.getClass().getResourceAsStream(PATH_TO_MAPPING)));

			Unmarshaller unmarshaller = new Unmarshaller(PrintConfiguration.class);
			unmarshaller.setMapping(mapping);
			unmarshaller.setValidation(false);

			PrintConfiguration configuration = (PrintConfiguration) unmarshaller.unmarshal(new InputSource(new FileReader(propertiesFilename)));
			configuration.setPrinters(Collections.unmodifiableList(configuration.getPrinters()));
			 */
			
			//TODO LOAD PRINTERS CONFIGURATION
			
			return configuration;

		} catch (Exception e) {
			throw new PrintServicesException(e);
		}
	}
	
	public Printer getDefaultBarcodePrinter() throws PrintServicesException {
		return getDefaultPrinter(BARCODE_PRINTER_KEY);
	}
	
	public Printer getDefaultFlashcodePrinter() throws PrintServicesException {
		return getDefaultPrinter(FLASHCODE_PRINTER_KEY);
	}
	
	@SuppressWarnings("unchecked")
	private Printer getDefaultPrinter(String key) throws PrintServicesException {
		Printer defaultPrinter = null;

		String defaultPrinterName = Preferences.userNodeForPackage(this.getClass()).get(key, null);

		List printers = getBarcodePrinters();

		if (defaultPrinterName == null) {
			if (printers.size() > 0)
				defaultPrinter = (Printer) printers.get(0);
		} else
			for (Iterator iter = printers.iterator(); iter.hasNext() && defaultPrinter == null;) {
				Printer printer = (Printer) iter.next();
				if (printer.getName().equals(defaultPrinterName))
					defaultPrinter = printer;
			}

		return defaultPrinter;
	}

	private void setDefaultPrinterName(String name, String type) throws PrintServicesException {
		Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		preferences.put(type, name);

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			throw new PrintServicesException(e);
		}
	}
	
	public void setDefaultBarcodePrinterName(String name) throws PrintServicesException {
		setDefaultPrinterName(name, BARCODE_PRINTER_KEY);
	} 
	
	public void setDefaultFlashcodePrinterName(String name) throws PrintServicesException {
		setDefaultPrinterName(name, FLASHCODE_PRINTER_KEY);
	}

	public PrintService getDefaultPrintService() {
		return PrintServiceLookup.lookupDefaultPrintService();
	}

	public void setDefaultPrinterName(String name) throws PrintServicesException {
//		Appel au patch "maison" deprecie
//		UnixPrintServiceLookup.setDefaultPrintService(name);
	}

}
