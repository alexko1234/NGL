package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.dao.StateDAO;
import models.utils.ListObject;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

// This link : {@link models.laboratory.common.description.State}

/**
 * Value of the possible state of type
 * 
 * @author ejacoby
 * @author dnoisett
 * 
 */
public class State extends Model<State> {

    public static final StateFinder find = new StateFinder();

    public String name;
    public boolean active;
    public Integer position;
    public StateCategory category;
    public List<ObjectType> objectTypes;
    
    public boolean display;
    public String functionnalGroup;
    
    public State() {
    	super(StateDAO.class.getName());
    }
    
	@Override
	protected Class<? extends AbstractDAO<State>> daoClass() {
		return StateDAO.class;
	}
    
    public static class StateFinder extends Finder<State,StateDAO> {

//		public StateFinder() {
//		    super(StateDAO.class.getName());
//		}
		public StateFinder() { super(StateDAO.class); }
	
		public List<State> findByCategoryCode(String code) throws DAOException {
//		    return ((StateDAO) getInstance()).findByCategoryCode(code);
		    return getInstance().findByCategoryCode(code);
		}
	
		public List<State> findByTypeCode(String typeCode) throws DAOException {
		    return getInstance().findByTypeCode(typeCode);
		}
	
		public boolean isCodeExistForTypeCode(String code, String typeCode)	throws DAOException {
//		    return ((StateDAO) getInstance()).isCodeExistForTypeCode(code, typeCode);
		    return getInstance().isCodeExistForTypeCode(code, typeCode);
		}
		
		public boolean isCodeExistForObjectTypeCode(String code, CODE objectTypeCode) throws DAOException {
//			return ((StateDAO) getInstance()).isCodeExistForObjectTypeCode(code,objectTypeCode);
			return getInstance().isCodeExistForObjectTypeCode(code,objectTypeCode);
		}
	
		public List<ListObject> findAllForContainerList() throws DAOException {
//		    return ((StateDAO) getInstance()).findAllForContainerList();
		    return getInstance().findAllForContainerList();
		}
	
		public List<State> findByObjectTypeCode(CODE objectTypeCode) throws DAOException {
//		    return ((StateDAO) getInstance()).findByObjectTypeCode(objectTypeCode);
		    return getInstance().findByObjectTypeCode(objectTypeCode);
		}
	    
		public List<State> findByDisplayAndObjectTypeCode(Boolean display, CODE objectTypeCode) throws DAOException {
//		    return ((StateDAO) getInstance()).findByDisplayAndObjectTypeCode(display, objectTypeCode);
		    return getInstance().findByDisplayAndObjectTypeCode(display, objectTypeCode);
		}
		
    }

}
