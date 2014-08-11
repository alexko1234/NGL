package models.laboratory.common.description;


import java.util.List;

import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.common.description.dao.StateHierarchyDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


public class StateHierarchy extends Model<StateHierarchy> {
	
    public String childStateCode;
    public String childStateName;
    public String parentStateCode;
    public String objectTypeCode;

    public StateHierarchy(){
    	super(StateHierarchyDAO.class.getName());
    }

    public static StateHierarchyFinder find = new StateHierarchyFinder();

    
    public static class StateHierarchyFinder extends Finder<StateHierarchy> {

		public StateHierarchyFinder() {
		    super(StateHierarchyDAO.class.getName());
		}
		
		public List<StateHierarchy> findByObjectTypeCodeWithNames(CODE objectTypeCode) throws DAOException {
		    return ((StateHierarchyDAO) getInstance()).findByObjectTypeCodeWithNames(objectTypeCode);
		}
	
    }

}
