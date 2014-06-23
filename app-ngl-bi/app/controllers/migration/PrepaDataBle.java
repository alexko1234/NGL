package controllers.migration;

import static play.data.Form.form;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import validation.ContextValidation;
import workflows.Workflows;
import controllers.CommonController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;

public class PrepaDataBle extends CommonController{
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	
	
	public static Result createTest() {
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		ContextValidation ctxVal = new ContextValidation(filledQueryFieldsForm.errors());
		ctxVal.setUpdateMode();
		Project p = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, "BCI");
		if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, "TEST")){
			Logger.info("Create Project TEST");
			p._id = null;
			p.code = "TEST";
			p.name = "TEST";
			p.bioinformaticAnalysis = true;
			MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, p);
		}
		/*
		 * 
"BCI_AOSN_1_1_A7PDE.IND19","BCI_ADNOSF_1_1_A7B5R.IND32","BCI_AHEOSF_1_1_A7B5R.IND25","BCI_AHNOSF_1_1_A7B5R.IND26","BCI_AIBOSF_1_1_A7B5R.IND27","BCI_AKHOSF_1_1_A7B5R.IND30","BCI_AQQOSF_1_1_A7B5R.IND33","BCI_BAMOSF_1_1_A7B5R.IND28","BCI_BIDOSF_1_1_A7B5R.IND34","BCI_GVOSF_1_1_A7B5R.IND31","BCI_INOSF_1_1_A7B5R.IND29
		 */
		List<ReadSet> rsl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.in("code", 
				Arrays.asList(
						"BCI_AOSN_1_A7PDE.IND19","BCI_ADNOSF_1_A7B5R.IND32","BCI_AHEOSF_1_A7B5R.IND25",
						"BCI_AHNOSF_1_A7B5R.IND26","BCI_AIBOSF_1_A7B5R.IND27","BCI_AKHOSF_1_A7B5R.IND30",
						"BCI_AQQOSF_1_A7B5R.IND33","BCI_BAMOSF_1_A7B5R.IND28","BCI_BIDOSF_1_A7B5R.IND34",
						"BCI_GVOSF_1_A7B5R.IND31","BCI_INOSF_1_A7B5R.IND29"))).toList();
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("projectCode", "TEST"));
		if(rsl.size() == 11){
			for(ReadSet rs: rsl){
				String oldSampleCode = rs.sampleCode;
				String newSampleCode = getMapSample().get(oldSampleCode);
				rs._id = null;
				rs.code = rs.code.replace(oldSampleCode, newSampleCode);
				rs.projectCode = "TEST";
				rs.sampleCode = newSampleCode;
				rs.sampleOnContainer.sampleCode = newSampleCode;
				rs.productionValuation.valid = TBoolean.TRUE;
				rs.bioinformaticValuation.valid = TBoolean.UNSET;
				rs.path = rs.path.replace(oldSampleCode.replace("_","/"), newSampleCode.replace("_","/"));
				for(File file: rs.files){
					file.fullname = file.fullname.replace(oldSampleCode, newSampleCode);
				}
				rs.state.code = "IW-VQC";
				rs.state.historical = null;
				MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, rs);
				Workflows.nextReadSetState(ctxVal, rs);
			}
		}else{
			Logger.error("MISSING RS : "+rsl.size());
		}
		
		
		return ok("Données du projet TEST misent à jour ;-)");
	}
	
	private static Map<String, String> mapSample;
	private static Map<String, String> getMapSample(){
		if(null == mapSample){
			mapSample = new HashMap<String, String>();
			mapSample.put("BCI_A", "TEST_AAA");
			mapSample.put("BCI_ADN", "TEST_AAAA");
			mapSample.put("BCI_AHE", "TEST_AAAB");
			mapSample.put("BCI_AHN", "TEST_AAAC");
			mapSample.put("BCI_AIB", "TEST_AAAD");
			mapSample.put("BCI_AKH", "TEST_AAAE");
			mapSample.put("BCI_AQQ", "TEST_AAAF");
			mapSample.put("BCI_BAM", "TEST_AAAG");
			mapSample.put("BCI_BID", "TEST_AAAH");
			mapSample.put("BCI_GV", "TEST_AAAI");
			mapSample.put("BCI_IN", "TEST_AAAJ");
			
			for(Entry<String, String> sampleCode: mapSample.entrySet()){
				if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleCode.getValue())){
					Sample oldSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleCode.getKey());
					oldSample._id = null;
					oldSample.code = sampleCode.getValue();
					oldSample.projectCodes.clear();
					oldSample.projectCodes.add("TEST");
					MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME, oldSample);
				}
			}
		}
		return mapSample;
	}
}
