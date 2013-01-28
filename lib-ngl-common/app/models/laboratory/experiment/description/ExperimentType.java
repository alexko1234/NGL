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

public class ExperimentType extends CommonInfoType implements IDynamicType{

	public Long id;
	
	public List<ExperimentType> nextExperimentTypes=new ArrayList<ExperimentType>();

	public List<Protocol> protocols; 
	
	public List<InstrumentUsedType> instrumentTypes;
	
	public ExperimentType() {
		super();
	}

	public ExperimentType(List<ExperimentType> nextExperimentTypes,
			List<Protocol> protocols, List<InstrumentUsedType> instrumentTypes) {
		super();
		this.nextExperimentTypes = nextExperimentTypes;
		this.protocols = protocols;
		this.instrumentTypes = instrumentTypes;
	}

	@Override
	public CommonInfoType getInformations() {		
		return this;
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
			mapExperimentTypes.put(expType.id.toString(), expType.name);
		}
		return mapExperimentTypes;
	}
	
	public static Map<String, String> getMapExperimentTypes(long filterId)
	{
		Map<String, String> mapExperimentTypes = new HashMap<String, String>();
		ExperimentTypeDAO experimentTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		for(ExperimentType expType : experimentTypeDAO.findAll()){
			if(!expType.id.equals(filterId))
				mapExperimentTypes.put(expType.id.toString(), expType.name);
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
	
	
}
