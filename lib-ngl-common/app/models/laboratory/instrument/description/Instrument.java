package models.laboratory.instrument.description;


import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.common.description.Institute;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


public class Instrument extends Model<Instrument>{

	public String shortName;
	public String name;
	public Boolean active;
	public String path;
	
	@JsonIgnore
	public InstrumentUsedType instrumentUsedType;	
	
	@JsonIgnore
	public List<Institute> institutes;
	
	/* used only to send in json */
	public String typeCode;
	public String categoryCode;	
	
	public static InstrumentFinder find = new InstrumentFinder();

	public Instrument() {
		super(InstrumentDAO.class.getName());
	}
		
	public static class InstrumentFinder extends Finder<Instrument>{

		public InstrumentFinder() {
			super(InstrumentDAO.class.getName());			
		}
		
		public List<Instrument> findByQueryParams(InstrumentQueryParams instrumentsQueryParams) throws DAOException{
			return ((InstrumentDAO)getInstance()).findByQueryParams(instrumentsQueryParams);
		}
	}
	
}
