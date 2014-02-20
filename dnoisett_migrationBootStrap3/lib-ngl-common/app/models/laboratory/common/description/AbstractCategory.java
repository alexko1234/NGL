package models.laboratory.common.description;

import models.utils.Model;

/**
 * Parent class categories not represented by a table in the database
 * @author ejacoby
 *
 */
public abstract class AbstractCategory<T> extends Model<T>{

	public String name;

	public AbstractCategory(String classNameDA0) {
		super(classNameDA0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCategory other = (AbstractCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}
