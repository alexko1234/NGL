package models.laboratory.instrument.description;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Institute;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model;


public class Instrument extends Model<Instrument>{

	public String name;
	
	public Boolean active;
	
	public String path;
	
	public List<Institute> institutes = new ArrayList<Institute>();
	
	
	public static Finder<Instrument> find = new Finder<Instrument>(InstrumentDAO.class.getName()); 

	
	public Instrument() {
		super(InstrumentDAO.class.getName());
	}
	
	
}
