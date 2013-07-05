package models.utils.instance;

import java.util.ArrayList;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.SampleUsed;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import validation.utils.BusinessValidationHelper;

public class ContainerHelper {

	public static ContainerSupport getContainerSupportTube(String barCode){
		ContainerSupport containerSupport=new ContainerSupport();
		containerSupport.barCode=barCode;	
		containerSupport.categoryCode="tube";
		containerSupport.x="1";
		containerSupport.y="1";
		return containerSupport;
	}


	public static void addContent(Container container,Sample sample) throws DAOException{

		//Create new content
		if(container.contents==null){
			container.contents=new ArrayList<Content>();
		}

		Content content = new Content(new SampleUsed(sample.code, sample.typeCode, sample.categoryCode));

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);
				
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
