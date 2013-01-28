package models.laboratory.instrument.description;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.IDynamicType;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import play.modules.spring.Spring;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
public class InstrumentUsedType extends CommonInfoType implements IDynamicType{
	
	public Long id;
	
	public List<Instrument> instruments;
	
	public InstrumentCategory instrumentCategory;
	
	@Override
	public CommonInfoType getInformations() {		
		return this;
	}
	
	@Override
	public long getIdType() {
		return id;
	}
	
	public static Map<String, String> getMapInstrumentTypes()
	{
		Map<String, String> mapInstrumentTypes = new HashMap<String, String>();
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		for(InstrumentUsedType instType : instrumentUsedTypeDAO.findAll()){
			mapInstrumentTypes.put(instType.id.toString(), instType.name);
		}
		return mapInstrumentTypes;
	}
	public IDynamicType findById(long id)
	{
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		return instrumentUsedTypeDAO.findById(id);
	}

	public static InstrumentUsedType findByCommonInfoType(Long idCommonInfoType)
	{
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		return instrumentUsedTypeDAO.findByCommonInfoType(idCommonInfoType);
	}
}
