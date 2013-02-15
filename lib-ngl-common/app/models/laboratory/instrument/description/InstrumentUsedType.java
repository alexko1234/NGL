package models.laboratory.instrument.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
public class InstrumentUsedType extends CommonInfoType{
	
	public List<Instrument> instruments;
	
	public InstrumentCategory instrumentCategory;

	public static Finder<InstrumentUsedType> find = new Finder<InstrumentUsedType>(InstrumentUsedTypeDAO.class.getName()); 
	
	public InstrumentUsedType() {
		super(InstrumentUsedTypeDAO.class.getName());
	}
	
	
}
