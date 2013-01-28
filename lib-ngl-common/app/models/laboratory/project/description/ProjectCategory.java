package models.laboratory.project.description;

public class ProjectCategory {

	public Long id;

	public String name;

	public String code;

	public ProjectCategory() {
		super();
	}

	public ProjectCategory(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
}
