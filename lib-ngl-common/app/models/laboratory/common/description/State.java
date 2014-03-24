package models.laboratory.common.description;

import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.run.description.RunCategory;
import models.utils.ListObject;
import models.utils.Model;
import models.utils.dao.DAOException;

/**
 * Value of the possible state of type
 * 
 * @author ejacoby, dnoisett
 * 
 */
public class State extends Model<State> {

    public String name;
    public boolean active;
    public Integer position;
    public StateCategory category;
    public List<ObjectType> objectTypes;
    

    public static StateFinder find = new StateFinder();

    public State() {
	super(StateDAO.class.getName());
    }

    public static class StateFinder extends Finder<State> {

	public StateFinder() {
	    super(StateDAO.class.getName());
	}

	public List<State> findByCategoryCode(String code) throws DAOException {
	    return ((StateDAO) getInstance()).findByCategoryCode(code);
	}

	public List<State> findByTypeCode(String typeCode) throws DAOException {
	    return ((StateDAO) getInstance()).findByTypeCode(typeCode);
	}

	public boolean isCodeExistForTypeCode(String code, String typeCode)
		throws DAOException {
	    return ((StateDAO) getInstance()).isCodeExistForTypeCode(code,
		    typeCode);
	}

	public List<ListObject> findAllForContainerList() throws DAOException {
	    return ((StateDAO) getInstance()).findAllForContainerList();
	}

	public List<State> findByObjectTypeCode(CODE objectTypeCode)
		throws DAOException {
	    return ((StateDAO) getInstance())
		    .findByObjectTypeCode(objectTypeCode);

		}
    }

}
