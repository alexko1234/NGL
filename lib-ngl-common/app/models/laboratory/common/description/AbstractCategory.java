package models.laboratory.common.description;

import models.utils.Model;

/**
 * Parent class categories not represented by a table in the database
 * @author ejacoby
 *
 */
public abstract class AbstractCategory extends Model<AbstractCategory>{

	public String name;

	public AbstractCategory(String classNameDA0) {
		super(classNameDA0);
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
