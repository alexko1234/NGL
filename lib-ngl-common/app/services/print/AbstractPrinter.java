package services.print;


public abstract class AbstractPrinter implements Printer {	

	private String name;

	private String model ;
	
	private String location;

	private String ipAdress;

	private Integer port;	
	
	protected AbstractPrinter(String model) {
		this.model = model ;
	}
	
	public AbstractPrinter(String name,String model, String location,String ipAdress,Integer port) {
		this(model) ;
		this.name = name ;		
		this.location = location ;
		this.ipAdress = ipAdress ;
		this.port = port ; 		
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getIpAdress() {
		return ipAdress;
	}

	public Integer getPort() {
		return port;
	}	

	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public abstract void reset() throws PrintServicesException ;
	
	public String toString() {
		return getName()+" ("+model+") "+getLocation() ;
	}
		
	public boolean equals(Object object) {
		if (!(object instanceof AbstractPrinter) || object==null) 
			return false;
		
		AbstractPrinter printer = (AbstractPrinter)object;
		return printer.getName().equals(name) ;
	}

	public String getModel() {
		return model;
	}

}