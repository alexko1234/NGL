package models.description.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.description.IDynamicType;
import models.description.common.CommonInfoType;
import models.description.experiment.dao.ExperimentTypeDAO;
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
            options.put(c.getInformations().getId().toString(), c.getInformations().getName());
        }
        return options;
    }

	public static Map<String, String> getMapExperimentTypes()
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		for(ExperimentType expType : experimentTypeDAO.findAll()){
			mapExperimentTypes.put(expType.getId().toString(), expType.getCommonInfoType().getName());
		}
		return mapExperimentTypes;
	}
	
	public static Map<String, String> getMapExperimentTypes(long filterId)
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		for(ExperimentType expType : experimentTypeDAO.findAll()){
			if(expType.getId()!=filterId)
				mapExperimentTypes.put(expType.getId().toString(), expType.getCommonInfoType().getName());
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ExperimentType> getNextExperimentTypes() {
		return nextExperimentTypes;
	}

	public void setNextExperimentTypes(List<ExperimentType> nextExperimentTypes) {
		this.nextExperimentTypes = nextExperimentTypes;
	}

	public List<Protocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<Protocol> protocols) {
		this.protocols = protocols;
	}

	public List<InstrumentUsedType> getInstrumentTypes() {
		return instrumentTypes;
	}

	public void setInstrumentTypes(List<InstrumentUsedType> instrumentTypes) {
		this.instrumentTypes = instrumentTypes;
	}

	public CommonInfoType getCommonInfoType() {
		return commonInfoType;
	}

	public void setCommonInfoType(CommonInfoType commonInfoType) {
		this.commonInfoType = commonInfoType;
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
		if (commonInfoType.getCode() == null) {
			if (other.getCommonInfoType().getCode() != null)
				return false;
		} else if (!commonInfoType.getCode().equals(other.getCommonInfoType().getCode()))
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
