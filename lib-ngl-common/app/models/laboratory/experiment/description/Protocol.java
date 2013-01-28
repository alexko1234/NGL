package models.laboratory.experiment.description;

import java.util.List;

public class Protocol{

	public Long id;
	
	public String name;
	public String filePath;
	public String version;
	
	public ProtocolCategory protocolCategory;
	
	public List<ReagentType> reagentTypes;
	
	//TODO Detail manip et dosage manip ???
	
	
	
}
