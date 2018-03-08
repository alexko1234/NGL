package models.laboratory.common.description;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
// import models.laboratory.common.description.dao.StateDAO;
import models.utils.ListObject;
import models.utils.Model;
// import models.utils.Model.Finder;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;
// import play.api.modules.spring.Spring;

/**
 * Class attributes common types
 * Represented by a table in the database with its own id
 * The subclasses are represented by tables in the database with the same id as the parent class
 * Relations with the protocols and instruments are accessible by the common_info_type table for the
 * experiment subclasses (experimentType, qualityCcontrolType ...)
 * 
 * @author ejacoby
 *
 */
public class CommonInfoType extends Model<CommonInfoType> {

	public String name; //used as label
	
	public Integer displayOrder; //position on display
	
	public List<State> states = new ArrayList<State>();
	
	public List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();

	public ObjectType objectType;
	
	public List<Institute> institutes = new ArrayList<Institute>();

	public Boolean active = Boolean.TRUE;
	
	public static CommonInfoTypeFinder find = new CommonInfoTypeFinder();	
	
	public CommonInfoType() {
		super(CommonInfoTypeDAO.class.getName());
	}

	protected CommonInfoType(String classNameDAO){
		super(classNameDAO);
	}

	@JsonIgnore
	public Map<String, PropertyDefinition> getMapPropertyDefinition() {
		Map<String, PropertyDefinition> mapProperties = new HashMap<String, PropertyDefinition>();
		for (PropertyDefinition propertyDefinition : propertiesDefinitions) {
			mapProperties.put(propertyDefinition.code, propertyDefinition);
		}
		return mapProperties;
	}

	public void setCommonInfoType(CommonInfoType commonInfoType) {
		this.id                    = commonInfoType.id;
		this.name                  = commonInfoType.name;
		this.code                  = commonInfoType.code;
		this.states                = commonInfoType.states;
		this.propertiesDefinitions = commonInfoType.propertiesDefinitions;
		this.objectType            = commonInfoType.objectType;
		this.institutes            = commonInfoType.institutes;
		//position on display
		this.displayOrder          = commonInfoType.displayOrder;
		this.active                = commonInfoType.active;
	}

	/*
	 * Return the PropertyDefinition that's contain all levels
	 * @param levels
	 * @return
	 */
	public List<PropertyDefinition> getPropertyDefinitionByLevel(Level.CODE... levels) {
		List<PropertyDefinition> proDefinitions = new ArrayList<PropertyDefinition>();
		for (PropertyDefinition propertyDefinition:this.propertiesDefinitions) {
			boolean containsAll = true;
			for (int i=0; i<levels.length; i++) {
				Level level = new Level(levels[i]);
				if (!propertyDefinition.levels.contains(level)) {
					containsAll = false;
					break;
				}						
			}
			if (containsAll) {
				proDefinitions.add(propertyDefinition);
			}
		}	
		return proDefinitions;
	}
	
	public static class CommonInfoTypeFinder extends Finder<CommonInfoType,CommonInfoTypeDAO> {

//		public CommonInfoTypeFinder() {
//			super(CommonInfoTypeDAO.class.getName());
//		}

		public CommonInfoTypeFinder() { super(CommonInfoTypeDAO.class); }
		public List<CommonInfoType> findByObjectTypeCode(CODE objectTypeCode) throws DAOException {
//			return ((CommonInfoTypeDAO)getInstance()).findByObjectTypeCode(objectTypeCode);
			return getInstance().findByObjectTypeCode(objectTypeCode);
		}
		
		public CommonInfoType findByExperimentTypeId(Long id) throws DAOException{
//			return ((CommonInfoTypeDAO)getInstance()).findByExperimentTypeId(id);
			return getInstance().findByExperimentTypeId(id);
		}
		
	}

//	public static class AbstractCommonInfoTypeFinder<T> extends Finder<T> { 
//	
////		public AbstractCommonInfoTypeFinder(Class<? extends AbstractDAOCommonInfoType> type) {
////			super(type.getName());
////		}
//		public AbstractCommonInfoTypeFinder(Class<? extends AbstractDAOCommonInfoType> type) { super(type); }
//	
//		public List<ListObject> findAllForList() throws DAOException {
//			return ((AbstractDAOCommonInfoType) getInstance()).findAllForList();
//		}
//		
//	}
	public static class AbstractCommonInfoTypeFinder<T extends CommonInfoType, U extends AbstractDAOCommonInfoType<T>> extends Finder<T,U> { 
		
//		public AbstractCommonInfoTypeFinder(Class<? extends AbstractDAOCommonInfoType> type) {
//			super(type.getName());
//		}
//		public AbstractCommonInfoTypeFinder(Class<? extends AbstractDAOCommonInfoType<T>> type) { super(type); }
		public AbstractCommonInfoTypeFinder(Class<U> type) { super(type); }
	
		public List<ListObject> findAllForList() throws DAOException {
//			return ((AbstractDAOCommonInfoType) getInstance()).findAllForList();
//			return ((AbstractDAOCommonInfoType<T>) getInstance()).findAllForList();
			return getInstance().findAllForList();
		}
		
	}

}
