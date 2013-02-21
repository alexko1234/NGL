package models.laboratory.instrument.description;

import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model;


public class Instrument extends Model<Instrument>{

	
	public String name;
	
	public Instrument() {
		super(InstrumentDAO.class.getName());
	}
	
	
}
