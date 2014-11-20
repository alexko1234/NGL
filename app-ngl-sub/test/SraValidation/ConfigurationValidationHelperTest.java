package SraValidation;


import java.io.IOException;

import models.sra.configuration.instance.Configuration;
import models.sra.utils.SraException;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import utils.AbstractTestsSRA;



// Pour les tests utilisant MongoDb mettre extents AbstractTests
public class ConfigurationValidationHelperTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test
		
	@Test
	public void validationConfigurationSuccess() throws IOException, SraException {
		//this.initConfig();
		// Tests config avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.
		Configuration config = new Configuration();
		config.code = "conf_test_1";
		config.projectCode = "test";
		config.strategySample = "strategy_sample_taxon";

		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		config.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, config);
		contextValidation.setUpdateMode();
		config.validate(contextValidation);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.configuration.instance.Configuration.class, config.code);

		//contextValidation.getContextObjects().put("stateCode", "F");

		System.out.println(contextValidation.errors.toString());
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		//Assert.assertTrue(contextValidation.errors.size()==1ou > ); // si aucune erreur
	}
	
	@Test
	public void validationConfigurationEchec() throws IOException, SraException {
		//this.initConfig();
		// Tests config avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.
		Configuration config = new Configuration();
		config.code = "conf_test_2";
		config.projectCode = "test";
		config.strategySample = "strategy_sample_taxon_farfelue";
		config.librarySelection = "";
		config.librarySource = "   ";
		config.libraryStrategy = "libStrategy_vague";
		config.userFileExperiments = "toto";
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		config.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, config);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.configuration.instance.Configuration.class, config.code);

		//contextValidation.getContextObjects().put("stateCode", "F");

		System.out.println(contextValidation.errors.toString());
		System.out.println("nbre d'erreurs : "+contextValidation.errors.size());

		Assert.assertTrue(contextValidation.errors.size()==5); // On attend 5 erreurs
	}


}
