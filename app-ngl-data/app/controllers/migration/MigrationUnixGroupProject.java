package controllers.migration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import com.mongodb.MongoException;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationUnixGroupProject extends DocumentController<Project>{

	
	@Inject
	protected MigrationUnixGroupProject(NGLContext ctx) {
		super(ctx, InstanceConstants.PROJECT_COLL_NAME, Project.class);
	}

	
	public Result migration(String fileName) throws NumberFormatException, MongoException, IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		Logger.debug("File name "+fileName);
		String line = "";
		while ((line = reader.readLine()) != null) {
			String[] tabLine = line.split("\t");
			String codeProjet = tabLine[0];
			String unixGroup = tabLine[4];
			Logger.debug("Project "+codeProjet+" "+unixGroup);
			Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, codeProjet);
			Logger.debug("Get project "+project.code+" "+unixGroup);
			if(project.properties==null){
				project.properties=new HashMap<String,PropertyValue>();
			}
			
			if(unixGroup==null){
				project.properties.put("unixGroup", new PropertySingleValue("g-extprj"));
			}else{
				project.properties.put("unixGroup", new PropertySingleValue(unixGroup));
			}
			Logger.debug("Project "+project);
			MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME,Project.class, DBQuery.is("code", project.code),DBUpdate.set("properties",project.properties));
		}
		reader.close();		
		return ok("Migration UnixGroup finished");
	}
}
