package models.laboratory.stock.instance;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;

import models.utils.IValidation;
import net.vz.mongodb.jackson.MongoCollection;
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
public class Stock extends DBObject implements IValidation{

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
	public List<StockUsed> stockUsed;

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		// TODO Auto-generated method stub
		
	}

	
}
