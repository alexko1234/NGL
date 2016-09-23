package controllers.migration.cns;

import java.util.List;





import models.LimsCNSDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;





import org.mongojack.DBQuery;


import org.mongojack.DBUpdate;

import play.Logger;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import services.instance.sample.UpdateSamplePropertiesCNS;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class MigrationContainerProperties  extends CommonController{

	protected static ALogger logger=Logger.of("MigrationContainerProperties");
	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);

	public static Result migration() {
		ContextValidation contextError=new ContextValidation("ngl-sq");
		List<Container> containers=null;

	//	List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("properties.limsCode").is("code","BNU_A1")/*.notExists("contents.properties.sampleAliquotCode")*/.notExists("fromTransformationTypeCodes")).toList();
/*
		Logger.info("Nb containers to update :"+containers.size());

		containers.forEach(c->{
			MigrationContainerProperties.updateProperties(c, "sampleAliquotCode", new PropertySingleValue(c.code), c.contents.get(0).sampleCode, null);
		});

*/
		
		containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("properties.limsCode").notExists("contents.properties.sampleAliquotCode").size("fromTransformationTypeCodes", 1)).toList();

		Logger.info("Nb containers to update :"+containers.size());
		containers.forEach(container -> {
			Logger.debug("Container "+container.code);
			try {

				List<Content> contents =limsServices.findContentsFromContainer("pl_ContentFromContainer @matmanom=?", container.code);
				contents.forEach(c->{
					Logger.debug("Content container :"+ container.code+", sample "+c.sampleCode+", tag "+c.properties.get("tag").value);
					MigrationContainerProperties.updateProperties(container, "sampleAliquotCode", (PropertySingleValue) c.properties.get("sampleAliquotCode"), c.sampleCode, c.properties.get("tag").value.toString());
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		

		return ok("Migration update sampleAliquotCode Finish");
	}

	public static void updateProperties(Container container, String propertyName, PropertySingleValue propertyValue,String sampleCode, String tag){
		if(propertyValue!=null){
			Logger.debug("Update Container :"+container.code);

			DBQuery.Query queryContainer=DBQuery.is("code",container.code).is("contents.sampleCode", sampleCode);

			if(tag!=null){
				queryContainer.and(DBQuery.is("contents.properties.tag.value",tag));
			}

			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class
					,queryContainer
					,DBUpdate.set("contents.$.properties."+propertyName,propertyValue));

			//Update next container
			List<Container> sonContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("treeOfLife.from.containers.code",container.code)).toList();

			if(sonContainers!=null && sonContainers.size()>0){
				sonContainers.forEach(sc->{
					MigrationContainerProperties.updateProperties(sc, propertyName, propertyValue, sampleCode, tag);
				});
			}else {
				//Update readSet if exists
				//Logger.debug("Update readSets to container"+container.code);
				DBQuery.Query queryReadSet=DBQuery.is("sampleOnContainer.containerCode",container.code)
						.is("sampleOnContainer.sampleCode",sampleCode);

				if(tag!=null){
					queryReadSet.and(DBQuery.is("sampleOnContainer.properties.tag.value",tag));
				}


				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class
						, queryReadSet,DBUpdate.set("sampleOnContainer.properties."+propertyName,propertyValue));
			}
		}else {
			logger.error("No "+propertyName +" for container "+container.code);
		}

	}

}
