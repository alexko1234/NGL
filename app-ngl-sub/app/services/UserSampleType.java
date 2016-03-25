package services;

public class UserSampleType {
	private String alias = null;
	private String title = null;
	private String description = null;
	private String commonName = null;
	private String scientificName= null;
	private String anonymizedName=null;
	
	public UserSampleType() {
	}
	
	public UserSampleType(String alias) {
		this.setAlias(alias);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getAnonymizedName() {
		return anonymizedName;
	}

	public void setAnonymizedName(String anonymizedName) {
		this.anonymizedName = anonymizedName;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

}
