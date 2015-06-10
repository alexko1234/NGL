package models.administration.authorisation;

import java.util.List;

import models.utils.Model;


public class Role extends Model<Model>{
	
	public String label;
	public List<Permission> permissions;

}
