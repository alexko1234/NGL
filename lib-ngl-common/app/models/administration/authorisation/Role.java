package models.administration.authorisation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.administration.authorisation.description.dao.RoleDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;

/**
 * 
 * @author michieli
 *
 */
public class Role extends Model<Role> {
	
	@JsonIgnore
	public static RoleFinder find = new RoleFinder();
	
	public String label;
	public List<Permission> permissions;
	
	// Missing contructor with DAO class name as super arg.
	
	@Override
	protected Class<? extends AbstractDAO<Role>> daoClass() {
		return RoleDAO.class;
	}

	// Doc generation produces an error with the parent unqualified name.
	// public static class RoleFinder extends Finder<Role> {
	public static class RoleFinder extends Model.Finder<Role,RoleDAO> {
		
//		public RoleFinder() {
//			super(RoleDAO.class.getName());
//		}
		public RoleFinder() { super(RoleDAO.class); }
		
		@Override
		public List<Role> findAll() throws DAOException {
//			return ((RoleDAO)getInstance()).findAll();
			return getInstance().findAll();
		}
		
		public List<Role> findByUserLogin(String aLogin) throws DAOException {
//			return ((RoleDAO)getInstance()).findByUserLogin(aLogin);
			return getInstance().findByUserLogin(aLogin);
		}
		
	}

}
