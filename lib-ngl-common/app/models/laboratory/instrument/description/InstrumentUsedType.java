package models.laboratory.instrument.description;

import java.util.List;

import play.api.modules.spring.Spring;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processes.description.dao.ProcessTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
public class InstrumentUsedType extends CommonInfoType{
	
	public List<Instrument> instruments;
	
	public InstrumentCategory category;

	public List<ContainerSupportCategory> inContainerSupportCategories;
	public List<ContainerSupportCategory> outContainerSupportCategories;
	
	
	public static CommonInfoTypeFinder<InstrumentUsedTypeDAO,InstrumentUsedType> find = new CommonInfoTypeFinder<InstrumentUsedTypeDAO,InstrumentUsedType>(InstrumentUsedTypeDAO.class); 
	
	public InstrumentUsedType() {
		super(InstrumentUsedTypeDAO.class.getName());
	}
	

	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Instrument);
	}
}
