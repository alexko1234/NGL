package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.authorisation.PermissionHelper;
import validation.ContextValidation;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.ProcessHelper;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class OneToOneContainer extends AtomicTransfertMethod{

	public OneToOneContainer(){
		super();
	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) throws DAOException {

		//if(this.outputContainerUsed==null){
			
			if(this.inputContainerUseds!=null){
				if(null == this.outputContainerUseds.get(0).code){	
					this.outputContainerUseds.get(0).code=CodeHelper.getInstance().generateContainerSupportCode();
				}
				LocationOnContainerSupport support=new LocationOnContainerSupport();
				support.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
				// Same position 
				ContainerSupportCategory containerSupportCategory=ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode);

				if(containerSupportCategory.nbColumn==1 && containerSupportCategory.nbLine==1){
					support.line="1";
					support.code = outputContainerUseds.get(0).code;
					support.column="1";
				}else {
					contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
					Logger.error("Location in support not implemented");
				}

				this.outputContainerUseds.get(0).locationOnContainerSupport=support;

			}else{
				contextValidation.addErrors("inputContainerUsed", ValidationConstants.ERROR_NOTEXISTS_MSG);
			}
			
		//}else {
			//contextValidation.addErrors("outputContainerUsed", ValidationConstants.ERROR_ID_NOTNULL_MSG);
		//}
		return contextValidation;
	}


	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) throws DAOException {

		if(outputContainerUseds!=null && !MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class, this.outputContainerUseds.get(0).code)){
			// ContainerSupport
			ContainerSupport support=ContainerSupportHelper.createContainerSupport(this.outputContainerUseds.get(0).locationOnContainerSupport.code, null, 
					this.outputContainerUseds.get(0).locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);

			// Container
			Container outputContainer = new Container();
			outputContainer.code=this.outputContainerUseds.get(0).code;
			outputContainer.traceInformation = new TraceInformation();
			outputContainer.traceInformation.setTraceInformation(experiment.traceInformation.modifyUser);
			outputContainer.categoryCode=this.outputContainerUseds.get(0).categoryCode;
			//Add localisation
			outputContainer.support=outputContainerUseds.get(0).locationOnContainerSupport;
			outputContainer.state=new State("N",experiment.traceInformation.modifyUser);
			outputContainer.valuation=new Valuation();

			//TODO volume, proportion
			outputContainer.mesuredVolume=(PropertySingleValue) this.outputContainerUseds.get(0).volume;
			outputContainer.mesuredConcentration= (PropertySingleValue) this.outputContainerUseds.get(0).concentration;
			outputContainer.mesuredQuantity=(PropertySingleValue) this.outputContainerUseds.get(0).quantity;

				
			//TODO validation properties !
			
			Map<String,PropertyValue> properties=ExperimentHelper.getAllPropertiesFromAtomicTransfertMethod(this,experiment);
			ContainerHelper.addContent(outputContainer, this.getInputContainers(), experiment, properties);
			ContainerSupportHelper.updateData(support, this.getInputContainers(), experiment, properties);
			contextValidation.setCreationMode();
			ContainerSupportHelper.save(support, contextValidation);

			if(!contextValidation.hasErrors()){
				ContainerHelper.save(outputContainer, contextValidation);
				ProcessHelper.updateNewContainerSupportCodes(outputContainerUseds.get(0), inputContainerUseds, experiment);
			}


		} else {
			contextValidation.addErrors("locationOnContainerSupport.code", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG);
		}

		return contextValidation;

	}

	@Override
	public void validate(ContextValidation contextValidation) {

		contextValidation.putObject("level", Level.CODE.ContainerIn);		
		inputContainerUseds.get(0).validate(contextValidation);	
		contextValidation.removeObject("level");
		if(outputContainerUseds != null){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			outputContainerUseds.get(0).validate(contextValidation);
			contextValidation.removeObject("level");
		}
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		return inputContainerUseds;
	}

	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){		
		return outputContainerUseds;
	}



}
