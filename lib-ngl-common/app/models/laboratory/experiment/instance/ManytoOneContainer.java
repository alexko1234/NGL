package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;
import models.utils.instance.ContainerUsedHelper;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.ProcessHelper;

import org.apache.commons.collections.CollectionUtils;
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


	public ManytoOneContainer(){
		super();
	}

	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) throws DAOException {

		if(this.inputContainerUseds!=null){

			ContainerSupportCategory containerSupportCategory=ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode);

			//Code outPutContainer
			String outPutContainerCode=null;
			LocationOnContainerSupport support=new LocationOnContainerSupport();

			if(experiment.instrumentProperties.get("containerSupportCode")==null){
				outPutContainerCode=CodeHelper.getInstance().generateContainerSupportCode();
				support.code=outPutContainerCode;
				if(this.line==null){ support.line="1";} else { support.line=this.line;}
				if(this.column==null){ support.column="1";} else { support.column=this.column;}
			}else{
				if(experiment.instrumentProperties.get("containerSupportCode").value!=null){

					support.code=experiment.instrumentProperties.get("containerSupportCode").value.toString();
					if(containerSupportCategory.nbColumn==1 && containerSupportCategory.nbLine==1)
					{
						outPutContainerCode=experiment.instrumentProperties.get("containerSupportCode").value.toString();//TODO: voir Maud pour la flowcell 1
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

			PropertyValue volume = new PropertySingleValue();
			PropertyValue concentration = new PropertySingleValue();
			
			if(this.outputContainerUseds!=null){
				this.outputContainerUseds.get(0).code=outPutContainerCode;				
			}else {
				this.outputContainerUseds = new ArrayList<ContainerUsed>();
				this.outputContainerUseds.add(new ContainerUsed(outPutContainerCode));
			}
			
			support.categoryCode=experiment.instrument.outContainerSupportCategoryCode;
			this.outputContainerUseds.get(0).locationOnContainerSupport=support;
			this.outputContainerUseds.get(0).validate(contextValidation);

		}else{
			contextValidation.addErrors("inputContainerUsed", ValidationConstants.ERROR_NOTEXISTS_MSG);
		}

		return contextValidation; 
	}

	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) throws DAOException {
		
		if(this.inputContainerUseds.size()!=0){

			if(outputContainerUseds.get(0).code!=null && !MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, this.outputContainerUseds.get(0).code)){
				// Output ContainerSupport
				ContainerSupport support =MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,ContainerSupport.class, this.outputContainerUseds.get(0).locationOnContainerSupport.code);
				if(support==null){
					support=ContainerSupportHelper.createContainerSupport(this.outputContainerUseds.get(0).locationOnContainerSupport.code, null,
							this.outputContainerUseds.get(0).locationOnContainerSupport.categoryCode , experiment.traceInformation.modifyUser);
				}


				// Output Container
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


				//Add contents to container and data projets, sample ... in containersupport
				Map<String,PropertyValue> properties=ExperimentHelper.getAllPropertiesFromAtomicTransfertMethod(this,experiment);
				ContainerHelper.addContent(outputContainer, this.getInputContainers(), experiment, properties);
				ContainerSupportHelper.updateData(support, this.getInputContainers(), experiment, properties);
				ContainerSupportHelper.save(support,contextValidation);
				
				if(!contextValidation.hasErrors()){
					ContainerHelper.save(outputContainer,contextValidation);
					ProcessHelper.updateNewContainerSupportCodes(outputContainerUseds.get(0),inputContainerUseds,experiment);
				}

			} else {
				Logger.debug("OutputContainerUsed.code is null");
			}
		}
		return contextValidation;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		if(CollectionUtils.isNotEmpty(outputContainerUseds)){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			contextValidation.addKeyToRootKeyName("outputContainerUsed");
			outputContainerUseds.get(0).validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("outputContainerUsed");
			contextValidation.removeObject("level");
		}
		
		contextValidation.addKeyToRootKeyName("inputContainerUseds");
		for(ContainerUsed containerUsed:inputContainerUseds){
			contextValidation.putObject("level", Level.CODE.ContainerIn);
			containerUsed.validate(contextValidation);
			contextValidation.removeObject("level");
		}
		contextValidation.removeKeyFromRootKeyName("inputContainerUseds");
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
