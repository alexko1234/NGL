package controllers.migration;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;



import models.laboratory.common.instance.property.PropertyListValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.mvc.Result;
import rules.services.RulesException;
import rules.services.RulesServices;
import rules.services.RulesServices6;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;


public class MigrationLibProcessTypeCodesRun extends CommonController{
	
	public static Result migration() throws MongoException, ParseException{
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("properties", 1);
		keys.put("projectCodes", 1);
		keys.put("sampleCodes", 1);
		keys.put("containerSupportCode", 1);
		
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.exists("code"), keys).toList();
		
		for(Run run : runs){
			ContainerSupport cs = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, run.containerSupportCode);
			if(null != cs){
			
				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", run.containerSupportCode)).toList();
				
				Set<String> libProcessTypeCodes = new TreeSet<String>();
				for(Container container:containers){
					for(Content content:container.contents){
						if(content.properties.containsKey("libProcessTypeCode")){
							libProcessTypeCodes.add((String)(content.properties.get("libProcessTypeCode").value));
						}
					}
				}
				if(libProcessTypeCodes.size() > 0){
						run.properties.put("libProcessTypeCodes", new PropertyListValue(new ArrayList(libProcessTypeCodes)));
						MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
								DBQuery.is("code", run.code),
								DBUpdate.set("properties", run.properties));
				}
				
				if(run.projectCodes.size() == 0 || run.sampleCodes.size() == 0)
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", run.code),
						DBUpdate.set("projectCodes",new TreeSet<String>(cs.projectCodes))
							.set("sampleCodes",new TreeSet<String>(cs.sampleCodes)));
			}
			
		}
		
		
		return ok();
	}
	

}
