package models.laboratory.experiment.description;

import java.util.List;

import models.laboratory.experiment.description.dao.ProtocolDAO;
import models.laboratory.reagent.description.ReagentType;
import models.utils.Model;
import play.api.modules.spring.Spring;

public class Protocol extends Model<Protocol>{


	public String name;
	public String filePath;
	public String version;
	
	public ProtocolCategory category;
	
	public List<ReagentType> reagentTypes;

	public static Finder<Protocol> find = new Finder<Protocol>(ProtocolDAO.class.getName()); 
	
	public Protocol() {
		super(ProtocolDAO.class.getName());
	}
	
}
