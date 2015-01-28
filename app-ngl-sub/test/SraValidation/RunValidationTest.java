package SraValidation;

import java.io.IOException;

import models.laboratory.run.instance.ReadSet;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;

import org.junit.Assert;
import org.junit.Test;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import services.SubmissionServices;
import utils.AbstractTestsSRA;
import validation.ContextValidation;

public class RunValidationTest extends AbstractTestsSRA {
	
	@Test
	public void validationRunSuccess() throws IOException, SraException {
		ContextValidation contextValidation = new ContextValidation(userContext);
		String projectCode = "AWK";
		String codeReadSet = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
		SubmissionServices submissionServices = new SubmissionServices();
		Run run = submissionServices.createRunEntity(readSet, projectCode);
		run.validate(contextValidation);
		System.out.println("\ndisplayErrors pour validationRunSuccess :");
		contextValidation.displayErrors(Logger.of("SRA"));
		Assert.assertTrue(contextValidation.errors.size()==0); // si aucune erreur
	}
}
