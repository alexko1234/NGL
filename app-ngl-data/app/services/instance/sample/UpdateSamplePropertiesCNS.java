package services.instance.sample;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.springframework.beans.factory.annotation.Autowired;

import play.Logger;
import play.api.modules.spring.Spring;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;
import workflows.container.ContentHelper;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateSamplePropertiesCNS extends AbstractImportDataCNS {

	ContentHelper contentHelper;
	
	public UpdateSamplePropertiesCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdatePropertiesSampleCNS", durationFromStart, durationFromNextIteration);
		contentHelper = Spring.getBeanOfType(ContentHelper.class);
		
	}

	public UpdateSamplePropertiesCNS(String string, FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(string, durationFromStart, durationFromNextIteration);
		contentHelper = Spring.getBeanOfType(ContentHelper.class);
		
	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		//Récupère tous les samples modifiés les derniers 48h
		updateSampleModifySince(-1,contextError);
	}

	private void updateSampleModifySince(int nbDays,ContextValidation contextError){

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, nbDays);
				Date date =  calendar.getTime();

				List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.greaterThanEquals("traceInformation.modifyDate", date).notExists("life"))
						.sort("code").toList();
				Logger.info("Nb samples to update :"+samples.size());
				samples.stream().forEach(sample -> {
					//Logger.debug("Sample "+sample.code);
					updateOneSample(sample,contextError);
				});
	}

	public void updateOneSample(Sample sample,ContextValidation contextError) {

		logger.debug("Update sample "+sample.code);

		Map<String, PropertyValue> updatedProperties=new HashMap<String, PropertyValue>();
		Set<String> deletedPropertyCodes = new TreeSet<String>();
		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,updatedProperties);
			deletedPropertyCodes.addAll(InstanceHelpers.getDeletedPropertyDefinitionCode(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties));
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,updatedProperties);
			deletedPropertyCodes.addAll(InstanceHelpers.getDeletedPropertyDefinitionCode(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties));
		}
		
		logger.warn("property will be deleted "+deletedPropertyCodes);
		
		
		updateCollectionsFromSample(sample, updatedProperties, deletedPropertyCodes,contextError);

	} 

	private void updateCollectionsFromSample(Sample sample, Map<String, PropertyValue> updatedProperties, Set<String> deletedPropertyCodes, ContextValidation contextError){

		logger.info("Update son samples, containers, readSets, process from sample :"+sample.code);
		
		MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
				DBQuery.is("life.from.sampleCode",sample.code).in("life.from.projectCode", sample.projectCodes))
			.cursor.forEach(updatedSample -> {
				updatedSample.traceInformation.setTraceInformation(contextError.getUser());
				updatedSample.referenceCollab = sample.referenceCollab;
				updatedSample.taxonCode = sample.taxonCode;
				updatedSample.ncbiScientificName = sample.ncbiScientificName;
				updatedSample.ncbiLineage = sample.ncbiLineage;
				updatedSample.properties = InstanceHelpers.updateProperties(updatedSample.properties, updatedProperties, deletedPropertyCodes);
				MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,updatedSample);
		});
		
		//Containers content update contents.$.properties
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.is("contents.sampleCode", sample.code).in("contents.projectCode", sample.projectCodes))
			.cursor.forEach(container -> {
				container.traceInformation.setTraceInformation(contextError.getUser());
				container.contents.stream()
					.filter(content -> sample.code.equals(content.sampleCode) && sample.projectCodes.contains(content.projectCode) )
					.forEach(content -> {
						content.ncbiScientificName = sample.ncbiScientificName;
						content.taxonCode = sample.taxonCode;
						content.referenceCollab = sample.referenceCollab;
						
						content.properties = InstanceHelpers.updateProperties(content.properties, updatedProperties, deletedPropertyCodes);
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, contentHelper.getContentQuery(container, content), DBUpdate.set("contents.$", content));
					});;
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);	
			});
		
		// ReadSet update sampleOnContainer.properties		
		MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,	
				DBQuery.is("sampleOnContainer.sampleCode", sample.code).in("sampleOnContainer.projectCode", sample.projectCodes))
		.cursor
		.forEach(readset -> {
			readset.traceInformation.setTraceInformation(contextError.getUser());
			readset.sampleOnContainer.lastUpdateDate = new Date();
			
			readset.sampleOnContainer.referenceCollab = sample.referenceCollab;
			readset.sampleOnContainer.taxonCode = sample.taxonCode;
			readset.sampleOnContainer.ncbiScientificName = sample.ncbiScientificName;
			
			readset.sampleOnContainer.properties = InstanceHelpers.updateProperties(readset.sampleOnContainer.properties, updatedProperties, deletedPropertyCodes);
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readset);			
		});	
		
		// Processes update sampleOnInputContainer.properties		
		MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class, 
				DBQuery.is("sampleOnInputContainer.sampleCode", sample.code).in("sampleOnInputContainer.projectCode", sample.projectCodes))
		.cursor
		.forEach(process -> {
			process.traceInformation.setTraceInformation(contextError.getUser());
			process.sampleOnInputContainer.lastUpdateDate = new Date();
			
			process.sampleOnInputContainer.referenceCollab = sample.referenceCollab;
			process.sampleOnInputContainer.taxonCode = sample.taxonCode;
			process.sampleOnInputContainer.ncbiScientificName = sample.ncbiScientificName;
			
			process.sampleOnInputContainer.properties = InstanceHelpers.updateProperties(process.sampleOnInputContainer.properties, updatedProperties, deletedPropertyCodes);
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, process);		
		});
		
		
		/*
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
				DBQuery.is("sampleOnInputContainer.sampleCode", sample.code),
				updates.get("process")
					.set("sampleOnInputContainer.referenceCollab",sample.referenceCollab)
					.set("sampleOnInputContainer.taxonCode",sample.taxonCode)
					.set("sampleOnInputContainer.ncbiScientificName", sample.ncbiScientificName)
					,true);
		*/

		//Update son samples
		MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,
				DBQuery.is("life.from.sampleCode",sample.code).in("life.from.projectCode", sample.projectCodes))
				.cursor
				.forEach(sonSample ->{
					updateCollectionsFromSample(sonSample, updatedProperties, deletedPropertyCodes, contextError);
				});
	}

}
