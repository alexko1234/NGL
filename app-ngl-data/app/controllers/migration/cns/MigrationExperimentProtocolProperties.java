package controllers.migration.cns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;


import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationExperimentProtocolProperties extends MigrationExperimentProperties{

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");

	private static Map<String, String> protocolsMap = new HashMap<String, String>();
	/**
	 * Propagation d'une propriété d'experiment au niveau content (containers et readSets)
	 * @param experimentTypeCode
	 * @param keyProperty
	 * @return
	 */
	public static Result migration(String experimentTypeCode, String keyProperty){

		//backupContainerCollection();
		//backupReadSetCollection();

		//Get list experiment
		List<Experiment> experiments = getListExperiments(DBQuery.is("typeCode", experimentTypeCode).exists("protocolCode"));

		//Get list experiment with no experiment properties
		for(Experiment exp : experiments){
			checkATMExperiment(exp);
		}

		//Get all inputQuantity to change to libraryInputQuantity
		for(Experiment exp : experiments){
			Logger.debug("Code experiment "+exp.code);
			Logger.debug("Classe "+OneToOneContainer.class.getName());
			//Get protocol Name
			String protocolName = getProtocolName(exp.protocolCode);
			PropertyValue propValue = new PropertySingleValue(protocolName);
			exp.atomicTransfertMethods.stream().filter(atm->atm.getClass().getName().equals(OneToOneContainer.class.getName())).forEach(atm->{
				Logger.debug("ATM "+atm.getClass());
				atm.inputContainerUseds.stream().forEach(input->{
					//Get property
					Logger.debug("Update property for container out "+input.code);

					updateContainerContents(input, keyProperty, propValue);
					
					//Get container from input
					updateContainer(input.code, keyProperty, propValue, false);

					updateOutputContainer(atm, keyProperty, propValue, false);

				});
			});

			//Update experiment in database
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}

		return ok();
	}


	private static String getProtocolName(String protocolCode)
	{
		if(protocolsMap.containsKey(protocolCode))
			return protocolsMap.get(protocolCode);
		else{
			Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, protocolCode);
			protocolsMap.put(protocolCode, protocol.name);
			return protocol.name;
		}
					
	}
}
