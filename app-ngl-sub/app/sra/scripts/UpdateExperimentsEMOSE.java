package sra.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.lfw.controllers.AbstractScript;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;
import services.XmlServices;


public class UpdateExperimentsEMOSE extends AbstractScript {
	// Pas besoin de definir un constructeur avec des arguments passés par injection car utilisation de Services statiques
	
	
	public List<String> updateInDatabase ( String libraryConstructionProtocol, List<String> experimentAccessions) {
		List<String> experimentCodes = new ArrayList<>();
		for (String experimentAC: experimentAccessions) {
			Experiment experiment = MongoDBDAO
				.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, 
						Experiment.class, DBQuery.is("accession", experimentAC));
			if (experiment == null) {
				throw new RuntimeException("Experiment not found : "+ experimentAC);
			}
			experiment.libraryConstructionProtocol = libraryConstructionProtocol;
			MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
			experimentCodes.add(experiment.code);
		}
		return experimentCodes;
	}	
	
	@Override
	public void execute() {
	List<String> experimentAC_1 = Arrays.asList("ERX2252599", "ERX2252598", "ERX2252597");
		String libraryConstructionProtocol_1 = "Shotgun DNA sequencing";
		List<String> experimentCodes_1 = updateInDatabase(libraryConstructionProtocol_1, experimentAC_1);

		List<String> experimentAC_2 = Arrays.asList("ERX2252602", "ERX2252601", "ERX2252600");
		String libraryConstructionProtocol_2 = 
				"Amplicon sequencing after 18S amplification by PCR using 1391F/EukB primer set. Library were constructed according to Illumina Library protocol without any sizing.";
		List<String> experimentCodes_2 = updateInDatabase(libraryConstructionProtocol_2, experimentAC_2);

		List<String> experimentAC_3 = Arrays.asList("ERX2252605", "ERX2252604", "ERX2252603", "ERX2252609", "ERX2252608", "ERX2252607", "ERX2252606");
		String libraryConstructionProtocol_3 = 
				"Amplicon sequencing after 16S amplification by PCR using 515F/926R primer set. Library were constructed according to Illumina Library protocol without any sizing.";
		List<String> experimentCodes_3 = updateInDatabase(libraryConstructionProtocol_3, experimentAC_3);

		List<String> experimentCodes = new ArrayList<>();
		experimentCodes.addAll(experimentCodes_1);
		experimentCodes.addAll(experimentCodes_2);
		experimentCodes.addAll(experimentCodes_3);
		File outputFile = new File ("/env/cns/home/sgas/update_experiments_EMOSE/experiment.xml");
		try {
			XmlServices.writeSimpleExperimentXml(experimentCodes, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SraException e) {
			e.printStackTrace();
		}
		
	}

}
