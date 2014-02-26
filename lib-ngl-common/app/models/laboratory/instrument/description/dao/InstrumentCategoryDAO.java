package models.laboratory.instrument.description.dao;

import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAODefault;

import org.springframework.stereotype.Repository;

@Repository
public class InstrumentCategoryDAO extends AbstractDAODefault<InstrumentCategory>{
	
	public static Finder<InstrumentCategory> find = new Finder<InstrumentCategory>(InstrumentCategoryDAO.class.getName()); 	


	public InstrumentCategoryDAO() {
		super("instrument_category",InstrumentCategory.class,true);
	}

	public InstrumentCategory findByInstrumentUsedTypeCode(){
		return null;
	}
	
}
