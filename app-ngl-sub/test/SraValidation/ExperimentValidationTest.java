package SraValidation;

import java.io.IOException;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.sra.experiment.instance.Experiment;
import models.sra.utils.SraException;
import models.utils.InstanceConstants;
import org.junit.Assert;
import org.junit.Test;
import services.SubmissionServices;
import utils.AbstractTestsSRA;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;
import fr.cea.ig.MongoDBDAO;
import models.sra.utils.VariableSRA;

import play.Logger;


//Pour les tests utilisant MongoDb mettre extents AbstractTests
public class ExperimentValidationTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test
	

	@Test
	public void validationExperimentSuccess() throws IOException, SraException {
		//this.initConfig();
		// Tests experiment avec infos obligatoires presentes et bonnes valeurs et mode
		// creation ou update.
		//Experiment experiment = new Experiment();
		//Ex de donnée pairee avec mapping :
		String projectCode = "AWK";
		String codeReadSet = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);

		SubmissionServices submissionServices = new SubmissionServices();
	 
		Experiment experiment = submissionServices.createExperimentEntity(readSet, projectCode, userContext);
		experiment.librarySelection = "random";
		experiment.librarySource = "genomic";
		experiment.libraryStrategy = "wgs";
		String user = "william";
		experiment.traceInformation = new TraceInformation(); 
		experiment.traceInformation.setTraceInformation(user);
		experiment.sampleCode = "sample_code_test";
		experiment.studyCode = "study_code_test";
		ContextValidation contextValidation = new ContextValidation(userContext);
		contextValidation.setCreationMode();
		experiment.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.experiment.instance.Experiment.class, experiment.code);
		System.out.println("contextValidation.errors pour validationExperimentSuccess :");
		contextValidation.displayErrors(Logger.of("SRA"));
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		//Assert.assertTrue(contextValidation.errors.size()==1ou > ); // si aucune erreur
	}

}

	