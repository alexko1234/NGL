package controllers.configurations.api;

import static play.data.Form.form;



import models.sra.submit.sra.instance.*;
import models.sra.submit.util.SraCodeHelper;
import models.utils.InstanceConstants;
import play.data.Form;
import play.mvc.Result;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import validation.ContextValidation;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.State;

public class Configurations extends DocumentController<Configuration>{

	final static Form<Configuration> configurationForm = form(Configuration.class);
	
	public Configurations() {
		super(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class);
	}

	
	public Result save() {
		Form<Configuration> filledForm = getFilledForm(configurationForm, Configuration.class);
		Configuration userConfiguration = filledForm.get();
		
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			contextValidation.setCreationMode();	
			if (userConfiguration._id == null) {
				userConfiguration.traceInformation = new TraceInformation(); 
				userConfiguration.traceInformation.setTraceInformation(getCurrentUser());
				userConfiguration.state = new State("userValidate", getCurrentUser());
				userConfiguration.code = SraCodeHelper.getInstance().generateConfigurationCode(userConfiguration.projectCode);
				userConfiguration.validate(contextValidation);
				
				if(contextValidation.errors.size()==0) {
					MongoDBDAO.save(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, userConfiguration);
				} else {
					return badRequest("configuration no valid : " + contextValidation.errors);
				}
			} else {
				return badRequest("configuration with id "+userConfiguration._id +" already exist");
			}
		
		return ok("Successful save configuration : " + userConfiguration.code);
	}
	
}
