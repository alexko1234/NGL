package controllers.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.util.DataMappingCNS;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import play.Logger;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.container.ContainerOld;
import fr.cea.ig.MongoDBDAO;

@Repository
public class MigrationStateContainer   extends CommonController{
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	@Qualifier("lims")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);              
	}
	
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_ContainerState";

	public static Result migration() {

		MigrationStateContainer migrationStateContainer=new MigrationStateContainer();

		List<ContainerOld> containersCollBck = MongoDBDAO.find(CONTAINER_COLL_NAME_BCK, ContainerOld.class).toList();
		if(containersCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 1.a Update state Container starts");
			backupContainerCollection();
			// all container
			List<Container> containersList = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
			Logger.debug("Update state "+containersList.size()+" CONTAINERS");
			
			//update state container
			migrationStateContainer.updateStateContainerAndContainerSupport();

			//update sampleCodes in container
			for(Container container:containersList){
				List<String> sampleCodes=new ArrayList<String>();
				for(Content content : container.contents){
					InstanceHelpers.addCode(content.sampleCode, sampleCodes);
				}
				
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class
						,DBQuery.is("code",container.code),DBUpdate.set("sampleCodes", sampleCodes));
			}
			
			//udpdate property container lane
			for(Container container:containersList){
			migrationStateContainer.updateContentPropertyLibProcessTypeCode(container);
			}
			Logger.info(">>>>>>>>>>> 1.b Update State Container end");
		} else {
			Logger.info("Update State CONTAINER already execute !");
		}
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		return ok("Migration Container "+ containers.size()+ "Finish");
	}
	

	 private void updateContentPropertyLibProcessTypeCode(Container container) {
		
	}


	private class StateContainerSupport{
		public Integer stateCode;
		public String containerSupportCode;
		public String containerCategoryCode;
		public StateContainerSupport(){
		}
	}

	public void updateStateContainerAndContainerSupport() {
		ContextValidation contextError=new ContextValidation();
		String sql="select containerCategoryCode='tube',stateCode=etubco, containerSupportCode=tubnom from Tubeident t, Materielmanip m where m.matmaco=t.matmaco and m.matmaInNGL!=null " +
				"union" +
				"select  containerCategoryCode='lane',stateCode=ematerielco , containerSupportCode=lotrearef from Prepaflowcell p, Materielmanip m, Lotreactif l where p.matmaco=m.matmaco and m.matmaInNGL!=null and l.lotreaco=p.lotreaco ";
		
		Logger.debug("SQL "+sql);
		List<StateContainerSupport> results = this.jdbcTemplate.query(sql,new Object[]{} 
		,new RowMapper<StateContainerSupport>() {

			@SuppressWarnings("rawtypes")
			public StateContainerSupport mapRow(ResultSet rs, int rowNum) throws SQLException {
				StateContainerSupport stateContainer = new StateContainerSupport();
				stateContainer.stateCode=rs.getInt("stateCode");
				stateContainer.containerSupportCode=rs.getString("containerSupportCode");
				stateContainer.containerCategoryCode=rs.getString("containerCategoryCode");				
				return stateContainer;
			}
		});
		
		for(StateContainerSupport stateContainerSupport:results){
			String newStateCode=DataMappingCNS.getState(stateContainerSupport.containerCategoryCode, stateContainerSupport.stateCode);
			Logger.debug("New state "+newStateCode+" for ContainerSupport "+stateContainerSupport.containerSupportCode);
			
			/*MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,ContainerSupport.class
					,DBQuery.is("code",stateContainerSupport.containerSupportCode),DBUpdate.set("state.code",newStateCode));
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class
					,DBQuery.is("support.code",stateContainerSupport.containerSupportCode),DBUpdate.set("state.code",newStateCode),true);
		*/
		}
	}

	private static void backupContainerCollection() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}
	
	

}
