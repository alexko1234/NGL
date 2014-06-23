package controllers.migration;

import java.util.List;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ContainerOld2;
import controllers.migration.models.ProjectOld;
import controllers.migration.models.SampleOld;
import fr.cea.ig.MongoDBDAO;

/**
 * Refactoring : Migration of stateCode & valid to state.code & Valuation.valid
 * @author dnoisett
 * 12-03-2014
 */

public class MigrationStateValuationRefactoring extends CommonController {

	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME+"_BCKrefactoring";
	private static final String PROJECT_COLL_NAME_BCK = InstanceConstants.PROJECT_COLL_NAME+"_BCKrefactoring";
	private static final String SAMPLE_COLL_NAME_BCK = InstanceConstants.SAMPLE_COLL_NAME+"_BCKrefactoring";

	public static void updateContainerCollection() {
		JacksonDBCollection<ContainerOld2, String> containersCollBck = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_BCK, ContainerOld2.class);
		if(containersCollBck.count() == 0){
			
			Logger.info(">>>>>>>>>>> 1.a Migration Container starts");

			backupContainerCollection();

			//container
			List<ContainerOld2> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld2.class).toList();
			Logger.debug("Migre "+oldContainers.size()+" CONTAINERS");
			for (ContainerOld2 container : oldContainers) {
				migreStateCode(container);
				migreValid(container);
			}
			Logger.info(">>>>>>>>>>> 1.b Migration Container end");
		} else {
			Logger.info("Migration CONTAINER already execute !");
		}		
	}
	
	
	public static void updateProjectCollection() {
		JacksonDBCollection<ProjectOld, String> projectsCollBck = MongoDBDAO.getCollection(PROJECT_COLL_NAME_BCK, ProjectOld.class);
		if(projectsCollBck.count() == 0){

			Logger.info(">>>>>>>>>>> 2.a Migration PROJECT starts");

			backupProjectCollection();

			List<ProjectOld> oldProjects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld.class).toList();
			Logger.debug("Migre "+oldProjects.size()+" PROJECTS");
			for (ProjectOld project : oldProjects) {
				migreStateCode(project);
			}
			Logger.info(">>>>>>>>>>> 2.b Migration PROJECT end");

		} else {
			Logger.info("Migration PROJECT already execute !");
		}		
	}
	
	
	
	public static void updateSampleCollection() {
		JacksonDBCollection<SampleOld, String> samplesCollBck = MongoDBDAO.getCollection(SAMPLE_COLL_NAME_BCK, SampleOld.class);
		if(samplesCollBck.count() == 0){

			Logger.info(">>>>>>>>>>> 3.a Migration SAMPLE starts");

			backupSampleCollection();

			List<SampleOld> oldSamples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, SampleOld.class).toList();
			Logger.debug("Migre "+oldSamples.size()+" SAMPLES");
			for (SampleOld sample : oldSamples) {
				migreValid(sample);
			}
			Logger.info(">>>>>>>>>>> 3.b Migration SAMPLE end");


		} else {
			Logger.info("Migration SAMPLE already execute !");
		}		
	}
	
	
	
	
	
	
	public static Result migration() {

		Logger.info("Start point of Migration REFACTORING");
		
		updateContainerCollection();

		updateProjectCollection();
		
		updateSampleCollection();		

		Logger.info("Migration REFACTORING finish");
		return ok("Migration REFACTORING Finish");
	}




	public static void migreStateCode(ContainerOld2 container) {
			
		Container c = new Container();		
		State state = new State(); 
		state.user = (null == container.traceInformation.modifyUser) ? container.traceInformation.createUser : container.traceInformation.modifyUser;
		state.date = (null == container.traceInformation.modifyUser) ? container.traceInformation.creationDate : container.traceInformation.modifyDate;
		c.state = state;
		
		if (container.stateCode != null) {
			c.state.code = container.stateCode;
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
					DBQuery.is("code", container.code), 
					DBUpdate.unset("stateCode")
					//a voir push ?
					.set("state", c.state) );
		}
		else {
			Logger.error("Missing state for "+container.code);
		}
	}


	public static void migreStateCode(ProjectOld project) {
		
		Project p = new Project();		
		State state = new State(); 
		state.user = (null == project.traceInformation.modifyUser) ? project.traceInformation.createUser : project.traceInformation.modifyUser;
		state.date = (null == project.traceInformation.modifyUser) ? project.traceInformation.creationDate : project.traceInformation.modifyDate;
		p.state = state;
		
		if (project.stateCode != null) {
			p.state.code = project.stateCode;
			
			MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
					DBQuery.is("code", project.code), 
					DBUpdate.unset("stateCode")
					//a voir push ?
					.set("state", p.state) );
		}
		else {
			Logger.error("Missing state for "+p.code);
		}
	}

	
	public static void migreValid(ContainerOld2 container) {
			
		Container c = new Container();		
		Valuation valuation = new Valuation();
		
		if (container.valid == null) {
			valuation.valid = TBoolean.UNSET;
		}		
		else {
			if (container.valid == Boolean.FALSE) {
				valuation.valid = TBoolean.FALSE;
			}
			else {
				valuation.valid = TBoolean.TRUE;
			}
		}
			
		if (valuation.valid != TBoolean.UNSET) {
			valuation.user = (null == container.traceInformation.modifyUser) ? container.traceInformation.createUser : container.traceInformation.modifyUser; 
			valuation.date = (null == container.traceInformation.modifyUser) ? container.traceInformation.creationDate : container.traceInformation.modifyDate;		
		}
		
		c.valuation = valuation;
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.is("code", container.code), 
				DBUpdate.unset("valid")
				.set("valuation", c.valuation) );
	}


	public static void migreValid(SampleOld sample) {
		
		Sample s = new Sample();		
		Valuation valuation = new Valuation();
		
		if (sample.valid == null) {
			valuation.valid = TBoolean.UNSET;
		}		
		else {
			if (sample.valid == Boolean.FALSE) {
				valuation.valid = TBoolean.FALSE;
			}
			else {
				valuation.valid = TBoolean.TRUE;
			}
		}
			
		if (valuation.valid != TBoolean.UNSET) {
			valuation.user = (null == sample.traceInformation.modifyUser) ? sample.traceInformation.createUser : sample.traceInformation.modifyUser; 
			valuation.date = (null == sample.traceInformation.modifyUser) ? sample.traceInformation.creationDate : sample.traceInformation.modifyDate;		
		}
		
		s.valuation = valuation;
		
		MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, 
				DBQuery.is("code", sample.code), 
				DBUpdate.unset("valid")
				.set("valuation", s.valuation) );
	}
	
	/*************************************************************/
	private static void backupContainerCollection() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld2.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}

	private static void backupProjectCollection() {
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" end");
	}

	private static void backupSampleCollection() {
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" start");
		MongoDBDAO.save(SAMPLE_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, SampleOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SAMPLE_COLL_NAME+" end");
	}
	/*************************************************************/

}


