package models.laboratory.common.description;


import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.dao.StateHierarchyDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;


public class StateHierarchy extends Model<StateHierarchy> {
	
    public static final StateHierarchyFinder find = new StateHierarchyFinder();
    
    public String childStateCode;
    public String childStateName;
    public String parentStateCode;
    public String objectTypeCode;
    public Integer position;
    public String functionnalGroup; 

    public StateHierarchy() {
    	super(StateHierarchyDAO.class.getName());
    }

	@Override
	protected Class<? extends AbstractDAO<StateHierarchy>> daoClass() {
		return StateHierarchyDAO.class;
	}

    public static class StateHierarchyFinder extends Finder<StateHierarchy,StateHierarchyDAO> {

//		public StateHierarchyFinder() {
//		    super(StateHierarchyDAO.class.getName());
//		}
		public StateHierarchyFinder() { super(StateHierarchyDAO.class); }
		
		public List<StateHierarchy> findByObjectTypeCode(CODE objectTypeCode) throws DAOException {
//		    return ((StateHierarchyDAO) getInstance()).findByObjectTypeCode(objectTypeCode);
		    return getInstance().findByObjectTypeCode(objectTypeCode);
		}
	
    }

}
