package submission;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.junit.Test;

import fr.cea.ig.MongoDBDAO;

import services.SubmissionServices;
import utils.AbstractTests;

public class SubmissionTest extends AbstractTests{

	@Test
	public void testSubmission()
	{
		SubmissionServices submissionServices = new SubmissionServices();
		
		String codeReadSet = "AKL_ABOSA_1_80MJ3ABXX"; // equivalent lotSeqName 
		// ex de donnée illumina single : AUP_COSW_4_D09BTACXX.IND7
		// ex de donnée illumina paired : AUP_NAOSW_5_C0UW4ACXX.IND10, AKL_ABOSA_1_80MJ3ABXX
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet);
		List<ReadSet> readSets = new ArrayList<ReadSet>();
		readSets.add(readSet);
		
		//String strategySample = "STRATEGY_SAMPLE_CLONE";
		String strategySample = "STRATEGY_SAMPLE_TAXON";
		//String strategySample = "STRATEGY_NO_SAMPLE";
		System.out.println("Create new submission for readSet " + readSet.code);

		submissionServices.createNewSubmission("AKL", readSets, null, "", strategySample);
	}
}
