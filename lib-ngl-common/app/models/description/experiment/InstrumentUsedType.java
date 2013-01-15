package models.description.experiment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.description.IDynamicType;
import models.description.common.CommonInfoType;
import models.description.experiment.dao.InstrumentUsedTypeDAO;
import play.modules.spring.Spring;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
public class InstrumentUsedType implements IDynamicType{
	
	public Long id;
	
	public CommonInfoType commonInfoType;

	public List<Instrument> instruments;
	
	public InstrumentCategory instrumentCategory;
	
	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
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
			mapInstrumentTypes.put(instType.getId().toString(), instType.getCommonInfoType().getName());
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CommonInfoType getCommonInfoType() {
		return commonInfoType;
	}

	public void setCommonInfoType(CommonInfoType commonInfoType) {
		this.commonInfoType = commonInfoType;
	}

	public List<Instrument> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<Instrument> instruments) {
		this.instruments = instruments;
	}

	
	public InstrumentCategory getInstrumentCategory() {
		return instrumentCategory;
	}

	public void setInstrumentCategory(InstrumentCategory instrumentCategory) {
		this.instrumentCategory = instrumentCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstrumentUsedType other = (InstrumentUsedType) obj;
		if (commonInfoType.getCode() == null) {
			if (other.getCommonInfoType().getCode() != null)
				return false;
		} else if (!commonInfoType.getCode().equals(other.getCommonInfoType().getCode()))
			return false;
		return true;
	}
}
