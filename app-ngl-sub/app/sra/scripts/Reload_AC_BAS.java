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
		List<String> submissionCodes = new ArrayList<String>();
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

