package models.laboratory.common.description;

import models.laboratory.common.description.dao.ResolutionDAO;
import models.utils.Model;


/**
 * Value of the resolution of final possible state
 * @author ejacoby
 *
 */
public class Resolution extends Model<Resolution>{

	public String name;
	
	public static Finder<Resolution> find = new Finder<Resolution>(ResolutionDAO.class.getName());
	
	public Resolution() {
		super(ResolutionDAO.class.getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resolution other = (Resolution) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
}
