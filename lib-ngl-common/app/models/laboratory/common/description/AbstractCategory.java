package models.laboratory.common.description;
/**
 * Parent class categories not represented by a table in the database
 * @author ejacoby
 *
 */
public class AbstractCategory {

	public Long id;

	public String name;

	public String code;

	public AbstractCategory() {
		super();
	}

	public AbstractCategory(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	/**
	 * Necessary in update operation to add new type in relationship because compare list from database and list to update
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCategory other = (AbstractCategory) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
}
