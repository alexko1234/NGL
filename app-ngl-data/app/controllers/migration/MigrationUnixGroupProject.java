package controllers.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.jdbc.core.RowMapper;

import fr.cea.ig.MongoDBDAO;
import models.LimsCNSDAO;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import play.api.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Result;

public class MigrationUnixGroupProject extends Controller{

	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	
	public static Result migration() {
		
		List<Project> results = limsServices.jdbcTemplate.query("pl_ProjetToNGL ",new Object[]{} 
		,new RowMapper<Project>() {

			@SuppressWarnings("rawtypes")
			public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
				//Get code and unix groupe
				String code = rs.getString(2).trim();
				Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, code);
				String unixGroup =  rs.getString(6);
				
				if(unixGroup==null){
					project.properties.put("unixGroup", new PropertySingleValue("g-extprj"));
				}else{
					project.properties.put("unixGroup", new PropertySingleValue(unixGroup));
				}
				
				return project;
			}
		});
		
		for(Project project : results){
			MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME,Container.class, DBQuery.is("code", project.code),DBUpdate.set("project.properties",project.properties));
		}
		
		return ok("Migration UnixGroup finished");
	}
}
