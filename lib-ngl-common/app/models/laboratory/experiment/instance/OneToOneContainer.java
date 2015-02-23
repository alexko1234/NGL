package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.authorisation.PermissionHelper;
import validation.ContextValidation;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
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

	public ContainerUsed inputContainerUsed;
	public ContainerUsed outputContainerUsed;

	public OneToOneContainer(){
		super();
	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) throws DAOException {

		if(this.outputContainerUsed==null){
			
			if(this.inputContainerUsed!=null){
				
				String outPutContainerCode=CodeHelper.getInstance().generateContainerSupportCode();
				this.outputContainerUsed = new ContainerUsed(outPutContainerCode);

				LocationOnContainerSupport support=new LocationOnContainerSupport();
				support.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
				// Same position 
				ContainerSupportCategory containerSupportCategory=ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode);

				if(containerSupportCategory.nbColumn==1 && containerSupportCategory.nbLine==1){
					support.line="1";
					support.code = outputContainerUsed.code;
					support.column="1";
				}else {
					contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
					Logger.error("Location in support not implemented");
				}

				this.outputContainerUsed.locationOnContainerSupport=support;

			}else{
				contextValidation.addErrors("inputContainerUsed", ValidationConstants.ERROR_NOTEXISTS_MSG);
			}
			
		}else {
			contextValidation.addErrors("outputContainerUsed", ValidationConstants.ERROR_ID_NOTNULL_MSG);
		}
		return contextValidation;
	}


	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) throws DAOException {

		if(outputContainerUsed!=null && !MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class, this.outputContainerUsed.code)){
			// ContainerSupport
			ContainerSupport support=ContainerSupportHelper.createSupport(this.outputContainerUsed.locationOnContainerSupport.code, null, 
					this.outputContainerUsed.locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);

			// Container
			Container outputContainer = new Container();
			outputContainer.code=this.outputContainerUsed.code;
			outputContainer.traceInformation = new TraceInformation();
			outputContainer.traceInformation.setTraceInformation(experiment.traceInformation.modifyUser);
			outputContainer.categoryCode=this.outputContainerUsed.categoryCode;
			//Add localisation
			outputContainer.support=outputContainerUsed.locationOnContainerSupport;
			outputContainer.state=new State("N",experiment.traceInformation.modifyUser);
			outputContainer.valuation=new Valuation();

			//TODO volume, proportion
			
			Map<String,PropertyValue> properties=ExperimentHelper.getAllPropertiesFromAtomicTransfertMethod(this,experiment);
			ContainerHelper.addContent(outputContainer, this.getInputContainers(), experiment, properties);
			ContainerSupportHelper.updateData(support, this.getInputContainers(), experiment, properties);
			contextValidation.setCreationMode();
			ContainerSupportHelper.save(support, contextValidation);

			if(!contextValidation.hasErrors()){
				ContainerHelper.save(outputContainer, contextValidation);
				ProcessHelper.updateNewContainerSupportCodes(outputContainerUsed, inputContainerUsed, experiment);
			}


		} else {
			contextValidation.addErrors("locationOnContainerSupport.code", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG);
		}

		return contextValidation;

	}

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.putObject("level", Level.CODE.ContainerIn);
		inputContainerUsed.validate(contextValidation);
		contextValidation.removeObject("level");
		if(outputContainerUsed != null){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			outputContainerUsed.validate(contextValidation);
			contextValidation.removeObject("level");
		}
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}

	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(outputContainerUsed);
		return cu;
	}



}
