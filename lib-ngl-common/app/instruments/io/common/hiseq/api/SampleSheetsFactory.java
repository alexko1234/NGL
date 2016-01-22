package instruments.io.common.hiseq.api;

import fr.cea.ig.MongoDBDAO;
import instruments.io.common.hiseq.tpl.txt.sampleSheet_1;
import instruments.io.utils.AbstractSampleSheetsfactory;
import instruments.io.utils.SampleSheetsFactoryHelper;
import instruments.io.utils.TagModel;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;


public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		super(varExperiment);
	}

	@Override
	public File generate() {
		List<Container> containers = getInputContainersFromExperiment();
		
		TagModel tagModel = getTagModel(containers);
		
		
		String content = format(sampleSheet_1.render(experiment,containers,tagModel).body());
		File file = new File(SampleSheetsFactoryHelper.getSampleSheetFilePath(experiment.instrument.code)+containers.get(0).support.code+".csv");
		//play.api.libs.Files.writeFile(file, content);
		AbstractSampleSheetsfactory.writeFile(file, content);
		
		return file;
	}

	private TagModel getTagModel(List<Container> containers) {
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