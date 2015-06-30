package controllers.migration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;













import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyListValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.mvc.Result;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class MigrationNbUsefulCyclesRun extends CommonController{
	
	public static Result migration() throws MongoException, ParseException{
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("lanes", 1);
		keys.put("state", 1);
		
		MongoDBResult<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.exists("code"), keys);
		DBCursor<Run> cursor = runs.cursor;
		while(cursor.hasNext()){
			Run run = cursor.next();
			if(run.lanes != null){
				for(Lane lane: run.lanes){
					if(null != lane.treatments.get("ngsrg")){
						Treatment ngsrg = lane.treatments.get("ngsrg");
						
						Map<String, PropertyValue> properties = ngsrg.results.get("default");
						Map<String, PropertyValue> newProperties = new HashMap<String, PropertyValue>();
						boolean yes = true;
						if(properties.containsKey("nbCycleRead1")){
							newProperties.put("nbUsefulCycleRead1", properties.get("nbCycleRead1"));							
						}else{
							yes = false;
							Logger.warn("missing nbCycleRead1 for "+run.code+" / "+lane.number);
						}
						
						if(properties.containsKey("nbCycleRead2")){
							newProperties.put("nbUsefulCycleRead2", properties.get("nbCycleRead2"));
						}else{
							yes = false;							
							Logger.warn("missing nbCycleRead2 for "+run.code+" / "+lane.number);
						}
						
						if(properties.containsKey("nbCycleReadIndex1")){
							if(((Integer)properties.get("nbCycleReadIndex1").value).intValue() == 7){
								newProperties.put("nbUsefulCycleReadIndex1", new PropertySingleValue((Integer)properties.get("nbCycleReadIndex1").value - 1));
							}else{
								newProperties.put("nbUsefulCycleReadIndex1", properties.get("nbCycleReadIndex1"));
							}
						}else{
							yes = false;							
							Logger.warn("missing nbCycleReadIndex1 for "+run.code+" / "+lane.number);
						}
						
						if(properties.containsKey("nbCycleReadIndex2")){	
							if(((Integer)properties.get("nbCycleReadIndex2").value).intValue() == 7){
								newProperties.put("nbUsefulCycleReadIndex2", new PropertySingleValue((Integer)properties.get("nbCycleReadIndex2").value - 1));
							}else{
								newProperties.put("nbUsefulCycleReadIndex2", properties.get("nbCycleReadIndex2"));
							}
						}else{
							yes = false;							
							Logger.warn("missing nbCycleReadIndex2 for "+run.code+" / "+lane.number);
						}
						
						if(yes){
							properties.putAll(newProperties);							
							MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
									DBQuery.and(DBQuery.is("code", run.code), DBQuery.is("lanes.number", lane.number)),
									DBUpdate.set("lanes.$.treatments.ngsrg", ngsrg));														
							
							MongoDBResult<ReadSet> readsets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
									DBQuery.and(DBQuery.is("runCode", run.code), DBQuery.is("laneNumber", lane.number)));
							
							DBCursor<ReadSet> rsCursor = readsets.cursor;
							while(rsCursor.hasNext()){
								ReadSet rs = rsCursor.next();
								Treatment rsngsrg =  rs.treatments.get("ngsrg");
								if(null != rsngsrg){
									rsngsrg.results.get("default").putAll(newProperties);
									MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
											DBQuery.is("code", rs.code),
											DBUpdate.set("treatments.ngsrg", rsngsrg));
								}else{
									Logger.warn("no ngsrg for "+rs.code);
								}
							}
							
						}else{
							Logger.error("not update "+run.code+" / "+lane.number);
						}
					}else{
						Logger.warn("no ngsrg for "+run.code+" / "+lane.number+" / "+run.state.code);
					}
				
				}
			}else{
				Logger.warn("no lanes for "+run.code);
			}
			
		}
		
		
		return ok();
	}
	

}
