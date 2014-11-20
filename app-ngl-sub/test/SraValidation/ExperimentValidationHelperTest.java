package SraValidation;

import java.io.IOException;

import models.laboratory.run.instance.ReadSet;
import models.sra.experiment.instance.Experiment;
import models.sra.utils.SraException;
import models.utils.InstanceConstants;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import services.SubmissionServices;
import utils.AbstractTestsSRA;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.sra.experiment.instance.RawData;
import models.sra.experiment.instance.ReadSpec;
import models.sra.experiment.instance.Run;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.VariableSRA;



//Pour les tests utilisant MongoDb mettre extents AbstractTests
public class ExperimentValidationHelperTest extends AbstractTestsSRA {
	//@BeforeClass appelé 1! fois avant tests
	//@Before appelee avant chaque test
	
	@Test
	public void validationSraValidationHelperRequiredAndConstraintSuccess() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = "random";
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
	}
	
	@Test
	public void validationSraValidationHelperRequiredAndConstraintEchec() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = "farfelue";
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==1); // si une erreur
	}
	
	@Test
	public void validationSraValidationHelperRequiredAndConstraintNull() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.addKeyToRootKeyName("experiment::");
		String librarySelection = null;
		SraValidationHelper.requiredAndConstraint(contextValidation, librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		contextValidation.removeKeyFromRootKeyName("experiment::");
		Assert.assertTrue(contextValidation.errors.size()==1); // si une erreur
	}
		
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
		String librarySelection = "";
		String libraryStrategy = "";
		String librarySource = "";
		SubmissionServices submissionServices = new SubmissionServices();
		Experiment experiment = submissionServices.createExperimentEntity(readSet, projectCode);
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.setCreationMode();
		contextValidation.addKeyToRootKeyName("experiment::");
		experiment.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
		contextValidation.setUpdateMode();
		experiment.validate(contextValidation);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.experiment.instance.Experiment.class, experiment.code);

		//contextValidation.getContextObjects().put("stateCode", "F");

		System.out.println(contextValidation.errors.toString());
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
		//Assert.assertTrue(contextValidation.errors.size()==1ou > ); // si aucune erreur
	}

}

	