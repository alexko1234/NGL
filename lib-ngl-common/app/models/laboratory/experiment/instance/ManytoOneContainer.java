package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.CommonController;

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

import org.mongojack.DBQuery;
import models.utils.instance.ContainerSupportHelper;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;

import play.Logger;

import validation.ContextValidation;

public class ManytoOneContainer extends AtomicTransfertMethod{

	public int inputNumber;

	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	public ManytoOneContainer(){
		super();
	}
	
	@Override
	public void createOutputContainerUsed(Experiment experiment) throws DAOException {

		if(this.outputContainerUsed==null){
			if(this.inputContainerUseds!=null){
				String outPutContainerCode=null;
				//Condition à supprimer 
				if(experiment.instrumentProperties.get("mapcardRef").value==null){
					outPutContainerCode=ContainerHelper.generateContainerCode(experiment.instrument.outContainerSupportCategoryCode);
				}else{
					outPutContainerCode=experiment.instrumentProperties.get("mapcardRef").value.toString();
				}
				
				this.outputContainerUsed = new ContainerUsed(outPutContainerCode);

				LocationOnContainerSupport support=new LocationOnContainerSupport();
				support.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
			// Same position 
			ContainerSupportCategory containerSupportCategory=ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode);
			
			if(containerSupportCategory.nbColumn==1 && containerSupportCategory.nbLine==1){
				support.line="1";
				support.column="1";
				support.code=outPutContainerCode;
			}else {
				Logger.error("Location in support not implemented");
			}

			this.outputContainerUsed.locationOnContainerSupport=support;
			
			}else{
				Logger.error("InputContainerUsed is not null");
			}
		}else {
			Logger.error("OutputContainerUsed is not null");
		}

		
	}

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment) {
		ContextValidation contextValidation = new ContextValidation();

		if(outputContainerUsed.code!=null && !MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, this.outputContainerUsed.code)){
			// ContainerSupport
			ContainerSupport support=ContainerSupportHelper.createSupport(this.outputContainerUsed.locationOnContainerSupport.code, null,
					this.outputContainerUsed.locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);

			// Container
			Container outputContainer = new Container();
			outputContainer.code=this.outputContainerUsed.code;
			outputContainer.traceInformation = new TraceInformation();
			outputContainer.traceInformation.setTraceInformation(experiment.traceInformation.modifyUser);
			outputContainer.categoryCode=this.outputContainerUsed.categoryCode;
			
			List<String> containerCodes=new ArrayList<String>();
			for(ContainerUsed containerUsed:inputContainerUseds){
				containerCodes.add(containerUsed.code);
			}

			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code",containerCodes)).toList();
			//Add content
			for(Container inputContainer:inputContainers){
				Logger.debug("Add content "+inputContainer.code);
				ContainerHelper.addContent(inputContainer, outputContainer, experiment);
				support.projectCodes=InstanceHelpers.addCodesList(inputContainer.projectCodes, support.projectCodes);
				support.sampleCodes=InstanceHelpers.addCodesList(inputContainer.sampleCodes, support.sampleCodes);
			}
			//Add localisation
			outputContainer.support=outputContainerUsed.locationOnContainerSupport;
			outputContainer.state=new State("N",experiment.traceInformation.modifyUser);
			outputContainer.valuation=new Valuation();
			//TODO volume, proportion

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
			Logger.debug("OutputContainerUsed.code is null");
		}

		return contextValidation;
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		outputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}
	
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		return inputContainerUseds;
	}
	
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(outputContainerUsed);
		return cu;
	}

	

}
