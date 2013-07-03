package models.utils.instance;

import java.util.ArrayList;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceHelpers;
import play.Logger;

public class ContainerHelper {

	public static ContainerSupport getContainerSupportTube(String barCode){
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode="tube";
		containerSupport.x="1";
		containerSupport.y="1";
		return containerSupport;
	}


	public static void addContent(Container container,Sample sample){

		//Create new content
		if(container.contents==null){
			container.contents=new ArrayList<Content>();
		}

		Content content = new Content(new SampleUsed(sample.code, sample.typeCode, sample.categoryCode));

		SampleType sampleType =new HelperObjects<SampleType>().getObject(SampleType.class, sample.categoryCode);
		ImportType importType =new HelperObjects<ImportType>().getObject(ImportType.class, sample.importTypeCode);
				
		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromLevel(importType.getMapPropertyDefinition(), "Content", sample.properties,content.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromLevel(sampleType.getMapPropertyDefinition(), "Content", sample.properties,content.properties);
		}

		container.contents.add(content);

		container.projectCodes=InstanceHelpers.addCodesList(sample.projectCodes,container.projectCodes);

		container.sampleCodes=InstanceHelpers.addCode(sample.code,container.sampleCodes);

	}

}
