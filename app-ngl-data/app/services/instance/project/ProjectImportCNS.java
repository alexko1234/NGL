package services.instance.project;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;

import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ProjectImportCNS extends AbstractImportDataCNS{

	public ProjectImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Project CNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createProject(contextError);
		deleteProject(contextError);
	}

	
	private void deleteProject(ContextValidation contextValidation) throws SQLException, DAOException{
		List<String> availableProjectCodes = limsServices.findProjectToCreate(contextValidation)
				.stream()
				.map(p -> p.code)
				.collect(Collectors.toList());
		
		MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).getCursor().forEach(p -> {
			
			if(!availableProjectCodes.contains(p.code)){
				logger.info("delete project : "+p.code);
				int nbSample = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("projectCodes", p.code)).count();
				if(nbSample == 0){
					MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, p.code);
				}else{
					logger.error("Try to delete project : "+p.code+" but sample exists :"+nbSample);
				}
			}
			
		});;
		
	}

	public static void createProject(ContextValidation contextValidation) throws SQLException, DAOException{
		
	List<Project> projects = limsServices.findProjectToCreate(contextValidation) ;
		
		for(Project limsProject:projects){
	
			Project nglProject = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, limsProject.code);
			if(nglProject != null){
				Builder update =  DBUpdate.set("name",limsProject.name).set("archive", limsProject.archive);
				if((limsProject.lastSampleCode != null && nglProject.lastSampleCode != null && 
						limsProject.lastSampleCode.compareTo(nglProject.lastSampleCode) > 0) || limsProject.lastSampleCode != null){
					update.set("lastSampleCode",limsProject.lastSampleCode);
					update.set("nbCharactersInSampleCode",limsProject.nbCharactersInSampleCode);
				}
				nglProject.traceInformation.setTraceInformation("ngl-data");
				MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
						DBQuery.is("code", limsProject.code),update);
				
			}else{
				InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,limsProject,contextValidation);
			}
		}
	
		//InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextValidation);
		
	}
}
