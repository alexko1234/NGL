package services.instance;

import java.util.regex.Pattern;

import play.Logger;

public class ImportDataUtilGET {

	/**
	 * check a string for jFlow unauthorized characters
	 * 
	 * @author okorovina
	 * @return true if string is void or matched with all special characters except "-" 
	 * also it start or finish by "-" or contains "--"
	 **/
	public static Boolean Checkname(String name) {
		String regexp = "^(-).*|.*[^a-zA-Z0-9-].*|.*(-){2,}.*|.*(-)$";
		boolean check;
		if (name.equals("")){
			return true;
		}else{
			check = Pattern.matches(regexp, name.trim());
			Logger.debug("Checkname " + name + " = " + check);
		}
		return check;
	}
}
