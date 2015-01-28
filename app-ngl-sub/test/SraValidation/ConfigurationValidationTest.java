package SraValidation;


import java.io.IOException;
import java.util.Date;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTestsSRA;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;
import fr.cea.ig.MongoDBDAO;
import play.Logger;

// Pour les tests utilisant MongoDb mettre extents AbstractTests
public class ConfigurationValidationTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test
		
	@Test
	public void validationConfigurationSuccess() throws IOException, SraException {
		//this.initConfig();
		// Tests config avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.
		Configuration config = new Configuration();
		config.code = "conf_AWK_1";
		config.projectCode = "AWK";
		config.strategySample = "strategy_sample_taxon";
		config.librarySelection = "random";
		config.librarySource = "genomic";
		config.libraryStrategy = "wgs";
		String user = "william";
		config.traceInformation = new TraceInformation(); 
		config.traceInformation.setTraceInformation(user);
		//config.state = new State("new");
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		config.validate(contextValidation);
		
		MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, config);
		// MongoDBDAO.save ajoute un _id dans mongodb. Pour l'avoir dans l'objet il faut le recharger
		// depuis mongodb. En mode update, le validate verifie que id existe bien dans l'objet.

		config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, config.code);
		contextValidation.setUpdateMode();
		config.traceInformation.setTraceInformation(user);
		config.validate(contextValidation);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.submit.sra.instance.Configuration.class, config.code);

		//System.out.println(contextValidation.errors.toString());
		System.out.println("\ndisplayErrors pour validationConfigurationSuccess :");
		contextValidation.displayErrors(Logger.of("SRA"));
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		//Assert.assertTrue(contextValidation.errors.size()==1ou > ); // si aucune erreur
	}
}
