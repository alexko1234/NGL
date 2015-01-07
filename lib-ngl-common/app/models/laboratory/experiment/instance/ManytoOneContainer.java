package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;
import models.utils.instance.ContainerUsedHelper;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.ProcessHelper;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.MongoDBDAO;

public class ManytoOneContainer extends AtomicTransfertMethod{

	public String line;
	public String column;

	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	public ManytoOneContainer(){
		super();
	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) throws DAOException {

		//	if(this.outputContainerUsed!=null){

		if(this.inputContainerUseds!=null){

			ContainerSupportCategory containerSupportCategory=ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode);

			//Code outPutContainer
			String outPutContainerCode=null;
			LocationOnContainerSupport support=new LocationOnContainerSupport();

			if(experiment.instrumentProperties.get("containerSupportCode")==null){
				outPutContainerCode=ContainerHelper.generateContainerCode(experiment.instrument.outContainerSupportCategoryCode);
				support.code=outPutContainerCode;
			}else{
				if(experiment.instrumentProperties.get("containerSupportCode").value!=null){

					support.code=experiment.instrumentProperties.get("containerSupportCode").value.toString();
					if(containerSupportCategory.nbColumn==1 && containerSupportCategory.nbLine==1)
					{
						outPutContainerCode=experiment.instrumentProperties.get("containerSupportCode").value.toString();
						support.line="1";
						support.column="1";
					} else {
						outPutContainerCode=experiment.instrumentProperties.get("containerSupportCode").value.toString()+'_'+this.line;
						if(StringUtils.isNotEmpty(this.line) && StringUtils.isNotEmpty(this.column)) {
							support.line=this.line;
							support.column=this.column;
						}else {
							contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
						}
					}
				}else {
					contextValidation.addErrors("containerSupportCode",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG);
				}
			}

			if(this.outputContainerUsed!=null){
				this.outputContainerUsed.code=outPutContainerCode;
			}else {
				this.outputContainerUsed = new ContainerUsed(outPutContainerCode);
			}

			support.categoryCode=experiment.instrument.outContainerSupportCategoryCode;

			this.outputContainerUsed.locationOnContainerSupport=support;

			this.outputContainerUsed.validate(contextValidation);

		}else{
			contextValidation.addErrors("inputContainerUsed", ValidationConstants.ERROR_NOTEXISTS_MSG);
		}

		return contextValidation; 
	}

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) throws DAOException {
		
		if(this.inputContainerUseds.size()!=0){

			if(outputContainerUsed.code!=null && !MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, this.outputContainerUsed.code)){
				// Output ContainerSupport
				ContainerSupport support =MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME,ContainerSupport.class, this.outputContainerUsed.locationOnContainerSupport.code);
				if(support==null){
					support=ContainerSupportHelper.createSupport(this.outputContainerUsed.locationOnContainerSupport.code, null,
							this.outputContainerUsed.locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);
				}


				// Output Container
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


				//Add contents to container and data projets, sample ... in containersupport
				Map<String,PropertyValue> properties=ExperimentHelper.getAllPropertiesFromAtomicTransfertMethod(this,experiment);
				ContainerHelper.addContent(outputContainer, this.getInputContainers(), experiment, properties);
				ContainerSupportHelper.updateData(support, this.getInputContainers(), experiment, properties);
				ContainerSupportHelper.save(support,contextValidation);
				
				if(!contextValidation.hasErrors()){
					ContainerHelper.save(outputContainer,contextValidation);
					ProcessHelper.updateNewContainerSupportCodes(outputContainerUsed,inputContainerUseds,experiment);
				}

			} else {
				Logger.debug("OutputContainerUsed.code is null");
			}
		}
		return contextValidation;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		if(outputContainerUsed!=null)
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
