package sra.scripts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.lfw.controllers.AbstractScript;
import mail.MailServiceException;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;
import services.FileAcServices;
import services.SubmissionServices;
import validation.ContextValidation;

public class Reload_AC_BAS extends AbstractScript {
	
	private FileAcServices fileAcServices;
//	private SubmissionServices submissionServices;
//	private ContextValidation contextValidation; 
	
	@Inject
	public Reload_AC_BAS(FileAcServices fileAcServices, SubmissionServices submissionServices) {
		super();
		this.fileAcServices = fileAcServices;
//		this.submissionServices = submissionServices;
	}
	
	@Override
	public void execute() throws IOException, SraException, MailServiceException {
		reloadAC_BAS();
	}
	
	public void reloadAC_BAS() throws IOException, SraException, MailServiceException {
		List<String> submissionCodes = new ArrayList<>();
		submissionCodes.add("GSC_BAS_339A4VWB4");
		
		for (String submissionCode: submissionCodes) {
			Submission submission = MongoDBDAO
					.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, 
							models.sra.submit.common.instance.Submission.class, submissionCode);
			
			File fileEbi = new File("/env/cns/home/sgas/debug_BAS",  "listAC_" + submission.code + ".txt");
			String user = "william";
			ContextValidation ctxVal = new ContextValidation(user);
			submission = this.fileAcServices.traitementFileAC(ctxVal, submissionCode, fileEbi); 
		}	
	}
		
}

