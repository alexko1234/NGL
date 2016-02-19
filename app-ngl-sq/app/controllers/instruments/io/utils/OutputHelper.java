package controllers.instruments.io.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.parameter.Index;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
import play.Play;
import scala.io.Codec;

public class OutputHelper {

	
	public static String getInstrumentPath(String instrumentCode){
		Instrument instrument = null;
		try {
			instrument = Instrument.find.findByCode(instrumentCode);
		} catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
		}
		if(instrument != null){
			if(Play.application().configuration().getString("ngl.path.instrument") != null){
				return Play.application().configuration().getString("ngl.path.instrument")+File.separator;
			}else{
				return instrument.path+File.separator+"SampleSheet"+File.separator;
			}
		}
		return null;
	}
	
	public static void writeFile(File file, String content){
		Writer writer = null;
		try {
			
			FileOutputStream fos = new FileOutputStream(file);
			writer = new OutputStreamWriter(fos, Codec.UTF8().name());			
			writer.write(content);
			writer.append("\r\n");
			writer.close();
			fos.close();
			
		} catch (Exception e) {
			Logger.error("Problem to create sample sheet",e);
			Logger.error("DAO error: "+e.getMessage(),e);
		}
	}
	
	public static String format(String content){
		if(content != null){
			return content.trim().replaceAll("(?m)^\\s{1,}", "").replaceAll("\n{2,}", "\n");
		}
		return "";
	}
	
	public static List<Container> getInputContainersFromExperiment(Experiment experiment){
		List<Container> containers = new ArrayList<Container>();
		for(int i=0; i<experiment.atomicTransfertMethods.size();i++){
			for(InputContainerUsed cu : experiment.atomicTransfertMethods.get(i).inputContainerUseds){
				containers.add(MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, cu.code));
			}
		}
		
		return containers;
	}
	
	public static String getOutputContainerUsedCode(AtomicTransfertMethod atomic){		
		return atomic.outputContainerUseds.get(0).code;
	}
	
	public static Index getIndex(String typeCode, String code){
		Index index  = MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("typeCode", typeCode).and(DBQuery.is("code", code)));
		return index;
	}
	
	public static String getSequence(Index index, TagModel tagModel){
		if("NONE".equals(tagModel.tagType)){
			return null;
		}else if("SINGLE-INDEX".equals(tagModel.tagType)){
			if(null == index || "MID".equals(index.categoryCode)){
				return getIndex(null, tagModel.maxTag1Size);
			} else {
				return getIndex(index.sequence, tagModel.maxTag1Size);
			}
		}else if("DUAL-INDEX".equals(tagModel.tagType)){
			if(null == index || "MID".equals(index.categoryCode)){
				return StringUtils.repeat("N", tagModel.maxTag1Size)+"-"+StringUtils.repeat("N", tagModel.maxTag2Size);
			}else if("SINGLE-INDEX".equals(index.categoryCode)){
				return getIndex(index.sequence, tagModel.maxTag1Size)+"-"+getIndex(null, tagModel.maxTag2Size);
			}else {
				String[] sequences = index.sequence.split("-",2);
				return getIndex(sequences[0], tagModel.maxTag1Size)+"-"+getIndex(sequences[1], tagModel.maxTag2Size);
			}
		}else{
			throw new RuntimeException("Index not manage "+tagModel.tagType);
		}
	}

	private static String getIndex(String sequence, Integer maxIndexSize) {
		if(null == sequence){
			return StringUtils.repeat("N", maxIndexSize);
		}else if(sequence.length() < maxIndexSize){
			return sequence.concat(StringUtils.repeat("N", maxIndexSize-sequence.length()));
		}else{
			return sequence;
		}
	}
	
	public static String getSequence(Index index){
		if(index != null && !index.categoryCode.equals("MID")){
			return index.sequence;
		}else{
			return null;
		}
	}
	
	public static String getContentProperty(Content content, String propertyName){
		if(content.properties.get(propertyName) != null){
			return (String) content.properties.get(propertyName).value;
		}
		return "";
	}
	
	public static Double getContentDoubleProperty(Content content, String propertyName){
		if(content.properties.get(propertyName) != null){
			return  (Double) content.properties.get(propertyName).value;
		}
		return 0.0;
	}	
	
	public static String getSupplierName(Index tag, String supplierName){
		if(tag!= null && tag.supplierName != null){
			return tag.supplierName.get("illumina");
		}
		
		return "";
	}
	
	public static String getIntrumentBooleanProperties(Experiment experiment,String propertyName){
		if(experiment.instrumentProperties.get(propertyName) != null && Boolean.class.isInstance(experiment.instrumentProperties.get(propertyName).value)){
			if((Boolean) experiment.instrumentProperties.get(propertyName).value){
				return "O";
			}
		}
		return "N";
	}
	
	public static String getContainerProperty(Container container, String propertyName){
		if(container.properties.get(propertyName) != null && Boolean.class.isInstance(container.properties.get(propertyName).value)){
			if((Boolean) container.properties.get(propertyName).value){
				return "O";
			}
			return "N";
		}
		
		return (String) container.properties.get(propertyName).value;
	}
	
	public static String getInputContainerUsedExperimentProperty(InputContainerUsed container, String propertyName){		
		return container.experimentProperties.get(propertyName).value.toString().replace(".",",") ;
	}
	
	public static TagModel getTagModel(List<Container> containers) {
		List<PropertyValue> tags = containers.stream().map((Container container) -> container.contents)
			.flatMap(List::stream)
			.filter(c -> c.properties.containsKey("tag"))
			.filter(c -> !c.properties.get("tagCategory").equals("MID"))
			.map((Content c) -> c.properties.get("tag"))
			.collect(Collectors.toList())
			;
		TagModel tagModel = new TagModel();
		if(tags.size() > 0){
			tagModel.maxTag1Size = 0;
			tagModel.maxTag2Size = 0;
			tagModel.tagType = "SINGLE-INDEX";
			for(PropertyValue _tag : tags){
				PropertySingleValue tag = (PropertySingleValue)_tag;
				Index index = getIndex("index-illumina-sequencing", tag.value.toString());
				
				if("SINGLE-INDEX".equals(index.categoryCode)) {
					if(index.sequence.length() > tagModel.maxTag1Size){
						tagModel.maxTag1Size = index.sequence.length();
					}
				}else if("DUAL-INDEX".equals(index.categoryCode)) {
					tagModel.tagType = "DUAL-INDEX";
					
					String[] sequences = index.sequence.split("-",2);
					if(sequences[0].length() > tagModel.maxTag1Size){
						tagModel.maxTag1Size = sequences[0].length();
					}
					
					if(sequences[1].length() > tagModel.maxTag2Size){
						tagModel.maxTag2Size = sequences[1].length();
					}
				}						
			};
		}else{
			tagModel.tagType = "NONE";
		}
				
		return tagModel;
	}
}
