package models.description.content;


public class ContainerSupportCategory{

	public Long id;
	
	public String name;
	
	public String code;
	
	public int nbUsableContainer;
	
	public int nbLine;
	
	public int nbColumn;
	

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getNbUsableContainer() {
		return nbUsableContainer;
	}

	public void setNbUsableContainer(int nbUsableContainer) {
		this.nbUsableContainer = nbUsableContainer;
	}

	public int getNbLine() {
		return nbLine;
	}

	public void setNbLine(int nbLine) {
		this.nbLine = nbLine;
	}

	public int getNbColumn() {
		return nbColumn;
	}

	public void setNbColumn(int nbColumn) {
		this.nbColumn = nbColumn;
	}
	
	
}
