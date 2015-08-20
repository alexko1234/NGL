package services.description.instrument;

import java.util.List;
import java.util.Map;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.Logger;
import play.data.validation.ValidationError;

public abstract class AbstractInstrumentService {
	
	public void main(Map<String, List<ValidationError>> errors) throws DAOException{
		
		Logger.debug("Begin remove Instrument");
		DAOHelpers.removeAll(Instrument.class, Instrument.find);
		
		Logger.debug("Begin remove Instrument Used Type");
		DAOHelpers.removeAll(InstrumentUsedType.class, InstrumentUsedType.find);
		
		Logger.debug("Begin remove Instrument Category !!!");
		DAOHelpers.removeAll(InstrumentCategory.class, InstrumentCategory.find);
		
		Logger.debug("Begin save categories");
		saveInstrumentCategories(errors);
		
		Logger.debug("Begin save Instrument Used Type");
		saveInstrumentUsedTypes(errors);	
		
		Logger.debug("End Instrument service");
	}

	public abstract void saveInstrumentUsedTypes(Map<String, List<ValidationError>> errors) throws DAOException;
		
	public abstract void saveInstrumentCategories(Map<String, List<ValidationError>> errors) throws DAOException;

}
