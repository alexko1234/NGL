package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.common.instance.PropertyValue;
import models.utils.InstanceConstants;
import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;

public class SwitchContainer extends CommonController{
	
	
	protected static ALogger logger=Logger.of("SwitchContainer");

	private static final String TAG_PROPERTY_NAME = "tag";
	
	public static Result migration() {
		String oldParentContainerName = "221D4N4IK_C3";
		String newParentContainerName = "221D4N4IK_E3";
		
		Container oldParentContainer = getContainer(oldParentContainerName);
		Container newParentContainer = getContainer(newParentContainerName);
		
		//TODO Load processes ??
		
		updateContainers(oldParentContainer,newParentContainer);
		return ok("SwitchContainer End");
	}


	private static Container getContainer(String code) {
		return MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, code);
	}


	private static void updateContainers(Container oldParent, Container newParent) {
		
		Set<String> oldParentProjectCodes = getNotExistInSecondElement(oldParent.projectCodes, newParent.projectCodes);
		Set<String> newParentProjectCodes =getNotExistInSecondElement(newParent.projectCodes, oldParent.projectCodes);
		
		Set<String> oldParentSampleCodes = getNotExistInSecondElement(oldParent.sampleCodes, newParent.sampleCodes);
		Set<String> newParentSampleCodes =getNotExistInSecondElement(newParent.sampleCodes, oldParent.sampleCodes);
		
		List<String> oldParentPaths = getPaths(oldParent.treeOfLife.paths, oldParent.code);
		List<String> newParentPaths = getPaths(newParent.treeOfLife.paths, newParent.code);
		
		
		Logger.info("oldParentProjectCodes : "+oldParentProjectCodes);
		Logger.info("newParentProjectCodes : "+newParentProjectCodes);
		
		Logger.info("oldParentSampleCodes : "+oldParentSampleCodes);
		Logger.info("newParentSampleCodes : "+newParentSampleCodes);
		
		Logger.info("oldParentPaths : "+oldParentPaths);
		Logger.info("newParentPaths : "+newParentPaths);
		
		Map<String, PropertyValue> allContentPropertiesKeep = new HashMap<String, PropertyValue>();
		//1 find all childs that's must be updated
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.regex("treeOfLife.paths", Pattern.compile(","+oldParent.code+"$|,"+oldParent.code+",")))
				.sort("traceInformation.creationDate").getCursor().forEach(container -> {
			Logger.info("Update : "+container.code+" / "+container.traceInformation.creationDate);
			
			container.projectCodes.removeAll(oldParentProjectCodes);
			container.projectCodes.addAll(newParentProjectCodes);
			
			container.sampleCodes.removeAll(oldParentSampleCodes);
			container.sampleCodes.addAll(newParentSampleCodes);
			
			//PATH
			List<String> oldPaths = container.treeOfLife.paths.parallelStream()
					.filter(path -> oldParentPaths.stream().map(oldPath -> path.startsWith(oldPath)).findFirst().get())
					.collect(Collectors.toList());
			
			List<String> newPaths = IntStream.range(0, oldPaths.size())
					.mapToObj(i -> oldPaths.get(i).replace(oldParentPaths.get(i), newParentPaths.get(i)))
					.collect(Collectors.toList());
			
			//Logger.debug("oldPaths : "+oldPaths);
			//Logger.debug("newPaths : "+newPaths);
			
			container.treeOfLife.paths.removeAll(oldPaths);
			container.treeOfLife.paths.addAll(newPaths);
			
			//CONTENTS
			Set<String> currentContentPropertyKeys = getContentPropertiesMustBeKeep(container);
			
			List<Content> oldContents = container.contents.stream()
					.filter(content -> oldParent.contents.stream()
								.map(oldContent -> isSameContent(content, oldContent))
								.findFirst().get())
					.collect(Collectors.toList());
			
			Map<String, PropertyValue> currentContentProperties = oldContents.parallelStream()
						.map(content -> content.properties.entrySet())
						.flatMap(Set::stream)
						.filter(entry -> currentContentPropertyKeys.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			
			allContentPropertiesKeep.putAll(currentContentProperties);
			
			/*
			List<Content> newContents =  IntStream.range(0, oldContents.size())
			.mapToObj(i -> oldContents.get(i).replace(oldParentPaths.get(i), newParentPaths.get(i)))
			.collect(Collectors.toList());
			*/
			Logger.debug("Nb container contents "+container.contents.size());
			
			List<Content> newContents =  newContents(newParent.contents, allContentPropertiesKeep);
			container.contents.removeAll(oldContents);
			container.contents.addAll(newContents);
			
			Logger.debug("Nb old contents "+oldContents.size());
			Logger.debug("Nb container contents "+container.contents.size());
			Logger.debug("Nb currentContentProperties "+currentContentProperties.size());
			Logger.debug("keep properties "+allContentPropertiesKeep);
		});
	}


	private static List<Content> newContents(List<Content> contents,
			Map<String, PropertyValue> allContentPropertiesKeep) {
		
		return contents.stream().map(c -> c.clone()).map(c -> {c.properties.putAll(allContentPropertiesKeep); return c;}).collect(Collectors.toList());
	}


	private static Set<String> getContentPropertiesMustBeKeep(Container container) {
		List<String> expTypeCodes = new ArrayList<String>();
		if(null!=container.fromTransformationTypeCodes)expTypeCodes.addAll(container.fromTransformationTypeCodes);
		if(null!=container.fromPurificationTypeCode)expTypeCodes.add(container.fromPurificationTypeCode);
		if(null!=container.fromTransfertTypeCode)expTypeCodes.add(container.fromTransfertTypeCode);
		
		if(null != container.qualityControlResults){
			expTypeCodes.addAll(container.qualityControlResults.stream().map(qc -> qc.typeCode).collect(Collectors.toList()));
		}
		
		List<ExperimentType> expTypes = ExperimentType.find.findByCodes(expTypeCodes);
		return expTypes.parallelStream()
			.map(expType -> expType.getPropertyDefinitionByLevel(Level.CODE.Content))
			.flatMap(List::stream)
			.map(pd -> pd.code)
			.collect(Collectors.toSet());		
	}


	private static boolean isSameContent(Content content, Content oldContent) {
		return content.projectCode.equals(oldContent.projectCode) 
							&& content.sampleCode.equals(oldContent.sampleCode)
							&& (!oldContent.properties.containsKey(TAG_PROPERTY_NAME) 
									|| (oldContent.properties.containsKey(TAG_PROPERTY_NAME) 
											&& content.properties.get(TAG_PROPERTY_NAME).value.equals(oldContent.properties.get(TAG_PROPERTY_NAME).value)));
		
		//TODO Manage fromSampleCode and fromProjectCode ???
		
	}


	private static List<String> getPaths(List<String> paths, String code) {
		return paths.stream().map(path -> path+","+code).collect(Collectors.toList());		
	}


	private static Set<String> getNotExistInSecondElement(Set<String> list1, Set<String> list2) {
		Set<String> retainElements = new TreeSet<String>(list1);
		retainElements.removeAll(list2);
		return retainElements;
	}
}
