package services.print.zebra;

import javax.print.attribute.PrintRequestAttribute;


public class BarcodePosition implements PrintRequestAttribute {	
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String id;

	private String barcodePositionName;

	private Integer labelWidth;

	private String labelCommand;

	private String barcodeCommand;

	private boolean barcodeBottom;

	public BarcodePosition(String id, String name, Integer labelWidth, String labelCommand, String barcodeCommand, boolean barcodeBottom) {
		this.id = id;
		this.barcodePositionName = name;
		this.labelWidth = labelWidth;
		this.labelCommand = labelCommand;
		this.barcodeCommand = barcodeCommand;
		this.barcodeBottom = barcodeBottom;
	}

	public String getBarcodePositionName() {
		return barcodePositionName;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return barcodePositionName;
	}

	public Integer getLabelWidth() {
		return labelWidth;
	}

	public boolean equals(Object object) {
		if (!(object instanceof BarcodePosition) || object == null) {
			return false;
		}

		return id.equals(((BarcodePosition) object).getId());
	}

	public int hashCode() {
		return barcodePositionName.hashCode();
	}

	public String toString() {
		return barcodePositionName;
	}

	public Class<BarcodePosition> getCategory() {
		return BarcodePosition.class;
	}

	public String getBarcodeCommand() {
		return barcodeCommand;
	}

	public String getLabelCommand() {
		return labelCommand;
	}

	public boolean isBarcodeBottom() {
		return barcodeBottom;
	}

	public String getPrintCommand(String label) {
	    StringBuffer printCommand = new StringBuffer();

	    if(label.contains(":")) {
	        String[] lignes = label.split(":");
	        //logger.debug("Impression d'un flashcode et de trois lignes : " + lignes[0] + ", " + lignes[1] + ", " + lignes[2]);
	        printCommand.append(barcodeCommand).append(",\"").append(lignes[0]).append("\"");
	        for(int i=0; i < lignes.length; i++) 
	            printCommand.append("\n").append(alterLabelCommandBis(i)).append(",\"").append(lignes[i]).append("\"");
	        return printCommand.toString();
	    }
	    
	    if(label.contains(";")) {
	        String[] lignes = label.split(";");
	        //logger.debug("Impression de trois lignes : " + lignes[0] + ", " + lignes[1] + ", " + lignes[2]);
	        for(int i=0; i < lignes.length; i++) 
	            printCommand.append(alterLabelCommand(i)).append(",\"").append(lignes[i]).append("\"\n"); 
	        return printCommand.toString();
	    }

	    if(label.contains(",")) {
	        String[] lignes = label.split(",");
	        //logger.debug("Impression de deux lignes : " + lignes[0] + ", " + lignes[1]);
	        return printCommand.append(labelCommand).append(",\"").append(lignes[0]).append("\"\n").append(barcodeCommand).append(",\"").append(lignes[1]).append('\"').toString(); 
	    }

	    if(label.contains("|")) {
	        String[] lignes = label.split("\\|"); //exemple : FRG_AHX_ABGA|0AGF3EW1D
	        //logger.debug("Impression de deux lignes : " + lignes[0] + ", " + lignes[1]);
	        printCommand.append(labelCommand).append(",\"").append(lignes[0]).append("\"\n");
	        printCommand.append(labelCommand.replace("3", "7")).append(",\"").append(lignes[1]).append("\"\n");
	        printCommand.append(barcodeCommand).append(",\"").append(lignes[1]).append('\"'); 
	        
	        return printCommand.toString();
	    }
	    
	    if (barcodeCommand.length() == 0)
	        return printCommand.append(labelCommand).append(",\"").append(label).append("\"\n").toString();

	    if (labelCommand.length() == 0)
	        return printCommand.append(barcodeCommand).append(",\"").append(label).append("\"\n").toString();

		if (isBarcodeBottom())
		    return printCommand.append(labelCommand).append(",\"").append(label).append("\"\n").append(barcodeCommand).append(",\"").append(label).append('\"').toString(); 
		else
		    return printCommand.append(barcodeCommand).append(",\"").append(label).append("\"\n").append(labelCommand).append(",\"").append(label).append('\"').toString();
	}
    
    private final String[] mapper = { null, "4", "8" };
    
    private String alterLabelCommand(int i) {
        StringBuffer sb = new StringBuffer(labelCommand);
        if(i != 0) 
            sb.insert(4, mapper[i]);
        return sb.toString();
    }
    
    private String alterLabelCommandBis(int i) {
        StringBuffer sb = new StringBuffer(labelCommand);
        if(i != 0) 
            sb.insert(5, mapper[i]);
        return sb.toString();
    }

}
