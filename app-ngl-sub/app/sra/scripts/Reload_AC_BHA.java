package sra.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import services.FileAcServices;
import services.SubmissionServices;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.lfw.controllers.AbstractScript;
import mail.MailServiceException;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Readset;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;


public class Reload_AC_BHA extends AbstractScript {
	
	private FileAcServices fileAcServices;
//	private SubmissionServices submissionServices;
//	private ContextValidation contextValidation; 
	
	@Inject
	public Reload_AC_BHA(FileAcServices fileAcServices, SubmissionServices submissionServices) {
		super();
		this.fileAcServices = fileAcServices;
//		this.submissionServices = submissionServices;
	}
	
	@Override
	public void execute() throws IOException, SraException, MailServiceException {
		reloadAC_BHA();
	}
	
	public void debug_data_BHA (Submission submission) {
		String stateCode = "IP-SUB";
		
		printfln("***************submission avec code=%s", submission.code);

		submission.state.code = stateCode;
		printfln("***************sauvegarde de la soumission %s", submission.code);
		// sauver la soumission avec le status IP-SUB:
		MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);

		String sampleCodeForExp = null;
		for (String sampleCode :  submission.sampleCodes) {	
//			Sample sample = 
					MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, sampleCode);
			sampleCodeForExp = sampleCode;
		}
		
		for (String experimentCode :  submission.experimentCodes) {	
			Experiment experiment = MongoDBDAO
				.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, 
					models.sra.submit.sra.instance.Experiment.class, experimentCode);
			// correction specifique a la soumission (corriger reference au sample)

			experiment.sampleCode = sampleCodeForExp; 
			printfln("***************sauvegarde de l'experiment %s avec correction pour le sampleCode ", experiment.code);
			MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);			
		}
	}
	

	public void reloadAC_BHA() throws IOException, SraException, MailServiceException {
		List<String> submissionCodes = new ArrayList<String>();
		submissionCodes.add("GSC_BHA_32FE4ABOO");
		
		for (String submissionCode: submissionCodes) {
			Submission submission = MongoDBDAO
					.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, 
							models.sra.submit.common.instance.Submission.class, submissionCode);
			debug_data_BHA(submission);
			
			File fileEbi = new File("/env/cns/home/sgas/debug_BHA",  "listAC_" + submission.code + ".txt");
			String user = "william";
			ContextValidation ctxVal = new ContextValidation(user);
			submission = this.fileAcServices.traitementFileAC(ctxVal, submissionCode, fileEbi); 
		}	
	}
	


	
}

