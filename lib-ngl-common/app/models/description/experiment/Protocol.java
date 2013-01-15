package models.description.experiment;

import java.util.List;

public class Protocol{

	public Long id;
	
	public String name;
	public String filePath;
	public String version;
	
	public ProtocolCategory protocolCategory;
	
	public List<ReagentType> reagentTypes;
	
	//TODO Detail manip et dosage manip ???
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<ReagentType> getReagentTypes() {
		return reagentTypes;
	}

	public void setReagentTypes(List<ReagentType> reagentTypes) {
		this.reagentTypes = reagentTypes;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ProtocolCategory getProtocolCategory() {
		return protocolCategory;
	}

	public void setProtocolCategory(ProtocolCategory protocolCategory) {
		this.protocolCategory = protocolCategory;
	}

	
}
