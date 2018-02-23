package models.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.index.Index;
import models.laboratory.processes.instance.Process;
import models.laboratory.processes.instance.SampleOnInputContainer;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.laboratory.sample.instance.Sample;
import models.sra.submit.common.instance.Readset;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import play.Logger;
import play.api.modules.spring.Spring;

import play.mvc.Http;
import validation.ContextValidation;
import validation.IValidation;
import workflows.container.ContentHelper;

import com.mongodb.BasicDBObject;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class InstanceHelpers {

	// @SuppressWarnings("unchecked")
	public static Map<String, PropertyValue<?>> getLazyMapPropertyValue() {
//		return MapUtils.lazyMap(new HashMap<String, PropertyValue<?>>(), new Transformer() {
		return MapUtils.lazyMap(new HashMap<>(), new Transformer() {
			public PropertyValue<?> transform(Object mapKey) {
				// todo comment je sais quel est le type on doit mettre
				return new PropertySingleValue();
			}
		});
	}

	@Deprecated
	public static String getUser() {
		String user;
		try {
			user = Http.Context.current().session().get("CAS_FILTER_USER");
			if (user == null) {
				user = "ngl";
			}
		} catch (RuntimeException e) {
			user = "ngl";
		}
		return user;

	}

	public static List<Comment> addComment(String comment, List<Comment> comments, String user) {
		if (comments == null) {
			comments = new ArrayList<Comment>();
		}

		Comment newComment = new Comment(comment, user);
		
		comments.add(newComment);
		return comments;
	}

	
	public static List<Comment> updateComments(List<Comment> comments, ContextValidation contextValidation){
		if(comments != null && comments.size() > 0){
			comments = comments.parallelStream()
							.filter(c -> StringUtils.isNotBlank(c.comment))
							.map(c -> {
								c.comment = c.comment.trim();
								if(null == c.createUser){
									c.createUser = contextValidation.getUser();
									c.creationDate = new Date();
									c.code = CodeHelper.getInstance().generateExperimentCommentCode(c);
								}
								return c;
							}).collect(Collectors.toList());
			if(comments.size() > 0)return comments;
		}
		return new ArrayList<Comment>(0);					
	}
	
	
	@Deprecated
	public static void updateTraceInformation(TraceInformation traceInformation, String user) {

		if (traceInformation.createUser == null) {
			traceInformation.createUser = user;
		} else {
			traceInformation.modifyUser = user;
		}

		if (traceInformation.creationDate == null) {
			traceInformation.creationDate = new Date();
		} else {
			traceInformation.modifyDate = new Date();
		}

	}
	@Deprecated
	public static TraceInformation updateTraceInformation(TraceInformation traceInformation, State nextState) {
		traceInformation.modifyDate = nextState.date;
		traceInformation.modifyUser = nextState.user;
		return traceInformation;
	}
	@Deprecated
	public static TraceInformation getUpdateTraceInformation(TraceInformation traceInformation, String user) {
		TraceInformation ti = null;
		if (traceInformation == null) {
			ti = new TraceInformation();
		} else {
			ti = traceInformation;
		}
		ti.setTraceInformation(user);
		return ti;
	}

	public static void copyPropertyValueFromPropertiesDefinition(List<PropertyDefinition> propertyDefinitions,
			                                                     Map<String, PropertyValue<?>> propertiesInput, 
			                                                     Map<String, PropertyValue<?>> propertiesOutPut) {
		// TODO: fix as it i is a meaningless creation and could as well return
		//       immediately without doing anything.
		if (propertiesOutPut == null) {
			propertiesOutPut = new HashMap<>(); // <String, PropertyValue>();
		}
		
		for (PropertyDefinition propertyDefinition : propertyDefinitions) {
			PropertyValue<?> propertyValue = propertiesInput.get(propertyDefinition.code);
			if (propertyValue != null) {
				propertiesOutPut.put(propertyDefinition.code, propertyValue);
			}
		}

	}
	
	public static Set<String> getDeletedPropertyDefinitionCode(List<PropertyDefinition> propertyDefinitions, Map<String, PropertyValue<?>> propertiesInput) {
		return propertyDefinitions.stream()
			.filter(pd -> !propertiesInput.containsKey(pd.code))
			.map(pd -> pd.code)
			.collect(Collectors.toSet());			
	}
	
	public static DBObject save(String collectionName, IValidation obj, ContextValidation contextError,
			Boolean keepRootKeyName) {
		ContextValidation localContextError = new ContextValidation(contextError.getUser());
		localContextError.setMode(contextError.getMode());
		if (keepRootKeyName) {
			localContextError.addKeyToRootKeyName(contextError.getRootKeyName());
		}
		localContextError.setContextObjects(contextError.getContextObjects());

		if (obj != null) {
			obj.validate(localContextError);
		} else {
			throw new IllegalArgumentException("missing object to validate");
		}

		if (localContextError.errors.size() == 0) {
			return MongoDBDAO.save(collectionName, (DBObject) obj);
		} else {
			contextError.errors.putAll(localContextError.errors);
			Logger.info("error(s) on output :: " + contextError.errors.toString());
			return null;
		}
	}

	public static DBObject save(String collectionName, IValidation obj, ContextValidation contextError) {
		return save(collectionName, obj, contextError, false);
	}

	public static <T extends DBObject> List<T> save(String collectionName, List<T> objects,
			ContextValidation contextErrors) {

		List<T> dbObjects = new ArrayList<T>();

		for (DBObject object : objects) {
			@SuppressWarnings("unchecked")
			T result = (T) InstanceHelpers.save(collectionName, (IValidation) object, contextErrors);
			if (result != null) {
				dbObjects.add(result);
			}
		}

		return (List<T>) dbObjects;
	}

	public static <T extends DBObject> List<T> save(String collectionName, List<T> objects,
			ContextValidation contextErrors, Boolean keepRootKeyName) {

		List<T> dbObjects = new ArrayList<T>();

		for (DBObject object : objects) {
			@SuppressWarnings("unchecked")
			T result = (T) InstanceHelpers.save(collectionName, (IValidation) object, contextErrors, keepRootKeyName);
			if (result != null) {
				dbObjects.add(result);
			}
		}

		return (List<T>) dbObjects;
	}

	public static SampleOnContainer getSampleOnContainer(ReadSet readSet) {
		// 1 retrieve containerSupportCode from Run
		String containerSupportCode = getContainerSupportCode(readSet);
		Container container = getContainer(readSet, containerSupportCode);
		if (null != container) {
			Content content = getContent(container, readSet);
			if (null != content) {
				SampleOnContainer sampleContainer = convertToSampleOnContainer(readSet, containerSupportCode,
						container, content);
				return sampleContainer;
			}
		}
		return null;
	}

	public static SampleOnInputContainer getSampleOnInputContainer(Content content,Container container) {

		SampleOnInputContainer sampleOnInputContainer = new SampleOnInputContainer();
		sampleOnInputContainer.projectCode        = content.projectCode;
		sampleOnInputContainer.sampleCode         = content.sampleCode;
		sampleOnInputContainer.sampleCategoryCode = content.sampleCategoryCode;
		sampleOnInputContainer.sampleTypeCode     = content.sampleTypeCode;
		sampleOnInputContainer.percentage         = content.percentage;
		sampleOnInputContainer.properties         = content.properties;

		Sample sample = getSample(content.sampleCode);
		
		sampleOnInputContainer.referenceCollab = sample.referenceCollab;
		sampleOnInputContainer.ncbiScientificName = sample.ncbiScientificName;
		sampleOnInputContainer.taxonCode = sample.taxonCode;
		
		sampleOnInputContainer.containerConcentration = container.concentration;
		sampleOnInputContainer.containerCode = container.code;
		sampleOnInputContainer.containerSupportCode = container.support.code;
		sampleOnInputContainer.containerVolume = container.volume;
		sampleOnInputContainer.containerQuantity = container.quantity;
		sampleOnInputContainer.containerConcentration = container.concentration;

		sampleOnInputContainer.lastUpdateDate = new Date();
		return sampleOnInputContainer;
	}

	private static SampleOnContainer convertToSampleOnContainer(ReadSet readSet, String containerSupportCode,
			Container container, Content content) {
		SampleOnContainer sc = new SampleOnContainer();
		sc.lastUpdateDate = new Date();
		sc.containerSupportCode = containerSupportCode;
		sc.containerCode = container.code;
		sc.projectCode = readSet.projectCode;
		sc.sampleCode = readSet.sampleCode;
		sc.sampleTypeCode = content.sampleTypeCode;
		sc.sampleCategoryCode = content.sampleCategoryCode;
		sc.percentage = content.percentage;
		sc.properties = content.properties;
		sc.containerConcentration = container.concentration;
		
		Sample sample = getSample(content.sampleCode);
		sc.referenceCollab = sample.referenceCollab;
		sc.ncbiScientificName = sample.ncbiScientificName;
		sc.taxonCode = sample.taxonCode;
		
		return sc;
	}

	public static Sample convertToSample(ReadSet readSet)
	{
		SampleOnContainer sampleOnContainer = readSet.sampleOnContainer;
		Sample sample = new Sample();
		sample.code            = sampleOnContainer.sampleCode;
		sample.name            = sampleOnContainer.sampleCode;
		sample.typeCode        = sampleOnContainer.sampleTypeCode;
		sample.categoryCode    = sampleOnContainer.sampleCategoryCode;
		sample.properties      = sampleOnContainer.properties;
		sample.referenceCollab = sampleOnContainer.referenceCollab;
		sample.projectCodes    = new HashSet<>();
		sample.projectCodes.add(readSet.projectCode);
		sample.importTypeCode  = "external";
		InstanceHelpers.getUpdateTraceInformation(sample.traceInformation, "ngl-bi");
		return sample;
	}
	private static Content getContent(Container container, ReadSet readSet) {
		String tag = getTag(readSet);
		for (Content sampleUsed : container.contents) {
			try {
				if ((null == tag && sampleUsed.sampleCode.equals(readSet.sampleCode))
						|| (null != tag && null != sampleUsed.properties.get(InstanceConstants.TAG_PROPERTY_NAME)
								&& tag.equals(convertTagCodeToTagShortName((String)sampleUsed.properties.get(InstanceConstants.TAG_PROPERTY_NAME).value)) && sampleUsed.sampleCode
									.equals(readSet.sampleCode))) {
					return sampleUsed;
				}
			} catch (Exception e) {
				Logger.error("Problem with " + readSet.code + " / " + readSet.sampleCode + " : " + e.getMessage());
			}
		}
		Logger.warn("Not found Content for " + readSet.code + " / " + readSet.sampleCode);
		return null;
	}

	private static Object convertTagCodeToTagShortName(String tagCode) {
		Index index=MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.in("typeCode", "index-illumina-sequencing","index-nanopore-sequencing").is("code", tagCode));
		if(null != index){
			return index.shortName;
		}else{
			Logger.error("Index not found for code : "+tagCode);
			return null;
		}		
	}

	private static String getTag(ReadSet readSet) {
		String[] codeParts = readSet.code.split("\\.", 2);
		return (codeParts.length == 2) ? codeParts[1] : null;
	}
	
	
	private static Sample getSample(String sampleCode){
		return MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,
				DBQuery.is("code", sampleCode));
	}
	
	
	public static String getReferenceCollab(String sampleCode){
		Sample sample = getSample(sampleCode);
		return sample.referenceCollab;
	}

	private static Container getContainer(ReadSet readSet, String containerSupportCode) {
		MongoDBResult<Container> cl = MongoDBDAO.find(
				InstanceConstants.CONTAINER_COLL_NAME,
				Container.class,
				DBQuery.and(DBQuery.is("support.code", containerSupportCode),
						DBQuery.is("support.line", readSet.laneNumber.toString()),
						DBQuery.in("sampleCodes", readSet.sampleCode)));

		if (cl.size() == 0) {
			Logger.warn("Not found Container for " + readSet.code + " with : '" + containerSupportCode + ", "
					+ readSet.laneNumber.toString() + ", " + readSet.sampleCode + "'");
			return null;
		}

		return cl.toList().get(0);
	}

	private static String getContainerSupportCode(ReadSet readSet) {
		BasicDBObject keys = new BasicDBObject();
		keys.put("containerSupportCode", 1);
		Run r = MongoDBDAO
				.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", readSet.runCode), keys);
		return r.containerSupportCode;
	}
	
	/*
	 * Used to update content properties 
	 * @param properties
	 * @param newProperties
	 * @param deletedPropertyCodes
	 * @return
	 */
	public static Map<String, PropertyValue<?>> updateProperties(Map<String, PropertyValue<?>> properties, 
			                                                     Map<String, PropertyValue<?>> newProperties, 
			                                                     Set<String> deletedPropertyCodes) {
		properties.replaceAll((k,v) -> (newProperties.containsKey(k))?newProperties.get(k):v);							
		newProperties.forEach((k,v)-> properties.putIfAbsent(k, v));
		deletedPropertyCodes.forEach(code -> properties.remove(code));
		return properties;
	}
	
	/*
	 * Update properties with using the oldValue to check if update is needed
	 * @param properties
	 * @param newProperties
	 * @param deletedPropertyCodes
	 * @return
	 */
	public static Map<String, PropertyValue<?>> 
	              updatePropertiesWithOldValueComparison(Map<String, PropertyValue<?>> properties, 
	            		                                 Map<String, Pair<PropertyValue<?>,PropertyValue<?>>> newProperties, 
	            		                                 Set<String> deletedPropertyCodes) {
		//1 replace if old value equals old value
		properties.replaceAll((k,v) -> (newProperties.containsKey(k) && ((newProperties.get(k).getLeft() != null && newProperties.get(k).getLeft().equals(v)) || newProperties.get(k).getLeft() == null))?newProperties.get(k).getRight():v);							
		//2 add new properties
		newProperties.forEach((k,v)-> properties.putIfAbsent(k, newProperties.get(k).getRight()));
		//3 delete remove properties
		deletedPropertyCodes.forEach(code -> properties.remove(code));
		return properties;
	}
	
	public static void updateContentProperties(Set<String> projectCodes, 
			                                   Set<String> sampleCodes, 
			                                   Set<String> containerCodes,
			                                   Set<String> tags, 
			                                   Map<String, Pair<PropertyValue<?>,PropertyValue<?>>> updatedProperties, 
			                                   Set<String> deletedPropertyCodes,
			                                   ContextValidation validation) {
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,  DBQuery.in("code", containerCodes))
			.cursor
			.forEach(container -> {
				container.traceInformation.setTraceInformation(validation.getUser());
				container.contents.stream()
					.filter(content -> ((!content.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME) && sampleCodes.contains(content.sampleCode) && projectCodes.contains(content.projectCode))
							|| (null != tags  && content.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME) && sampleCodes.contains(content.sampleCode) && projectCodes.contains(content.projectCode) 
									&&  tags.contains(content.properties.get(InstanceConstants.TAG_PROPERTY_NAME).value))))
					.forEach(content -> {
						Query findContentQuery = Spring.getBeanOfType(ContentHelper.class).getContentQuery(container, content);
						content.properties = InstanceHelpers.updatePropertiesWithOldValueComparison(content.properties, updatedProperties, deletedPropertyCodes);		
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, findContentQuery, DBUpdate.set("contents.$", content));
					});			
		});
		
		MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.or(DBQuery.in("inputContainerCodes", containerCodes), DBQuery.in("outputContainerCodes", containerCodes)))
			.cursor.forEach(experiment -> {
				experiment.traceInformation.setTraceInformation(validation.getUser());
				experiment.atomicTransfertMethods.forEach(atm ->{
					atm.inputContainerUseds
						.stream()
						.filter(icu -> containerCodes.contains(icu.code))
						.map(icu -> icu.contents)
						.flatMap(List::stream)
						.filter(content -> sampleCodes.contains(content.sampleCode) && projectCodes.contains(content.projectCode) )
						.forEach(content -> {
							content.properties = InstanceHelpers.updatePropertiesWithOldValueComparison(content.properties, updatedProperties, deletedPropertyCodes);							
						});
					if (atm.outputContainerUseds != null) {
						atm.outputContainerUseds
							.stream()
							.filter(ocu -> containerCodes.contains(ocu.code))							
							.map(ocu -> ocu.contents)
							.flatMap(List::stream)
							.filter(content -> sampleCodes.contains(content.sampleCode) && projectCodes.contains(content.projectCode) )
							.forEach(content -> {
								content.properties = InstanceHelpers.updatePropertiesWithOldValueComparison(content.properties, updatedProperties, deletedPropertyCodes);							
							});
					}
				});				
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", experiment.code), 
						DBUpdate.set("atomicTransfertMethods", experiment.atomicTransfertMethods).set("traceInformation", experiment.traceInformation));	
		});	
		
		//update processes with new exp property values
		MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class, 
				DBQuery.in("sampleOnInputContainer.containerCode", containerCodes).in("sampleOnInputContainer.sampleCode", sampleCodes).in("sampleOnInputContainer.projectCode", projectCodes))
		.cursor
		.forEach(process -> {
			if(!process.sampleOnInputContainer.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME)
					|| (null != tags && process.sampleOnInputContainer.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME) 
					&&  tags.contains(process.sampleOnInputContainer.properties.get(InstanceConstants.TAG_PROPERTY_NAME).value))){
				process.traceInformation.setTraceInformation(validation.getUser());
				process.sampleOnInputContainer.lastUpdateDate = new Date();
				process.sampleOnInputContainer.properties = InstanceHelpers.updatePropertiesWithOldValueComparison(process.sampleOnInputContainer.properties, updatedProperties, deletedPropertyCodes);	
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, process);
			}
		});
		
		//update readsets with new exp property values
		MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,	
				DBQuery.in("sampleOnContainer.containerCode", containerCodes).in("sampleCode", sampleCodes).in("projectCode", projectCodes))
			.cursor
			.forEach(readset -> {
				if(!readset.sampleOnContainer.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME)
						|| (null != tags && readset.sampleOnContainer.properties.containsKey(InstanceConstants.TAG_PROPERTY_NAME) 
						&&  tags.contains(readset.sampleOnContainer.properties.get(InstanceConstants.TAG_PROPERTY_NAME).value))){
					readset.traceInformation.setTraceInformation(validation.getUser());
					readset.sampleOnContainer.lastUpdateDate = new Date();
					readset.sampleOnContainer.properties = InstanceHelpers.updatePropertiesWithOldValueComparison(readset.sampleOnContainer.properties, updatedProperties, deletedPropertyCodes);	
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readset);
				}
		});	
	}
	
	public static void updateContentProperties(Sample sample, 
			                                   Map<String, PropertyValue<?>> updatedProperties, 
			                                   Set<String> deletedPropertyCodes,
			                                   ContextValidation validation) {
		MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, 
				DBQuery.is("life.from.sampleCode",sample.code).in("life.from.projectCode", sample.projectCodes))
			.cursor.forEach(updatedSample -> {
				updatedSample.traceInformation.setTraceInformation(validation.getUser());
				updatedSample.referenceCollab = sample.referenceCollab;
				updatedSample.taxonCode = sample.taxonCode;
				updatedSample.ncbiScientificName = sample.ncbiScientificName;
				updatedSample.ncbiLineage = sample.ncbiLineage;
				updatedSample.properties = InstanceHelpers.updateProperties(updatedSample.properties, updatedProperties, deletedPropertyCodes);
				MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,updatedSample);
		});
		
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.is("contents.sampleCode", sample.code).in("contents.projectCode", sample.projectCodes))
			.cursor.forEach(container -> {
				container.traceInformation.setTraceInformation(validation.getUser());
				container.contents.stream()
					.filter(content -> sample.code.equals(content.sampleCode) && sample.projectCodes.contains(content.projectCode) )
					.forEach(content -> {
						Query findContentQuery = Spring.getBeanOfType(ContentHelper.class).getContentQuery(container, content);
						content.ncbiScientificName = sample.ncbiScientificName;
						content.taxonCode = sample.taxonCode;
						content.referenceCollab = sample.referenceCollab;
						
						content.properties = InstanceHelpers.updateProperties(content.properties, updatedProperties, deletedPropertyCodes);
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,findContentQuery, DBUpdate.set("contents.$", content));
					});				
		});
		
		MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.in("sampleCodes", sample.code).in("projectCodes", sample.projectCodes))
			.cursor.forEach(experiment -> {
				experiment.traceInformation.setTraceInformation(validation.getUser());
				experiment.atomicTransfertMethods.forEach(atm ->{
					atm.inputContainerUseds
						.stream()
						.map(icu -> icu.contents)
						.flatMap(List::stream)
						.filter(content -> sample.code.equals(content.sampleCode) && sample.projectCodes.contains(content.projectCode) )
						.forEach(content -> {
							content.ncbiScientificName = sample.ncbiScientificName;
							content.taxonCode = sample.taxonCode;
							content.referenceCollab = sample.referenceCollab;
							
							content.properties = InstanceHelpers.updateProperties(content.properties, updatedProperties, deletedPropertyCodes);							
						});
					if(null != atm.outputContainerUseds){
						atm.outputContainerUseds
							.stream()
							.map(ocu -> ocu.contents)
							.flatMap(List::stream)
							.filter(content -> sample.code.equals(content.sampleCode) && sample.projectCodes.contains(content.projectCode) )
							.forEach(content -> {
								content.ncbiScientificName = sample.ncbiScientificName;
								content.taxonCode = sample.taxonCode;
								content.referenceCollab = sample.referenceCollab;
								
								content.properties = InstanceHelpers.updateProperties(content.properties, updatedProperties, deletedPropertyCodes);							
							});
					}
				});				
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", experiment.code), 
						DBUpdate.set("atomicTransfertMethods", experiment.atomicTransfertMethods).set("traceInformation", experiment.traceInformation));	
		});
		
		// Processes update sampleOnInputContainer.properties		
		MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME,Process.class, 
				DBQuery.is("sampleOnInputContainer.sampleCode", sample.code).in("sampleOnInputContainer.projectCode", sample.projectCodes))
		.cursor
		.forEach(process -> {
			process.traceInformation.setTraceInformation(validation.getUser());
			process.sampleOnInputContainer.lastUpdateDate = new Date();
			
			process.sampleOnInputContainer.referenceCollab = sample.referenceCollab;
			process.sampleOnInputContainer.taxonCode = sample.taxonCode;
			process.sampleOnInputContainer.ncbiScientificName = sample.ncbiScientificName;
			
			process.sampleOnInputContainer.properties = InstanceHelpers.updateProperties(process.sampleOnInputContainer.properties, updatedProperties, deletedPropertyCodes);
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code", process.code), 
					DBUpdate.set("sampleOnInputContainer", process.sampleOnInputContainer).set("traceInformation", process.traceInformation));		
		});
		
		// ReadSet update sampleOnContainer.properties		
		MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,	
				DBQuery.is("sampleCode", sample.code).in("projectCode", sample.projectCodes)) 
		.cursor
		.forEach(readset -> {
			readset.traceInformation.setTraceInformation(validation.getUser());
			readset.sampleOnContainer.lastUpdateDate = new Date();
			
			readset.sampleOnContainer.referenceCollab = sample.referenceCollab;
			readset.sampleOnContainer.taxonCode = sample.taxonCode;
			readset.sampleOnContainer.ncbiScientificName = sample.ncbiScientificName;
			
			readset.sampleOnContainer.properties = InstanceHelpers.updateProperties(readset.sampleOnContainer.properties, updatedProperties, deletedPropertyCodes);
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Readset.class, DBQuery.is("code", readset.code), 
					DBUpdate.set("sampleOnContainer", readset.sampleOnContainer).set("traceInformation", readset.traceInformation));			
		});	
		
	}

	/*
	 * WARNING : NEED TO CALL AFTER VALIDATION BECAUSE SOME CONVERTION ARE EXECUTE DURING VALIDATION
	 * @param availablePropertyCodes
	 * @param oldProperties
	 * @param newProperties
	 * @return a pair with left element is the old propertyValue and right the new propertyValue
	 */
	public static Map<String, Pair<PropertyValue<?>,PropertyValue<?>>> 
	              getUpdatedPropertiesForSomePropertyCodes(Set<String> propertyCodes, 
	            		                                   Map<String, PropertyValue<?>> oldProperties,
	            		                                   Map<String, PropertyValue<?>> newProperties) {
		return propertyCodes.stream()
					 .filter(code -> newProperties.containsKey(code))
					 .filter(code -> !newProperties.get(code).equals(oldProperties.get(code)))
					 .collect(Collectors.toMap(code -> code, code -> Pair.of(oldProperties.get(code), newProperties.get(code))));		
	}
	
	/*
	 * WARNING : NEED TO CALL AFTER VALIDATION BECAUSE SOME CONVERTION ARE EXECUTE DURING VALIDATION
	 * @param availablePropertyCodes
	 * @param dbProperties
	 * @param newProperties
	 * @return
	 */
	public static Set<String> getDeletedPropertiesForSomePropertyCodes(Set<String> propertyCodes, 
			                                                           Map<String, PropertyValue<?>> dbProperties,
			                                                           Map<String, PropertyValue<?>> newProperties) {
		return propertyCodes.stream()
					 .filter(code -> dbProperties.containsKey(code) && !newProperties.containsKey(code))
					 .collect(Collectors.toSet());		
	}
		
}
