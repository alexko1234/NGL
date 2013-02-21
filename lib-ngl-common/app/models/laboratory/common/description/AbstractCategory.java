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

}
