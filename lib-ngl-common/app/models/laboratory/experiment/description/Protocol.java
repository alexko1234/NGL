package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.utils.Model;
import play.modules.spring.Spring;

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
		ProtocolDAO protocolDAO = (ProtocolDAO) Spring.getBeanOfType(ProtocolDAO.class.getName());
		return protocolDAO.findByName(name);
	}
	
	
	//TODO Detail manip et dosage manip ???
	
	/**
	 * Necessary in update operation to add new type in relationship because compare list from database and list to update
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Protocol other = (Protocol) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	
	
	
}
