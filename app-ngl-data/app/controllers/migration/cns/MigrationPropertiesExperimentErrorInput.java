package controllers.migration.cns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationPropertiesExperimentErrorInput extends MigrationExperimentProperties{

	public static Result migration(String experimentTypeCode, String keyProperty, boolean addToRun){

		//Get all experiment with key property
		List<Experiment> experiments = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("typeCode", experimentTypeCode)).toList();
		Logger.debug("Size of experiments "+experiments.size());

		//Get all output container code of experiment type
		Set<String> allOuputContainerCodes = getAllOutputContainerCode(experiments);
		Logger.debug("Size of allOuputContainerCodes "+allOuputContainerCodes.size());

		//Update all input
		for(Experiment experiment : experiments){
			Logger.debug("Experiment "+experiment.code);
			experiment.atomicTransfertMethods.stream().forEach(atm->{
				atm.inputContainerUseds.stream().forEach(input->{
					for(Content content : input.contents){
						if(content.properties.containsKey(keyProperty)){
							Logger.debug("Remove property for container "+input.code);
							content.properties.remove(keyProperty);
							
						}
					}
					if(!allOuputContainerCodes.contains(input.code)){
						Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, input.code);
						Logger.debug("Update container "+container.code);
						if(container!=null){
							//add property to container
							container.contents.stream().forEach(c->{
								c.properties.remove(keyProperty);
							});
							MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);

							//Get ReadSet to update
							List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("sampleOnContainer.containerCode", container.code)).toList();
							readSets.stream().forEach(r->{
								Logger.debug("Update ReadSet"+r.code);
								r.sampleOnContainer.properties.remove(keyProperty);
								if(addToRun){
									Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, r.runCode);
									Logger.debug("Update run code "+run.code);
									run.properties.remove(keyProperty);
									MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
								}
								MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, r);
							});


						}
					}else{
						Logger.debug("WARNING in outputContainerCodes");
					}
				});
				
			});
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
		}

		return ok();

	}

	private static Set<String> getAllOutputContainerCode(List<Experiment> experiments)
	{
		Set<String> allOutputContainerCodes = new HashSet<String>();
		List<String> containerCodes = new ArrayList<String>();
		int nb=1;
		for(Experiment experiment : experiments){
			experiment.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				atm.outputContainerUseds.stream().forEach(output->{
					//Get list of all Container in process
					containerCodes.add(output.code);
					getListContainerCode(output.locationOnContainerSupport.code, containerCodes);
				});
			});
			nb++;
		}
		allOutputContainerCodes.addAll(containerCodes);
		return allOutputContainerCodes;
	}
}
