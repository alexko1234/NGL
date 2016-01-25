package models.administration.authorisation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.administration.authorisation.description.dao.PermissionDAO;
import models.utils.Model;
import models.utils.dao.DAOException;

/**
 * 
 * @author michieli
 *
 */
public class Permission extends Model<Permission>{

	public String label;
	// Code de Permission (pr l'instant = reading ou writting)
	public String code;

	@JsonIgnore
	public static PermissionFinder find = new PermissionFinder();
	
	public static class PermissionFinder extends Finder<Permission>{

		public PermissionFinder() {
			super(PermissionDAO.class.getName());
		}
		public List<Permission> findByUserLogin(String aLogin) throws DAOException{
			return ((PermissionDAO)getInstance()).findByUserLogin(aLogin);
		}
	}
}
