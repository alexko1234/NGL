package SraValidation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mail.MailServiceException;
import models.sra.configuration.instance.Configuration;
import models.sra.experiment.instance.Experiment;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;

import org.junit.Test;

import play.Logger;
import play.modules.mongojack.MongoDB;
import services.DataSetServices;
import services.FileAcServices;
import services.SubmissionServices;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

import validation.ContextValidation;
import utils.AbstractTestsSRA;

import org.junit.Assert;
import org.junit.Test;

public class FileAcServicesTest  extends AbstractTestsSRA {
	
	@Test
	public void FileAcServicesSuccess() throws IOException, SraException, MailServiceException {
		
/*		String studyCode = "test_study_AWK";
		String configCode = "test_conf_AWK";
		String submissionCode="test_cns_AWK";
		String readSetCode = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		String experimentCode = "test_exp_" + readSetCode;
		String sampleCode = "test_sample_AWK_472";
*/

		// Creation d'une soumission "test_cns_AWK" avec status en attente des numeros d'accession : 
		String submissionCode="test_cns_AWK";
		DataSetServices.deleteDataSetForFileAcServices();
		DataSetServices.createDataSetForFileAcServices();
		
		File fileEbi = new File("/env/cns/submit_traces/SRA/ngl-sub/mesTests/RESULT_AC");
		//Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		
		Submission submission = FileAcServices.traitementFileAC(submissionCode, fileEbi); 
		Assert.assertTrue(MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "accession", "ERA000000"));
		
	}
	@Test
	public void FileAcServicesEchec() throws IOException, SraException, MailServiceException {

		// Creation d'une soumission "test_cns_AWK" avec status en attente des numeros d'accession : 
		String submissionCode="test_cns_AWK";
		DataSetServices.deleteDataSetForFileAcServices();
		DataSetServices.createDataSetForFileAcServices();
		// Fichier des AC incomplet sans numeros pour le sample et le run :
		File fileEbi = new File("/env/cns/submit_traces/SRA/ngl-sub/mesTests/RESULT_AC_ERROR");
		//Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		
		FileAcServices.traitementFileAC(submissionCode, fileEbi);
		Assert.assertTrue(!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "accession", "ERA000000"));

	}
}
