package models.administration.authorisation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.administration.authorisation.description.dao.PermissionDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;
// import play.Logger;

// TODO: comment

/**
 * 
 * @author michieli
 *
 */
public class Permission extends Model<Permission> {

	@JsonIgnore
	public static final PermissionFinder find = new PermissionFinder();
	
	public String label;
//	public String code;

	@Override
	protected Class<? extends AbstractDAO<Permission>> daoClass() {
		return PermissionDAO.class;
	}
	
	// Doc generation produces an error with the parent unqualified name.
	// public static class PermissionFinder extends Finder<Permission> {
	public static class PermissionFinder extends Finder<Permission,PermissionDAO> {

//		public PermissionFinder() {
//			super(PermissionDAO.class.getName());
//		}
		public PermissionFinder() { super(PermissionDAO.class); }
		
		public List<Permission> findByUserLogin(String aLogin) throws DAOException {
//			return ((PermissionDAO)getInstance()).findByUserLogin(aLogin);
			return getInstance().findByUserLogin(aLogin);
		}
		
	}

}
