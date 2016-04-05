package controllers.instruments.io.utils;

import java.io.File;

import org.mongojack.DBQuery;

import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.parameter.Index;
import models.utils.InstanceConstants;

public class InputHelper {
	
/* @author Fernando Dos santos
 * 
 */
	
	public static boolean isPlatePosition(ContextValidation contextValidation, String position, int plFormat, int lineNum){

		if ((position.length() < 2) || (position.length() > 3 )) {
			contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.position.unkwnown", lineNum, position);
			return false;
		}
		
		String row = position.substring(0,1);
		String column = position.substring(1);
		//Logger.info("DEBUG...row:"+ row + " -column:"+ column );
		
		int col = Integer.parseInt(column); // et si la string ne correspond pas a un nombre ???
		
		if (plFormat==96){
			if (row.matches("[A-H]") && (col>=1 && col<=12)){
				return true;
			} else { 
				contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.position.outofbonds", lineNum, position, plFormat);
				return false; 
			}
		} else if (plFormat==384){
	    	if (row.matches("[A-P]") &&  (col>=1 && col <=24)){
				return true;
			} else { 
				contextValidation.addErrors("Erreurs fichier", "experiments.msg.import.position.outofbonds", lineNum , position, plFormat);
				return false;
			}
	    	
		} else {
			// unsupported plate format
			return false;
		}
	}
	
	public static Index getIndexByName(String name, String typeCode){
		Index index  = MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("typeCode", typeCode).and(DBQuery.is("name", name)));
		return index;
	}

	
	//05/04/2016 ajoute "0" dans les positions ex A1=> A01
	public static String add02pos(String pos){
		String row=pos.substring(0,1);
		String col=pos.substring(1);
		if (col.length() == 1){ 
			return row+"0"+col ;
		}else{
			return pos;
		}
	}
	
	//utiliser container.line + container.column serait plus propre ???
	public static String getCodePosition(String containerCode) {
		return containerCode.substring(containerCode.indexOf("_")+1);
	}

}