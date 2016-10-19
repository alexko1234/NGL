package models.utils.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import rules.services.RulesServices6;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import fr.cea.ig.MongoDBDAO;

public class SampleHelper {
	
	
	public static void updateSampleProperties(String sampleCode, Map<String,PropertyValue>  properties,ContextValidation contextValidation){
		
		if(properties !=null){
			for(Entry<String,PropertyValue> entry :properties.entrySet()){
			
			MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
					DBQuery.is("code",sampleCode),
					DBUpdate.set("properties."+entry.getKey(),entry.getValue())
							.set("traceInformation.modifyUser",contextValidation.getUser())
							.set("traceInformation.modifyDate",new Date() ));
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Run.class, 
					 DBQuery.is("contents.sampleCode", sampleCode),
					DBUpdate.set("contents.$.properties."+entry.getKey(),entry.getValue())
							.set("traceInformation.modifyUser",contextValidation.getUser())
							.set("traceInformation.modifyDate",new Date() ),true);					
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
					DBQuery.is("sampleOnContainer.sampleCode", sampleCode),
					DBUpdate.set("sampleOnContainer.properties."+entry.getKey(),entry.getValue())
							.set("sampleOnContainer.lastUpdateDate", new Date()),true);

			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
					DBQuery.is("sampleOnInputContainer.sampleCode", sampleCode),
					DBUpdate.set("sampleOnInputContainer.properties."+ entry.getKey(),entry.getValue()),true);
			}
		} else {
			contextValidation.addErrors("properties", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, sampleCode);
		}
		
	}

	//Return true if sample deleted 
	//Return false if error sample => Sample must be update
	public static boolean deleteSample(String sampleCode,ContextValidation contextValidation) {
		
		ContextValidation ctx=new ContextValidation(contextValidation.getUser());
		List<String> sampleCodes=new ArrayList<String>();
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("sampleOnContainer.sampleCode",sampleCode))){
			Logger.debug("Sample "+sampleCode+" dans ReadSet");
			ctx.addErrors("readSet.sampleOnContainer.sampleCode","Code {0} existe dans ReadSet" , sampleCode);
		}
		
		if(MongoDBDAO.checkObjectExist(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.notEquals("categoryCode","tube").in("sampleCodes", sampleCode))){
			Logger.debug("Sample "+sampleCode+" dans Container");
			ctx.addErrors("container.sampleOnContainer.sampleCode","Code {0} existe dans ReadSet" , sampleCode);
		}
		
		if(!ctx.hasErrors()){
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("sampleCodes", sampleCode).notExists("fromTransformationTypeCodes"));
			Logger.info("Delete container for sampleCode "+sampleCode);
			MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.is("code", sampleCode));
			Logger.info("Delete sample for sampleCode "+sampleCode);
		}
		else {
			return false;
		}

		contextValidation.errors.putAll(ctx.errors);
		return true;

	}

	public static void updateSampleReferenceCollab(Sample sample, ContextValidation contextError) {
			
			MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
					DBQuery.is("code",sample.code),
					DBUpdate.set("referenceCollab",sample.referenceCollab));
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
					 DBQuery.is("contents.sampleCode", sample.code),
					DBUpdate.set("contents.$.referenceCollab",sample.referenceCollab),true);					
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
					DBQuery.is("sampleOnContainer.sampleCode", sample.code),
					DBUpdate.set("sampleOnContainer.referenceCollab",sample.referenceCollab),true);

			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
					DBQuery.is("sampleOnInputContainer.sampleCode", sample.code),
					DBUpdate.set("sampleOnInputContainer.referenceCollab",sample.referenceCollab),true);
		
	}
	
	
	public static void updateSampleTaxon(Sample sample, ContextValidation contextError) {
				
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				 DBQuery.is("contents.sampleCode", sample.code),
				DBUpdate.set("contents.$.taxonCode",sample.taxonCode)
				.set("contents.$.ncbiScientificName", sample.ncbiScientificName),true);					
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
				DBQuery.is("sampleOnContainer.sampleCode", sample.code),
				DBUpdate.set("sampleOnContainer.taxonCode",sample.taxonCode)
				.set("sampleOnContainer.ncbiScientificName", sample.ncbiScientificName),true);
	}	
	
	public static void executeRules(Sample sample,String rulesName){
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(sample);
		
		List<Object> factsAfterRules = RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rulesName, facts);				
	}

	public static Set<String> getSampleParent(String sampleCode) {
		Sample sample=MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.is("code", sampleCode));
		Set<String> sampleCodes=new HashSet<String>();
		sampleCodes.add(sampleCode);

		if(sample.life!=null && sample.life.path!=null){
			sampleCodes.addAll(Arrays.asList(sample.life.path.split(",")));
		} 

		return sampleCodes;
	}

	public static Set<String> getProjectParent(Set<String> sampleCodes) {
		Set<String> projectCodes=new HashSet<String>();
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class,DBQuery.in("code", sampleCodes)).toList();
		
		for(Sample s:samples){
			projectCodes.addAll(s.projectCodes);
		}
		
		return projectCodes;
	}

}
