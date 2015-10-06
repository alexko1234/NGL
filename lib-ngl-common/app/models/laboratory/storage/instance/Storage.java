package models.laboratory.storage.instance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;



import org.mongojack.MongoCollection;
import fr.cea.ig.DBObject;


/**
 * 
 * Instances stock represents all positions in box, shelf, storageDevic, room, floor and building 
 * Code name is like  
 * 
 * Stock are referenced in Container
 *  
 * @author mhaquell
 *
 */
@MongoCollection(name="Stock")
public class Storage extends DBObject implements IValidation{

	//Place
	public String buildingCode;
//	Not necessarry
	//public String floorCode;
	public String roomCode;
	
	//Conteneur
	public String storageDeviceCode; 
	public String shelf;
	
	//Box
	public String boxCode;
	public String x;
	public String y;
	
	// History stocks/support
	public List<StorageHistory> stockUsed;

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

	
}
