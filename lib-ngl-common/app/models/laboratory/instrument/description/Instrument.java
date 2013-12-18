package models.laboratory.instrument.description;


import java.util.List;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.ValuationCriteria.ValuationCriteriaFinder;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


public class Instrument extends Model<Instrument>{

	public String name;
	public Boolean active;
	public String path;
	public List<Institute> institutes;
	
	public static InstrumentFinder find = new InstrumentFinder();

	
	public Instrument() {
		super(InstrumentDAO.class.getName());
	}
	
	
	public static class InstrumentFinder extends Finder<Instrument>{

		public InstrumentFinder() {
			super(InstrumentDAO.class.getName());			
		}
		
		public List<Instrument> findByInstrumentUsedTypeCode(String instrumentUsedTypeCode) throws DAOException{
			return ((InstrumentDAO)getInstance()).findByInstrumentUsedTypeCode(instrumentUsedTypeCode);
		}
	}
	
}
