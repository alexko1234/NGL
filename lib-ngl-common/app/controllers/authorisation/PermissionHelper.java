package controllers.authorisation;

/**
 * Permission and team manager
 * 
 * @author ydeshayes
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Query;

import models.administration.authorisation.Permission;
import models.administration.authorisation.Role;
import models.administration.authorisation.Team;
import models.administration.authorisation.User;
import play.mvc.Http.Session;

public class PermissionHelper {
	/**
	 * 
	 * @param ses the user session
	 * @param codePermission the code of the permission that you want to verify
	 * @return true if the user can access to the resources
	 */
	public static boolean checkPermission(Session ses, String codePermission) {
		User user = User.find.where().eq("login", ses.get("CAS_FILTER_USER")).findUnique();  
		if(user!=null) {
			for(Role role:user.roles) {
				for(models.administration.authorisation.Permission perm:role.permissions) {
					if(perm.code.equals(codePermission))
						return true;
				}
			}
		}
		return false;
		//return true; // pour les tests
	}
	
	/**
	 * 
	 * @param ses the user session
	 * @param codePermission the code of the permissions that you want to verify
	 * @param allPermission if user need to have all the permission(true) or just one of these(false)
	 * @return true if the user can access to the resources
	 */
	public static boolean checkPermission(Session ses,  ArrayList<String> codePermission, String allPermission) {
		User user = User.find.where().eq("login", ses.get("CAS_FILTER_USER")).findUnique();  
		if(user!=null) {
			if(allPermission.equals("false")){
				for(Role role:user.roles) {
					for(models.administration.authorisation.Permission perm:role.permissions) {
						for(String permissionAsk:codePermission){
							if(perm.code.equals(permissionAsk))
								return true;
						}
					}
				}
			}else{
				int i=0;
				for(Role role:user.roles) {
					for(models.administration.authorisation.Permission perm:role.permissions) {
						for(String permissionAsk:codePermission){	
							if(perm.code.equals(permissionAsk))
								i++;
						}
					}
				
			}
			return i == codePermission.size();	
		}
		}
		return false;
	}
	
	public static boolean isTechnical(int id) {
		return getUser(id).technicaluser == 1;
	}
	/**
	 * 
	 * @param ses the user session
	 * @param varteam the name of the team you want to verify
	 * @return true if the user is in the team
	 */
	public static boolean checkTeam(Session ses, String varteam) {
		User user = User.find.where().eq("login", ses.get("CAS_FILTER_USER")).findUnique();  
		if(user!=null) {
			for(Team team:user.teams) {
				if(team.nom.equals(varteam))
					return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @param ses the user session
	 * @param teams the name of the teams you want to verify
	 * @return  if the user is in one of these team
	 */
	public static boolean checkTeam(Session ses, ArrayList<String> teams) {
		//By default -> [""]
		if(teams.size() < 2 && teams.get(0).equalsIgnoreCase(""))
			return true;
		
		User user = User.find.where().eq("login", ses.get("CAS_FILTER_USER")).findUnique();  
		if(user!=null) {
			for(Team team:user.teams) {
				for(String varteam:teams){
					if(team.nom.equals(varteam))
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean existPerm(int idPerm,String id) {
		models.administration.authorisation.Role role = models.administration.authorisation.Role.find.byId(idPerm);
		models.administration.authorisation.Permission permission = models.administration.authorisation.Permission.find.byId(Integer.parseInt(id));
		
		return role.permissions.contains(permission);
	}
	
	public static boolean existRole(int idUser,String id) {
		models.administration.authorisation.User user = models.administration.authorisation.User.find.byId(idUser);
		Role role = models.administration.authorisation.Role.find.byId(Integer.parseInt(id));
		
		return user.roles.contains(role);
	}
	
	public static boolean existSingleRole(int id) {
		Query<models.administration.authorisation.Role> role = models.administration.authorisation.Role.find.where("id="+id);
		role.findRowCount();
		return role.findRowCount()!=0;
	}
	
	public static boolean existSinglePerm(int id) {
		Query<models.administration.authorisation.Permission> perm = models.administration.authorisation.Permission.find.where("id="+id);
		perm.findRowCount();
		return perm.findRowCount()!=0;
	}
	
	public static boolean existUser(int idUser) {
		Query<models.administration.authorisation.User> user = models.administration.authorisation.User.find.where("id="+idUser);
		user.findRowCount();
		return user.findRowCount()!=0;
	}
	
	public static List<models.administration.authorisation.Permission> getAllPermission() {
		return models.administration.authorisation.Permission.find.all();
	}
	
	public static List<models.administration.authorisation.User> getAllUser() {
		return models.administration.authorisation.User.find.all();
	}
	
	public static List<models.administration.authorisation.Team> getAllTeam() {
		return models.administration.authorisation.Team.find.all();
	}
	
	public static List<models.administration.authorisation.Role> getAllRole() {
		return models.administration.authorisation.Role.find.all();
	}
	
	public static models.administration.authorisation.Role getRole(int id) {
		return models.administration.authorisation.Role.find.byId(id);
	}
	
	public static models.administration.authorisation.Team getTeam(int id) {
		return models.administration.authorisation.Team.find.byId(id);
	}
	
	
	public static Permission getpermission(int id) {
		return models.administration.authorisation.Permission.find.byId(id);
	}
	
	public static Permission getpermission(String code) {
		return models.administration.authorisation.Permission.find.where("code LIKE "+code).findUnique();
	}
	
	public static User getUser(int id) {
		return models.administration.authorisation.User.find.byId(id);
	}
	
	
	public static Map<String, String> getMapRole() {
		  Map<String,String> map = new HashMap<String,String>();
		List<Role> liste = getAllRole();
		for(models.administration.authorisation.Role role: liste)
				map.put(String.valueOf(role.id), role.label);
		
		return map;
	}
	
	public static Map<String, String> getMapPerm() {
		Map<String,String> map = new HashMap<String,String>();
		List<models.administration.authorisation.Permission> liste = getAllPermission();
		for(models.administration.authorisation.Permission perm: liste)
				map.put(String.valueOf(perm.id), perm.label);
		
		return map;
   }
	
	public static Map<String,String> getMapTeam() {
		Map<String,String> map = new HashMap<String,String>();
		List<models.administration.authorisation.Team> liste = getAllTeam();
		for(models.administration.authorisation.Team team: liste)
				map.put(String.valueOf(team.id), team.nom);
		
		return map;
	}
		
	
}