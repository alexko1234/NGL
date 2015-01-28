package submission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Study;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.junit.Test;

import services.DbUtil;
import services.SubmissionServices;
import utils.AbstractTests;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import play.Logger;

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
		String user = "william";
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();		
		Configuration config = new Configuration();
		config.code = "conf_AWK_5";
		config.projectCode = "AWK";
		config.strategySample = "strategy_sample_taxon";
		config.librarySelection = "random";
		config.librarySource = "genomic";
		config.libraryStrategy = "wgs";
		config.traceInformation = new TraceInformation(); 
		config.traceInformation.setTraceInformation(user);
		config.state = new State("userValidate", user);
		config.validate(contextValidation);
		MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, config);
		contextValidation.displayErrors(Logger.of("SRA"));
		
		// Creer un study validé par utilisateur et le sauver dans mongodb:
		Study study = new Study();
		study.centerName=VariableSRA.centerName;
		study.projectCode = "AWK";
		study.centerProjectName = "AWK";
		study.code = "study_AWK_5";
		study.existingStudyType="Metagenomics";
		study.traceInformation.setTraceInformation(user);
		study.code = "study_" + config.projectCode;
		study.state = new State("userValidate", user);
		study.validate(contextValidation);
		contextValidation.displayErrors(Logger.of("SRA"));
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		////////sraDbServices.save(study);
		
		///////MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
		
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
		

		readSets.add(readSet4);

		System.out.println("Create new submission for readSet " + readSet4.code);
		String submissionCode = submissionServices.createNewSubmission(config.projectCode, readSets, study.code, config.code, "william");
		//XmlServices xmlServices = new XmlServices();
		//String directory = "/env/cns/submit_traces/SRA/SNTS_output_xml/mesTEST/lastTest";
		//xmlServices.writeAllXml(submissionCode, directory);
		
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, study.code);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, models.sra.submit.sra.instance.Configuration.class, config.code);
		
		DbUtil dbUtil = new DbUtil();
		
		//dbUtil.cleanDataBase(submissionCode);
		//////////MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		
	}
}
