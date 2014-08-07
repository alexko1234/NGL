package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.authorisation.PermissionHelper;

import validation.ContextValidation;


import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;

import models.laboratory.common.instance.State;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;


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
				
				String outPutContainerCode=ContainerHelper.generateContainerCode(experiment.instrument.outContainerSupportCategoryCode);
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
	public ContextValidation saveOutputContainers(Experiment experiment) throws DAOException {

		ContextValidation contextValidation=new ContextValidation();
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

			Container inputContainer=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,inputContainerUsed.code);
			//Add content
			ContainerHelper.addContent(inputContainer, outputContainer, experiment);
			//Add localisation
			outputContainer.support=outputContainerUsed.locationOnContainerSupport;
			outputContainer.state=new State("N",experiment.traceInformation.modifyUser);
			outputContainer.valuation=new Valuation();

			//TODO volume, proportion

			support.projectCodes=new ArrayList<String>(inputContainer.projectCodes);
			support.sampleCodes=new ArrayList<String>(inputContainer.sampleCodes);

			contextValidation.setCreationMode();
			contextValidation.addKeyToRootKeyName("support["+support.code+"]");
			InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME,support, contextValidation);
			contextValidation.removeKeyFromRootKeyName("support["+support.code+"]");
			if(!contextValidation.hasErrors()){
				contextValidation.addKeyToRootKeyName("container["+outputContainer.code+"]");
				InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME,outputContainer, contextValidation);
				contextValidation.removeKeyFromRootKeyName("container["+outputContainer.code+"]");
			}


		} else {
			contextValidation.addErrors("locationOnContainerSupport.code", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG);
		}

		return contextValidation;

	}

	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);
		if(outputContainerUsed != null){
			outputContainerUsed.validate(contextValidation);
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
