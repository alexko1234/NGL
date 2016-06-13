package services.print.zebra;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import services.print.AbstractPrinter;
import services.print.BarcodePrintJob;
import services.print.PrintJob;
import services.print.PrintRequestAttributeSet;
import services.print.PrintServices;
import services.print.PrintServicesException;

public abstract class EPL2Printer extends AbstractPrinter {

	private static final Log logger = LogFactory.getLog(EPL2Printer.class);

	private boolean inverseList = false ;
	private String defaultSpeed;
	private String defaultDensity;
	private String defaultBarcodePositionId;	
	private List<BarcodePosition> barcodePositions ;
	
	private transient BarcodePosition defaultBarcodePosition ;

	protected PrintRequestAttributeSet defaultPrintRequestAttributes = null ;
				
	protected EPL2Printer(String model) {
		super(model) ;
	}
	
	/**
	 * @param name
	 * @param location
	 * @param ipAdress
	 * @param port
	 */
	public EPL2Printer(String name, String model, String location, String ipAdress, Integer port, String defaultSpeed, String defaultDensity, String defaultBarcodePositionId) {
		super(name, model, location, ipAdress, port);		
		this.defaultSpeed = defaultSpeed ;
		this.defaultDensity = defaultDensity ;
		this.defaultBarcodePositionId = defaultBarcodePositionId ;					
	}	

	public void reset() throws PrintServicesException {
		sendCommands("^@");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			if (logger.isDebugEnabled())
				logger.debug("While waiting 2000ms before sending commands ", e);
		}

		StringBuffer commands = new StringBuffer();
		commands.append('S').append(defaultSpeed).append('\n');
		commands.append('D').append(defaultDensity).append('\n');

		sendCommands(commands.toString());
	}

	public void print(PrintJob printJob) throws PrintServicesException {
		PrintRequestAttributeSet attributes = printJob.getPrintRequestAttributes() ;

		StringBuffer commands = new StringBuffer();
		BarcodePosition barcodePosition = (BarcodePosition)attributes.get(BarcodePosition.class) ;
		commands.append('q').append(barcodePosition.getLabelWidth().intValue()).append("\n");

		List<String> labels = ((BarcodePrintJob)printJob).getLabels();		

		if (inverseList)
			Collections.reverse(labels) ;
		
		for (int i=labels.size()-1; i>=0; i--) {
			String label = (String)labels.get(i) ;
			addBarCodeCommands(label, commands, barcodePosition);
			commands.append("P1\n");
		}

		sendCommands(commands.toString());
	}

	private void addBarCodeCommands(String barCode, StringBuffer commandsBuffer, BarcodePosition configuration) {		
		commandsBuffer.append("N\n");		
		commandsBuffer.append(configuration.getPrintCommand(barCode)).append('\n');
	}

	private void sendCommands(String commands) throws PrintServicesException {

		if (logger.isInfoEnabled())
			logger.info("Sending\n" + commands + "\n to " + this);

		Socket printerSocket = new Socket();

		try {
			printerSocket.connect(new InetSocketAddress(getIpAdress(), getPort().intValue()), 1000);

			BufferedOutputStream output = new BufferedOutputStream(printerSocket.getOutputStream());
			output.write(commands.getBytes());
			output.write('\n');
			output.close();

			if (logger.isInfoEnabled())
				logger.info("Done sending commands to " + this);

		} catch (Exception e) {
			throw new PrintServicesException("While sending \n" + commands + "\n to " + this, e);
		} finally {
			try {
				printerSocket.close();
			} catch (IOException e) {
				if (logger.isDebugEnabled())
					logger.debug("While closing socket to " + this, e);
			}
		}

	}

	public void calibrate() throws PrintServicesException {
		reset() ;
		sendCommands("xa");		
	}

	public PrintJob newPrintJob() {		
		return new ZebraPrintJob(this) ;
	}

	public synchronized PrintRequestAttributeSet getDefaultPrintRequestAttributes() {
		if (defaultPrintRequestAttributes==null) {
			defaultPrintRequestAttributes  = new PrintRequestAttributeSet() ;			
			try {				
				defaultPrintRequestAttributes.add(getDefaultBarcodePosition()) ;
			} catch (PrintServicesException e) {
				throw new RuntimeException("En positionnant la configuration par defaut (id "+defaultBarcodePositionId+") pour "+getName(),e) ;
			}
		}
		
		return defaultPrintRequestAttributes ;
	}

	public synchronized BarcodePosition getDefaultBarcodePosition() throws PrintServicesException {
		if (defaultBarcodePosition==null) {						
			String preferredBarcodeBositionId = Preferences.userNodeForPackage(PrintServices.class).get("barcode-position.default."+getName()+" ("+getModel()+")", null);
						
			if (preferredBarcodeBositionId==null)
				preferredBarcodeBositionId = defaultBarcodePositionId ;
			
			defaultBarcodePosition = getBarcodePosition(preferredBarcodeBositionId) ;
		}
		
		return defaultBarcodePosition ;
	}
	
	public void setDefaultBarcodePosition(BarcodePosition position) throws PrintServicesException {
		Preferences preferences = Preferences.userNodeForPackage(PrintServices.class);
		preferences.put("barcode-position.default."+getName()+" ("+getModel()+")", position.getId());

		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			throw new PrintServicesException(e);
		}
	}

	public BarcodePosition getBarcodePosition(String id) throws PrintServicesException {		
		
		for (Iterator<BarcodePosition> iter = barcodePositions.iterator(); iter.hasNext();) {
			BarcodePosition barcodePosition = (BarcodePosition)iter.next();
			if (barcodePosition.getId().equals(id))
				return barcodePosition;
		}

		throw new PrintServicesException("Identifiant de position de code barre inconnu : "+id) ;
	}

	public List<BarcodePosition> getBarcodePositions() {
		return barcodePositions;
	}

	public void setBarcodePositions(List<BarcodePosition> barcodePositions) {
		this.barcodePositions = barcodePositions;
	}

	public String getDefaultBarcodePositionId() {
		return defaultBarcodePositionId;
	}

	public void setDefaultBarcodePositionId(String defaultBarcodePositionId) {
		this.defaultBarcodePositionId = defaultBarcodePositionId;
	}

	public String getDefaultDensity() {
		return defaultDensity;
	}

	public void setDefaultDensity(String defaultDensity) {
		this.defaultDensity = defaultDensity;
	}

	public String getDefaultSpeed() {
		return defaultSpeed;
	}

	public void setDefaultSpeed(String defaultSpeed) {
		this.defaultSpeed = defaultSpeed;
	}

	public void setDefaultPrintRequestAttributes(PrintRequestAttributeSet defaultPrintRequestAttributes) {
		this.defaultPrintRequestAttributes = defaultPrintRequestAttributes;
	}

	public boolean isInverseList() {
		return inverseList;
	}

	public void setInverseList(boolean inverseList) {
		this.inverseList = inverseList;
	}

}
