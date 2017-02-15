package controllers.migration.cns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationPropertiesExperimentErrorInput extends MigrationExperimentProperties{


	public static Result migration(String experimentTypeCode, String keyProperty, String previousExperimentTypeCode, boolean addToRun){

		//Get all experiment with key property
		List<Experiment> experiments = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("typeCode", experimentTypeCode)).toList();
		Logger.debug("Size of experiments "+experiments.size());

		//Get all output container code of experiment type
		Set<String> allOuputContainerCodes = getAllOutputContainerCode(experiments);
		Logger.debug("Size of allOuputContainerCodes "+allOuputContainerCodes.size());

		//Update all input
		for(Experiment experiment : experiments){
			//Logger.debug("Experiment "+experiment.code);
			experiment.atomicTransfertMethods.stream().forEach(atm->{
				if(atm.inputContainerUseds.size()==1 && atm.outputContainerUseds.size()==1){
					InputContainerUsed input = atm.inputContainerUseds.iterator().next();

					input.contents.stream().forEach(c->{
						c.properties.remove(keyProperty);
					});

					if(!allOuputContainerCodes.contains(input.code)){
						Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, input.code);
						if(container!=null){
							//Logger.debug("Remove property for "+container.code);
							updateContainerRemoveProperty(container, keyProperty,addToRun);
						}else{
							Logger.debug("Experiment "+experiment.code);
							Logger.error("Container code "+input.code+" not exists");
						}
					}else{
						Logger.debug("Experiment "+experiment.code);
						Logger.debug("WARNING Input "+input.code+" in outputContainerCodes");
					}

				}else{
					Logger.debug("Experiment "+experiment.code);
					Logger.error("ERROR not OneToOne "+experiment.code);
				}
			});
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
		}
		//Get all childs container from previousExperiment with key property
		List<Container> containers = new ArrayList<Container>();
		if(previousExperimentTypeCode!=null){
			//containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("fromTransformationTypeCodes", previousExperimentTypeCode).exists("fromTransfertCode")).toList();
			containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("fromTransformationTypeCodes", previousExperimentTypeCode)).toList();
		}else
			containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("fromTransfertCode").or(DBQuery.notExists("fromTransformationTypeCodes"),DBQuery.is("fromTransformationTypeCodes", null), DBQuery.size("fromTransformationTypeCodes", 0))).toList();

		Logger.debug("Size of child containers "+containers.size());

		for(Container container : containers){
			if(!container.fromTransformationTypeCodes.contains(experimentTypeCode)){
				List<Content> contents = container.contents.stream().filter(c-> c.properties!=null && c.properties.containsKey(keyProperty)).collect(Collectors.toList());
				if(contents!=null && contents.size()>0){
					Logger.debug("Find satellite "+container.code);
					updateContainerRemoveProperty(container, keyProperty,addToRun);
					//Get experiment to remove property
					if(container.fromTransfertCode!=null){
						Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, container.fromTransfertCode);
						if(experiment!=null){
							Logger.debug("Update experiment satellite "+experiment.code);
							experiment.atomicTransfertMethods.stream().forEach(atm->{
								atm.outputContainerUseds.stream().filter(output-> output.code.equals(container.code)).forEach(output->{
									if(output.contents!=null){
										output.contents.stream().forEach(c->{
											c.properties.remove(keyProperty);
										});
									}else{
										Logger.debug("No contents for "+output.code);
									}
								});

							});
							MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
						}else{
							Logger.error("No experiment for "+container.code);
						}
					}else{
						Logger.error("No Transfert Code "+container.code);
					}
				}
			}
		}

		return ok();

	}


	private static void updateContainerRemoveProperty(Container container, String keyProperty, boolean addToRun)
	{
		container.contents.stream().forEach(c->{
			c.properties.remove(keyProperty);
		});
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);

		//Get ReadSet to update
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("sampleOnContainer.containerCode", container.code)).toList();
		readSets.stream().forEach(r->{
			r.sampleOnContainer.properties.remove(keyProperty);
			if(addToRun){
				Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, r.runCode);
				run.properties.remove(keyProperty);
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
			}
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, r);
		});
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
