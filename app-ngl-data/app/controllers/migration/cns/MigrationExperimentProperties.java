package controllers.migration.cns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.AbstractContainerUsed;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationExperimentProperties extends CommonController{

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");

	/**
	 * Propagation d'une propriété d'experiment au niveau content (containers et readSets)
	 * @param experimentTypeCode
	 * @param keyProperty
	 * @return
	 */
	public static Result migration(String experimentTypeCode, String keyProperty, String newKeyProperty){

		//backupContainerCollection();
		//backupReadSetCollection();

		//Get list experiment
		List<Experiment> experiments = getListExperiments(DBQuery.is("typeCode", experimentTypeCode));

		//Get list experiment with no experiment properties
		for(Experiment exp : experiments){
			checkATMExperiment(exp);
			checkInputExperimentProperties(exp, newKeyProperty);
		}

		//Get all inputQuantity to change to libraryInputQuantity
		for(Experiment exp : experiments){
			Logger.debug("Code experiment "+exp.code);
			Logger.debug("Classe "+OneToOneContainer.class.getName());
			exp.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				Logger.debug("ATM "+atm.getClass());
				atm.inputContainerUseds.stream().filter(input->input.experimentProperties!=null && input.experimentProperties.containsKey(keyProperty)).forEach(input->{
					Logger.debug("Update property");
					input.experimentProperties.put(newKeyProperty, input.experimentProperties.get(keyProperty));
					input.experimentProperties.remove(keyProperty);
					PropertyValue propValue = input.experimentProperties.get(newKeyProperty);
					//add property to contents properties to inputContainerUsed
					updateContainerContents(input, newKeyProperty, propValue);

					//Get container from input
					updateContainer(input.code, newKeyProperty, propValue);

					updateOutputContainer(atm,newKeyProperty,propValue);
				});
			});

			//Update experiment in database
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}



		return ok();
	}

	protected static List<Experiment> getListExperiments(Query query)
	{
		return MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, query).toList();
	}

	protected static void checkATMExperiment(Experiment experiment)
	{
		experiment.atomicTransfertMethods.stream().filter(atm->!atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
			Logger.debug("Experiment "+experiment.code+" ATM not one to one ");
		});
	}

	protected static void checkInputExperimentProperties(Experiment experiment, String keyProperty)
	{
		experiment.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
			atm.inputContainerUseds.stream().filter(input->input.experimentProperties==null).forEach(input->{
				Logger.debug("Experiment "+experiment.code+" inputContainer "+input.code+" no experiment property");
			});
		});
		experiment.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
			atm.inputContainerUseds.stream().filter(input->input.experimentProperties!=null && !input.experimentProperties.containsKey(keyProperty)).forEach(input->{
				Logger.debug("Experiment "+experiment.code+" inputContainer "+input.code+" no property "+keyProperty);
			});
		});
	}

	protected static void updateOutputContainer(AtomicTransfertMethod atm, String keyProperty, PropertyValue propValue)
	{
		//Get outputContainer
		atm.outputContainerUseds.stream().forEach(output->{
			Logger.debug("Get outputContainerCode "+output.code);
			updateContainer(output.code, keyProperty, propValue);
			//Get list of all Container in process
			List<String> containerCodes = new ArrayList<String>();
			getListContainerCode(output.locationOnContainerSupport.code, containerCodes);
			for(String codeContainer : containerCodes){
				Logger.debug("Update container code "+codeContainer);
				updateContainer(codeContainer, keyProperty, propValue);
			}
		});
	}

	protected static void updateContainerContents(AbstractContainerUsed container, String keyProperty, PropertyValue propValue)
	{
		if(container.contents!=null){
			if(container.contents.size()>1)
				Logger.error("Multiple contents ");
			if(container.contents.size()>0){
				container.contents.stream().filter(content->!content.properties.containsKey(keyProperty)).forEach(content->{
					Logger.debug("add property to content "+content.sampleCode);
					content.properties.put(keyProperty, propValue);
				});
			}
		}
	}

	protected static void getListContainerCode(String codeContainer, List<String> listContainerCode)
	{
		List<Experiment> experiments = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.in("inputContainerSupportCodes", codeContainer)).toList();
		for(Experiment exp : experiments){
			if(exp.outputContainerSupportCodes!=null && exp.outputContainerSupportCodes.size()>0){
				//Get outputContainerCode from outputContainerSupportCode
				for(String outputContainerSupportCode : exp.outputContainerSupportCodes){
					List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", outputContainerSupportCode)).toList();
					for(Container container : containers){
						listContainerCode.add(container.code);
						getListContainerCode(container.code, listContainerCode);
					}
				}
			}
		}
	}

	protected static void updateContainer(String codeContainer, String newKeyProperty, PropertyValue propValue)
	{
		//Get container
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, codeContainer);
		if(container!=null){
			//add property to container
			container.contents.stream().forEach(c->{
				Logger.debug("Update container "+codeContainer+" for content "+c.sampleCode);
				c.properties.put(newKeyProperty, propValue);
			});
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);

			//Get ReadSet to update
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("sampleOnContainer.containerCode", container.code)).toList();
			readSets.stream().forEach(r->{
				Logger.debug("Update ReadSet"+r.code);
				r.sampleOnContainer.properties.put(newKeyProperty, propValue);
			});

			for(ReadSet readSet : readSets){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
			}
		}
	}

	protected static void backupContainerCollection() {
		String backupName = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_"+sdf.format(new java.util.Date());

		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}

	protected static void backupReadSetCollection() {
		String backupName = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_"+sdf.format(new java.util.Date());
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);

		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("sampleOnContainer"), keys).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}

}
