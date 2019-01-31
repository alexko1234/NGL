package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import models.Constants;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
// import play.Logger;
// import play.api.modules.spring.Spring;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import workflows.container.ContWorkflows;

public abstract class UpdateContainerImportCNS extends AbstractImportDataCNS {
	
	@Inject
	public UpdateContainerImportCNS(String name,FiniteDuration durationFromStart, FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super(name,durationFromStart, durationFromNextIteration, ctx);
	}
	
	public void updateContainer(String sql,ContextValidation contextError,String containerCategoryCode,String experimentTypeCode) throws SQLException {
		List<Container> containers = limsServices.findContainersToCreate(sql,contextError, containerCategoryCode,null,experimentTypeCode);
		List<Container> containerUpdated = new ArrayList<>();
		for(Container containerUpdate:containers){
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,containerUpdate.code);
			if (container == null) {
				contextError.addErrors("container.code", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG , containerUpdate.code);
			} else if(container.state.code!=containerUpdate.state.code){
				//Update state container
				ContextValidation contextValidation= new ContextValidation(Constants.NGL_DATA_USER);
				if(containerUpdate.state.code.equals("IS")&& CollectionUtils.isNotEmpty(container.processCodes)){
				//	contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, container.code);
					logger.warn("Le container "+container.code +" ne peut pas etre mise a l etat IS car elle a des processus");
				} else {
					//ContainerWorkflows.setContainerState(container, containerUpdate.state, contextValidation);
					contextValidation.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "controllers");
					contextValidation.putObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_SUPPORT_STATE, Boolean.TRUE);
					// Spring.get BeanOfType(ContWorkflows.class).setState(contextValidation, container, containerUpdate.state);
					ctx.injector().instanceOf(ContWorkflows.class).setState(contextValidation, container, containerUpdate.state);
					contextValidation.removeObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
					contextValidation.removeObject(CommonValidationHelper.FIELD_UPDATE_CONTAINER_SUPPORT_STATE);
				}
				if (!contextValidation.hasErrors()) {
					containerUpdated.add(container);
				} else { 
					contextError.errors.putAll(contextValidation.errors);
				}
			}
			if (container.valuation==null || container.valuation.valid!=containerUpdate.valuation.valid) {
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class, DBQuery.is("code", container.code),DBUpdate.set("valuation.valid", containerUpdate.valuation.valid));
			}
			limsServices.updateMaterielmanipLims(containerUpdated, contextError);
		}
	}

}
