package controllers.migration.models.experiment;

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
import models.laboratory.experiment.instance.ContainerUsed;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ContainerSupportHelper;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.ProcessHelper;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.migration.models.ExperimentOld;
import fr.cea.ig.MongoDBDAO;

public class ManytoOneContainerOld extends AtomicTransfertMethodOld{

	public String line;
	public String column;

	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	public ManytoOneContainerOld(){
		super();
	}

	@Override
	public ContextValidation createOutputContainerUsed(ExperimentOld experiment,ContextValidation contextValidation) throws DAOException {


		return contextValidation; 
	}

	@Override
	public ContextValidation saveOutputContainers(ExperimentOld experiment, ContextValidation contextValidation) throws DAOException {
		return contextValidation;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		if(outputContainerUsed!=null){
			contextValidation.putObject("level", Level.CODE.ContainerOut);
			contextValidation.addKeyToRootKeyName("outputContainerUsed");
			outputContainerUsed.validate(contextValidation);
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
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(outputContainerUsed);
		return cu;
	}



}
