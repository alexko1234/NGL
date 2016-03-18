package controllers.migration.cns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.ContainerSupportLocation;
import fr.cea.ig.MongoDBDAO;

public class MigrationUpdateSupportPlaque extends CommonController{
	
	protected static ALogger logger=Logger.of("MigrationUpdateSupportPlaque");
	

	public static Result migration() {
		updateSupportContainerBanqueAmpli();
		//updateSupportContainerSolutionStock();
		return ok("Migration Support Container Finish");
	}

	// TO FINISH and TEST
	private static void updateSupportContainerBanqueAmpli() {
		List<Container> containerBanqueAmpliPlaque=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,
					DBQuery.is("categoryCode", "96-well-plate").is("fromTransformationTypeCodes", "amplification")).toList();
		
		/*for(Container c: containerBanqueAmpliPlaque){
			
			if(!MongoDBDAO.checkObjectExist("tmp.updateSupportBanqueAmpli", ContainerSupportLocation.class,DBQuery.is("code",c.code))){
				
			}
			
		}*/
		
		List<ContainerSupportLocation> containerSupportLocation=MongoDBDAO.find("tmp.updateSupportBanqueAmpli", ContainerSupportLocation.class).toList();
		ContextValidation contextValidation=new ContextValidation("ngl");

		if(containerSupportLocation==null || containerSupportLocation.size()==0){
			logger.error("Pas d'elements dans la collection tmp.updateSupportBanqueAmpli");
		}else {
			
			List<Container> updateContainers=new ArrayList<Container>();
			for(ContainerSupportLocation c:containerSupportLocation){
				Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,c.container);
				if(container==null){
					logger.error("Le container "+c.container+" n'existe pas");
				}
				if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code)){
					MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,container.support.code);
				}
				container.support.code=c.support;
				container.support.line=c.line;
				container.support.column=c.column;
				container.support.categoryCode="96-well-plate";
				container.categoryCode="well";
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", container.code),
						DBUpdate.set("support",container.support).set("categoryCode", container.categoryCode)
						);
				
				updateContainers.add(container);
			}
			Map<String,PropertyValue<String>> propertiesContainerSupports=new HashMap<String, PropertyValue<String>>();

			ContainerHelper.createSupportFromContainers(updateContainers, propertiesContainerSupports, contextValidation);
			
		}
		
		
		
	}
	
	
/*	private static void updateSupportContainerSolutionStock() {
		List<ContainerSupportLocation> containerSupportLocation=MongoDBDAO.find("tmp.updateSupportSolutionStock", ContainerSupportLocation.class).toList();
		ContextValidation contextValidation=new ContextValidation("ngl");

		if(containerSupportLocation==null || containerSupportLocation.size()==0){
			logger.error("Pas d'elements dans la collection tmp.updateSupportSolutionStock");
		}else {
			
			List<Container> updateContainers=new ArrayList<Container>();
			for(ContainerSupportLocation c:containerSupportLocation){
				Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,c.container);
				if(container==null){
					logger.error("Le container "+c.container+" n'existe pas");
				}
				if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code)){
					MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,container.support.code);
				}
				container.support.code=c.support;
				container.support.line=c.line;
				container.support.column=c.column;
				container.support.categoryCode="96-well-plate";
				container.categoryCode="well";
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", container.code),
						DBUpdate.set("support",container.support).set("categoryCode", container.categoryCode)
						);
				
				updateContainers.add(container);
			}
			Map<String,PropertyValue<String>> propertiesContainerSupports=new HashMap<String, PropertyValue<String>>();

			ContainerHelper.createSupportFromContainers(updateContainers, propertiesContainerSupports, contextValidation);
			
		}
		
		
		
	}*/
}
