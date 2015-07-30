package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.CodeHelper;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;
import validation.utils.ValidationConstants;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	
	public OneToManyContainer(){
		super();
	}
	
	public void createOutputContainerUsedHelper(Experiment experiment,ContextValidation contextValidation) throws DAOException {
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

		}
	
	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) throws DAOException {
		//Logger.error("Not implemented");
		if(this.inputContainerUseds!=null){
			int outputContainerNumber = 1;
			if(this.inputContainerUseds.get(0).experimentProperties != null && this.inputContainerUseds.get(0).experimentProperties.get("outputNumber") != null){
				outputContainerNumber = (int)this.inputContainerUseds.get(0).experimentProperties.get("outputNumber").value;
			}
			
			for(int i=0;i<outputContainerNumber;i++){
				createOutputContainerUsedHelper(experiment, contextValidation);
			}
		}else{
			contextValidation.addErrors("inputContainerUsed", ValidationConstants.ERROR_NOTEXISTS_MSG);
		}
		
		return contextValidation;
	}
	
	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) {
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		//experiment.outputContainerCodes = experiment.getOutputContainerCodes();
		//ProcessHelper.updateNewContainerSupportCodes
		Logger.error("Not implemented");
		return contextValidation;
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		if(CollectionUtils.isNotEmpty(outputContainerUseds)){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			contextValidation.addKeyToRootKeyName("outputContainerUsed");
			for(ContainerUsed containerUsed:outputContainerUseds){
				containerUsed.validate(contextValidation);
			}
			contextValidation.removeKeyFromRootKeyName("outputContainerUsed");
			contextValidation.removeObject("level");
		}
		
		contextValidation.addKeyToRootKeyName("inputContainerUseds");
		contextValidation.putObject("level", Level.CODE.ContainerIn);
		inputContainerUseds.get(0).validate(contextValidation);
		contextValidation.removeObject("level");
		contextValidation.removeKeyFromRootKeyName("inputContainerUseds");
		
		AtomicTransfertMethodValidationHelper.validateOneInputContainer(inputContainerUseds, contextValidation);
		
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
