package services.instance.sample;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateSamplePropertiesCNS extends AbstractImportDataCNS {

	public UpdateSamplePropertiesCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdatePropertiesSampleCNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		//Récupère tous les samples modifiés les derniers 48h
		updateSampleModifySince(-4,contextError);
	}

	static public void updateSampleModifySince(int nbDays,ContextValidation contextError){

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, nbDays);
				Date date =  calendar.getTime();

				List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.greaterThanEquals("traceInformation.modifyDate", date).notExists("life")).toList();
				Logger.info("Nb samples to update :"+samples.size());
				/*samples.stream().forEach(sample -> {
					//Logger.debug("Sample "+sample.code);
					updateOneSample(sample,contextError);
				});*/
	}

	static public void updateOneSample(Sample sample,ContextValidation contextError) {

		Logger.debug("Update sample "+sample.code);

		Map<String, PropertyValue> properties=new HashMap<String, PropertyValue>();

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}

		Map<String,DBUpdate.Builder> updates=new HashMap<String, DBUpdate.Builder>();
		DBUpdate.Builder updateSample = new DBUpdate.Builder();
		DBUpdate.Builder updateContainer = new DBUpdate.Builder();
		DBUpdate.Builder updateReadSet = new DBUpdate.Builder();
		DBUpdate.Builder updateProcess = new DBUpdate.Builder();
		
		properties.forEach((k,v) -> {
			Logger.debug("properties "+k+" value "+v);
			updateSample.set("properties."+k,v);
			updateContainer.set("contents.$.properties."+k,v);
			updateReadSet.set("sampleOnContainer.properties."+k,v);
			updateProcess.set("sampleOnInputContainer.properties."+k,v);
		});		
		
		updates.put("sample",updateSample);
		updates.put("container",updateContainer);
		updates.put("readset",updateReadSet);
		updates.put("process",updateProcess);
		
		updateCollectionsFromSample(sample, updates,contextError);

	} 

	static private void updateCollectionsFromSample(Sample sample, Map<String,DBUpdate.Builder> updates, ContextValidation contextError){

		Logger.info("Update son samples, containers, readSets, process from sample :"+sample.code);
		
		//Son Samples update properties
		MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
				DBQuery.is("life.from.sampleCode",sample.code),
				updates.get("sample")
				.set("referenceCollab", sample.referenceCollab)
				.set("taxonCode",sample.taxonCode)
				.set("ncbiScientificName",sample.ncbiScientificName)
				.set("ncbiLineage",sample.ncbiLineage)
				);
		
		//Containers content update contents.$.properties
		//TODO Verifier si fonctionne pool meme sample et tag diff
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				 DBQuery.is("contents.sampleCode", sample.code),
				updates.get("container")
						.set("traceInformation.modifyUser",contextError.getUser())
						.set("traceInformation.modifyDate",new Date() ),true);
		
		// ReadSet update sampleOnContainer.properties
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,
				DBQuery.is("sampleOnContainer.sampleCode", sample.code),
				updates.get("readset")
						.set("sampleOnContainer.lastUpdateDate", new Date()),true);
		
		// Processes update sampleOnInputContainer.properties
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
				DBQuery.is("sampleOnInputContainer.sampleCode", sample.code),
				updates.get("process"),true);
		

		//Update son samples
		List<Sample> sonSamples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("life.from.sampleCode",sample.code)).toList();
		sonSamples.stream().forEach(sonSample ->{
			updateCollectionsFromSample(sonSample, updates, contextError);
		});
	}

}
