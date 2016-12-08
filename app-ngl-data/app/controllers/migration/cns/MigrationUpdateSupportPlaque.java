package controllers.migration.cns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.jdbc.core.RowMapper;

import lims.cns.services.LimsServiceCNS;
import models.LimsCNSDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import play.Logger;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import services.instance.container.ContainerImportCNS;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.ContainerSupportLocation;
import fr.cea.ig.MongoDBDAO;

public class MigrationUpdateSupportPlaque extends CommonController{

	protected static ALogger logger=Logger.of("MigrationUpdateSupportPlaque");
	protected static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);

	public static Result migration() {
		//updateSupportContainerBanqueAmpli();
		//updateSupportContainerSolutionStock();
		//updateSupportContainerBanqueAmpliPlaqueToTube();
		updateSupportContainerTubeLimsToPlaque();
		return ok("Migration Support Container Finish");
	}

	private static void updateSupportContainerTubeLimsToPlaque() {

		List<Container> results = limsServices.jdbcTemplate.query("select m.plaqueId, m.plaqueX, m.plaqueY, code=tubnom from Materielmanip m, Tubeident t where m.matmaco=t.matmaco and matmaInNGL!=null and m.plaqueId!=null ",new Object[]{} 
		,new RowMapper<Container>() {

			@SuppressWarnings("rawtypes")
			public Container mapRow(ResultSet rs, int rowNum) throws SQLException {

				Container container = new Container();

				container.code=rs.getString("code");
				container.support=new LocationOnContainerSupport();
				container.support.code=rs.getString("plaqueId");
				container.support.line=rs.getString("plaqueY");
				container.support.column=rs.getString("plaqueX");
				container.support.categoryCode="96-well-plate";
				return container;
			}

		});

		List<Container> updatedContainers=new ArrayList<Container>();
		//delete container support
		for(Container container : results){
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code",container.code), DBUpdate.set("support",container.support).set("categoryCode","well"));
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);
			updatedContainers.add(MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code));
		}
		
		//Create support container
		ContextValidation contextError=new ContextValidation("ngl");
		ContainerHelper.createSupportFromContainers(updatedContainers,null, contextError);

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


	private static void updateSupportContainerSolutionStock() {
		List<ContainerSupportLocation> containerSupportLocation=MongoDBDAO.find("tmp.updateSupportSolutionStock", ContainerSupportLocation.class).toList();
		ContextValidation contextValidation=new ContextValidation("ngl");

		if(containerSupportLocation==null || containerSupportLocation.size()==0){
			logger.error("Pas d'elements dans la collection tmp.updateSupportSolutionStock");
		}else {

			List<Container> updateContainers=new ArrayList<Container>();
			for(ContainerSupportLocation c:containerSupportLocation){

				// if(c.support.equals("STK_0B5D5FA1T")){
				Logger.debug(c.toString());
				Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,c.container);
				if(container==null){
					logger.error("Le container "+c.container+" n'existe pas");
				}
				else {
					if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code)){
						MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,container.support.code);
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
				//} 
			}

			Map<String,PropertyValue<String>> propertiesContainerSupports=new HashMap<String, PropertyValue<String>>();
			ContainerHelper.createSupportFromContainers(updateContainers, propertiesContainerSupports, contextValidation);

		}



	}

	private static void updateSupportContainerBanqueAmpliPlaqueToTube() {
		List<Container> updateContainers=new ArrayList<Container>();
		ContextValidation contextValidation=new ContextValidation("ngl");
		List<String> excludePlates= Arrays.asList("PCR_0AU84QPO9",
				"PCR_0B595ADPZ",
				"PCR_0B5E2A55A",
				"PCR_0B9752ARL",
				"PCR_0B9D3VB1F",
				"PCR_0BCD43HJJ",
				"PCR_0BDE2K48O",
				"PCR_0BGD4I3MZ",
				"PCR_0BHC5FHHU",
				"PCR_0BID2401Y",
				"PCR_0BJD2MMPU",
				"PCR_0BKD2VEDE",
				"PCR_0BND2MU3P",
				"PCR_11ED3QPGE",
				"PCR_123D1S4AK",
				"PCR_146D346QW",
				"PCR_146E1F5PL",
				"PCR_14FE1WBNA",
				"PCR_162D1C4LE",
				"PCR_16EC54O6E",
				"PCR_16KD48ZJ6",
				"PCR_16RB4D0D7",
				"PCR_175C2TJSC",
				"PCR_176D3O1QG",
				"PCR_17BE43YO3",
				"PCR_17BE4A49A",
				"PCR_17ME53ON0",
				"PCR_17SE33WK0",
				"PCR_18884HURA",
				"PCR_192D5I8NH",
				"PCR_1A4D2V047",
				"PCRE_BNR_0ATG38Z4N",
				"PCRE_BNR_0ATG38Z4U",
				"PCRE_BNR_0C1956SEA",
				"PCRE_BNR_0C1956SFZ");

		List<ContainerSupport> containerSupports=MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("fromTransformationTypeCodes", "pcr-amplification-and-purification").is("categoryCode","96-well-plate")).toList();
		Logger.debug("Nb containersupport banque ampli "+containerSupports.size());

		for(ContainerSupport support :containerSupports){
			 
			if(!excludePlates.contains(support.code)){

				//Container creer dans le Lims
				List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", support.code).exists("properties.limsCode")).toList();
								
				if(containers.size()>0){
					Logger.debug("Nb container "+containers.size()+" pour le support "+support.code);

					for(Container container:containers){

						container.support.line="1";
						container.support.column="1";
						container.support.code=container.code;
						container.support.categoryCode="tube";
						container.categoryCode="tube";

						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", container.code),
								DBUpdate.set("support",container.support).set("categoryCode", container.categoryCode)
								);

						updateContainers.add(container);
					}

					MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, support.code);
				}

			}
		}

		Map<String,PropertyValue<String>> propertiesContainerSupports=new HashMap<String, PropertyValue<String>>();
		ContainerHelper.createSupportFromContainers(updateContainers, propertiesContainerSupports, contextValidation);

	}

}
