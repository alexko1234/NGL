package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class OneToOneContainer extends AtomicTransfertMethod{

	public ContainerUsed inputContainerUsed;
	public ContainerUsed outputContainerUsed;

	public OneToOneContainer(){
		super();
	}

	@Override
	public void createOutputContainerUsed(Experiment experiment) throws DAOException {

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

		if(outputContainerUsed.code!=null){
			// ContainerSupport
			ContainerSupport support=ContainerSupportHelper.createSupport(this.outputContainerUsed.locationOnContainerSupport.code
					,this.outputContainerUsed.locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);

			// Container
			Container outputContainer = new Container();
			//outputContainer.categoryCode
			outputContainer.traceInformation = new TraceInformation();
			outputContainer.traceInformation.setTraceInformation(experiment.traceInformation.modifyUser);

			Container inputContainer=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,inputContainerUsed.code);
			//Add content
			ContainerHelper.addContent(inputContainer, outputContainer, experiment);
			//Add localisation
			outputContainer.support=outputContainerUsed.locationOnContainerSupport;

			//TODO volume, proportion


			support.projectCodes=new ArrayList<String>(inputContainer.projectCodes);
			support.sampleCodes=new ArrayList<String>(inputContainer.sampleCodes);

			InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME,support, contextValidation);
			if(!contextValidation.hasErrors()){
				InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME,outputContainer, contextValidation);
			}


		} else {
			Logger.debug("OutputContainerUsed.code is null");
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
