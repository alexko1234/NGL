package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.utils.Model;
import play.api.modules.spring.Spring;

public class Protocol extends Model<Protocol>{


	public String name;
	public String filePath;
	public String version;
	
	public ProtocolCategory protocolCategory;
	
	public List<ReagentType> reagentTypes;

	public static Finder<Protocol> find = new Finder<Protocol>(ProtocolDAO.class.getName()); 
	
	public Protocol() {
		super(ProtocolDAO.class.getName());
	}
	
	public static Protocol findByName(String name)
	{
		ProtocolDAO protocolDAO = (ProtocolDAO) Spring.getBeanOfType(ProtocolDAO.class);
		return protocolDAO.findByName(name);
	}
	
	
	//TODO Detail manip et dosage manip ???

	
}
