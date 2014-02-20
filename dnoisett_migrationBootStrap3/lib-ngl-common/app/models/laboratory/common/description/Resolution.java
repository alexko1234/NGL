package models.laboratory.common.description;

import java.util.List;
import models.laboratory.common.description.dao.ResolutionDAO;
import models.utils.Model;
import models.utils.dao.DAOException;


/**
 * Value of the resolution of final possible state
 * @author ejacoby
 *
 */
public class Resolution extends Model<Resolution>{

	public String name;
	public ResolutionCategory category;
	public List<Institute> institutes;
	public List<ObjectType> objectTypes;
	
	
	public static ResolutionFinder find = new ResolutionFinder();
	
	public Resolution() {
		super(ResolutionDAO.class.getName());
	}

	public static class ResolutionFinder extends Finder<Resolution> {

		public ResolutionFinder() {
			super(ResolutionDAO.class.getName());
		}

		public List<Resolution> findByCategoryCode(String code)
				throws DAOException {
			return ((ResolutionDAO) getInstance()).findByCategoryCode(code);
		}
		
		public List<Resolution> findByTypeCode(String code)
				throws DAOException {
			return ((ResolutionDAO) getInstance()).findByTypeCode(code);
		}

		public boolean isCodeExistForTypeCode(String code, String typeCode) throws DAOException {
			return ((ResolutionDAO) getInstance()).isCodeExistForTypeCode(code, typeCode);
		}
		
		public List<Resolution> findByObjectTypeCode(ObjectType.CODE objectTypeCode) throws DAOException {
		    return ((ResolutionDAO) getInstance()).findByObjectTypeCode(objectTypeCode);
		}
	
	}
	
}
