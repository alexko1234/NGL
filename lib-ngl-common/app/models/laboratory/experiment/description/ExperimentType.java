package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.IDynamicType;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.InstrumentUsedType;
import play.modules.spring.Spring;

public class ExperimentType implements IDynamicType{

	public Long id;
	
	public List<ExperimentType> nextExperimentTypes=new ArrayList<ExperimentType>();

	public List<Protocol> protocols; 
	
	public List<InstrumentUsedType> instrumentTypes;
	
	public CommonInfoType commonInfoType;

	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}
	
	public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
        for(ExperimentType c: experimentTypeDAO.findAll()) {
            options.put(c.getInformations().id.toString(), c.getInformations().name);
        }
        return options;
    }

	public static Map<String, String> getMapExperimentTypes()
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		for(ExperimentType expType : experimentTypeDAO.findAll()){
			mapExperimentTypes.put(expType.id.toString(), expType.commonInfoType.name);
		}
		return mapExperimentTypes;
	}
	
	public static Map<String, String> getMapExperimentTypes(long filterId)
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		for(ExperimentType expType : experimentTypeDAO.findAll()){
			if(!expType.id.equals(filterId))
				mapExperimentTypes.put(expType.id.toString(), expType.commonInfoType.name);
		}
		return mapExperimentTypes;
	}
	
	public static ExperimentType findByCommonInfoType(Long idCommonInfoType)
	{
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return expTypeDAO.findByCommonInfoType(idCommonInfoType);
	}
	
	public static ExperimentType findByCode(String code)
	{
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return expTypeDAO.findByCode(code);
	}
	
	public static ExperimentType findExpTypeById(long id)
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.findById(id);
	}
	
	public ExperimentType add()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.add(this);
	}
	
	public void update()
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		experimentTypeDAO.update(this);
	}
	@Override
	public long getIdType() {
		return id;
	}
	
	public IDynamicType findById(long id)
	{
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		return experimentTypeDAO.findById(id);
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentType other = (ExperimentType) obj;
		if (commonInfoType.code == null) {
			if (other.commonInfoType.code != null)
				return false;
		} else if (!commonInfoType.code.equals(other.commonInfoType.code))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	
	
	
}
