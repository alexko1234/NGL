package models.laboratory.common.description;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;

import models.utils.Model;

/**
 * Parent class categories not represented by a table in the database.
 * 
 * @author ejacoby
 *
 */
public abstract class AbstractCategory<T> extends Model<T> {

	public String name;

	public AbstractCategory(String classNameDA0) {
		super(classNameDA0);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(),name);
	}

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}*/

	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		AbstractCategory<?> other = (AbstractCategory<?>) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} /*else if (!name.equals(other.name))
//			return false;
//		return true;*/
//		return name.equals(other.name);
		return typedEquals(AbstractCategory.class, this, obj, 
				           (a,b) -> super.equals(obj) && objectEquals(a.name,b.name));
	}

}
