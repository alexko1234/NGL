package controllers.migration.cns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationExperimentPropertiesConcentration extends MigrationExperimentProperties{

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");

	/**
	 * Propagation d'une propriété d'experiment au niveau content (containers et readSets)
	 * @param experimentTypeCode
	 * @param keyProperty
	 * @return
	 */
	public static Result migration(String experimentTypeCode, String newKeyProperty){

		//backupContainerCollection();
		//backupReadSetCollection();

		//Get list experiment
		List<Experiment> experiments = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("typeCode", experimentTypeCode)).toList();

		//Get list experiment with no experiment properties
		for(Experiment exp : experiments){
			//Logger.debug("Code experiment "+exp.code);
			//Logger.debug("Classe "+OneToOneContainer.class.getName());
			exp.atomicTransfertMethods.stream().filter(atm->!atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				Logger.debug("Experiment "+exp.code+" ATM not one to one ");
			});
			exp.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				atm.outputContainerUseds.stream().filter(output->output.experimentProperties==null).forEach(output->{
					Logger.debug("Experiment "+exp.code+" inputContainer "+output.code+" no experiment property");
				});
			});
			exp.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				atm.outputContainerUseds.stream().filter(output->output.concentration==null).forEach(output->{
					Logger.debug("Experiment "+exp.code+" inputContainer "+output.code+" no concentration attribute");
				});
			});

		}

		//Get all inputQuantity to change to libraryInputQuantity
		for(Experiment exp : experiments){
			Logger.debug("Code experiment "+exp.code);
			Logger.debug("Classe "+OneToOneContainer.class.getName());
			exp.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				Logger.debug("ATM "+atm.getClass());
				atm.outputContainerUseds.stream().filter(output->output.experimentProperties!=null && !output.experimentProperties.containsKey(newKeyProperty)).forEach(output->{
					//Get property
					Logger.debug("Update property for container out "+output.code);
					PropertyValue propValue = output.concentration;
					output.experimentProperties.put(newKeyProperty, propValue);

					if(output.contents!=null){
						if(output.contents.size()>1)
							Logger.error("Multiple contents ");
						if(output.contents.size()>0){
							output.contents.stream().filter(content->!content.properties.containsKey(newKeyProperty)).forEach(content->{
								Logger.debug("add property to content "+content.sampleCode);
								content.properties.put(newKeyProperty, propValue);
							});
						}
					}
					updateContainer(output.code, newKeyProperty, propValue);

					List<String> containerCodes = new ArrayList<String>();
					getListContainerCode(output.locationOnContainerSupport.code, containerCodes);
					for(String codeContainer : containerCodes){
						Logger.debug("Update container code "+codeContainer);
						updateContainer(codeContainer, newKeyProperty, propValue);
					}

				});
			});

			//Update experiment in database
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}

		return ok();
	}


}
