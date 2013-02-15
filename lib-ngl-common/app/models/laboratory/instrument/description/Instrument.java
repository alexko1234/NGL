package models.laboratory.instrument.description;

import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model;


public class Instrument extends Model<Instrument>{

	
	public String name;
	
	public Instrument() {
		super(InstrumentDAO.class.getName());
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
		Instrument other = (Instrument) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	
}
