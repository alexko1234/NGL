package models.laboratory.instrument.description;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.description.Institute;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.DAOException;

public class Instrument extends Model<Instrument> {

	public static final InstrumentFinder find = new InstrumentFinder();

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
	
	public Instrument() {
		super(InstrumentDAO.class.getName());
	}
		
	public static class InstrumentFinder extends Finder<Instrument,InstrumentDAO> {

//		public InstrumentFinder() {
//			super(InstrumentDAO.class.getName());			
//		}
		public InstrumentFinder() { super(InstrumentDAO.class); }
		
		public List<Instrument> findByQueryParams(InstrumentQueryParams instrumentsQueryParams) throws DAOException {
//			return ((InstrumentDAO)getInstance()).findByQueryParams(instrumentsQueryParams);
			return getInstance().findByQueryParams(instrumentsQueryParams);
		}
		
		public List<Instrument> findByExperimentTypeQueryParams(InstrumentQueryParams instrumentsQueryParams) throws DAOException {
//			return ((InstrumentDAO)getInstance()).findByExperimentTypeQueryParams(instrumentsQueryParams);
			return getInstance().findByExperimentTypeQueryParams(instrumentsQueryParams);
		}
		
		public void cleanCache() throws DAOException {
//			 ((InstrumentDAO)getInstance()).cleanCache();
			 getInstance().cleanCache();
		}
		
	}
	
}
