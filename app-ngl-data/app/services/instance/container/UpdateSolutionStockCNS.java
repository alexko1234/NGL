package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import models.Constants;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import workflows.container.ContainerWorkflows;

import com.mongodb.MongoException;

import fr.cea.ig.MongoDBDAO;

public class UpdateSolutionStockCNS extends AbstractImportDataCNS {

	public UpdateSolutionStockCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSolutionStock", durationFromStart, durationFromNextIteration);

	}

	@Override
	public void runImport() throws SQLException, DAOException, MongoException, RulesException {
		updateSolutionStock("pl_SolutionStockToNGL @updated=1",contextError,"tube","solution-stock");

	}

	public static void updateSolutionStock(String sql,ContextValidation contextError,String containerCategoryCode,String experimentTypeCode) throws SQLException {
		List<Container> containers=	limsServices.findContainersToCreate(sql,contextError, containerCategoryCode,null,experimentTypeCode);
		List<Container> containerUpdated=new ArrayList<Container>();
		for(Container containerUpdate:containers){
			Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,containerUpdate.code);
			if(container==null){
				contextError.addErrors("container.code", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG , containerUpdate.code);
			}
			else if(container.state.code!=containerUpdate.state.code){
				//Update state container
				ContextValidation contextValidation= new ContextValidation(Constants.NGL_DATA_USER);
				if(containerUpdate.state.code.equals("IS")&& CollectionUtils.isNotEmpty(container.inputProcessCodes)){
					contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, container.code);
				}else {
					ContainerWorkflows.setContainerState(container, containerUpdate.state, contextValidation);
//					ContainerWorkflows.setContainerState(container.code, container.fromExperimentTypeCodes.get(0), containerUpdate.state, contextValidation, false, false, null);
				}
				
				if(!contextValidation.hasErrors()){
					containerUpdated.add(container);
				} else { contextError.errors.putAll(contextValidation.errors);
				}
			}
			limsServices.updateMaterielmanipLims(containerUpdated, contextError);
		}
	}
}
