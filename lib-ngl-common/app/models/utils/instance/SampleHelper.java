package models.utils.instance;

import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import fr.cea.ig.MongoDBDAO;

public class SampleHelper {
	
	
	public static void updateSampleProperties(String sampleCode, Map<String,PropertyValue>  properties){
		
		for(Entry<String,PropertyValue> entry :properties.entrySet()){
			
			MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
					DBQuery.is("code",sampleCode),
					DBUpdate.set("properties."+entry.getKey(),entry.getValue()));
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Run.class, 
					 DBQuery.is("contents.sampleCode", sampleCode),
					DBUpdate.set("contents.$.properties."+entry.getKey(),entry.getValue()),true);					
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
					DBQuery.is("sampleOnContainer.sampleCode", sampleCode),
					DBUpdate.set("sampleOnContainer.properties."+entry.getKey(),entry.getValue()),true);
			
		}
		
	}

}
