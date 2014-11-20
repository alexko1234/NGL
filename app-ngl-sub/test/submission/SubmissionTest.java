package submission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.configuration.instance.Configuration;
import models.sra.study.instance.Study;
import models.sra.utils.SraException;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.Test;

import services.DbUtil;
import services.SubmissionServices;
import services.XmlServices;
import utils.AbstractTests;
import fr.cea.ig.MongoDBDAO;

public class SubmissionTest extends AbstractTests {
	
/*	@AfterClass
	public void finalize()
	{
	
	}
	*/
	@Test
	public void testSubmission() throws IOException, SraException
	{
		SubmissionServices submissionServices = new SubmissionServices();
		DbUtil sraDbServices = new DbUtil();
		Configuration configuration = new Configuration();
		
		configuration.projectCode = "AKL";
		configuration.strategySample = "STRATEGY_SAMPLE_TAXON";
		// Creer un study validé par utilisateur et le sauver dans mongodb:
		Study study = new Study();
		study.code = "study_" + configuration.projectCode;
		study.state = new State("userValidate", null);
		
		////////sraDbServices.save(study);
		
		///////MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
		
		//Valeurs possibles pour strategySample :
		//"STRATEGY_SAMPLE_TAXON", "STRATEGY_SAMPLE_CLONE", "STRATEGY_NO_SAMPLE";
		String codeReadSet1 = "AUP_COSW_4_D09BTACXX.IND7";   // equivalent lotSeqName 
		String codeReadSet2 = "AUP_NAOSW_5_C0UW4ACXX.IND10"; // equivalent lotSeqName 
		String codeReadSet3 = "AKL_ABOSA_1_80MJ3ABXX"; // equivalent lotSeqName 
		String codeReadSet4 = "AWK_EMOSW_1_H9YKWADXX.IND1"; // lotSeqName pairé et avec mapping
		// ex de donnée illumina single : AUP_COSW_4_D09BTACXX.IND7
		// ex de donnée illumina paired : AUP_NAOSW_5_C0UW4ACXX.IND10, AKL_ABOSA_1_80MJ3ABXX
		ReadSet readSet1 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet1);
		ReadSet readSet2 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet2);
		ReadSet readSet3 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet3);
		//Ex de donnée pairee avec mapping :
		ReadSet readSet4 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet4);
		
	
		System.out.println("READSET4="+readSet4.code);
		List<ReadSet> readSets = new ArrayList<ReadSet>();
		

		readSets.add(readSet3);

		System.out.println("Create new submission for readSet " + readSet3.code);
		
		String submissionCode = submissionServices.createNewSubmission(configuration.projectCode, readSets, study, configuration.strategySample);
		XmlServices xmlServices = new XmlServices();
		String directory = "/env/cns/submit_traces/SRA/SNTS_output_xml/mesTEST/lastTest";
		xmlServices.writeAllXml(submissionCode, directory);
		DbUtil dbUtil = new DbUtil();
		dbUtil.cleanDataBase(submissionCode);
		//////////MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
	}
}
