package controllers.instruments.io;

import static play.data.Form.form;

import java.lang.reflect.Constructor;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.TPLCommonController;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import fr.cea.ig.MongoDBDAO;

public class IO extends TPLCommonController {
	
	final static Form<PropertyFileValue> fileForm = form(PropertyFileValue.class);
	
	private Experiment getExperiment(String code){
		return MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
	}
	
	
	private AbstractOutput getOutputInstance(Experiment experiment, ContextValidation contextValidation){
		
		if(ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")){
			String className = getClassName(experiment, "Output");
			
			try{
				Class<? extends AbstractOutput> clazz = (Class<? extends AbstractOutput>) Class.forName(className);
				Constructor<?> constructor = clazz.getConstructor();
				AbstractOutput instance = (AbstractOutput) constructor.newInstance();
				return instance;
			}catch(Exception e){
				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
			}			
		}
		return null;				
	}
	
	private AbstractInput getInputInstance(Experiment experiment, ContextValidation contextValidation){
		
		if(ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")){
			String className = getClassName(experiment, "Input");
			
			try{
				Class<? extends AbstractInput> clazz = (Class<? extends AbstractInput>) Class.forName(className);
				Constructor<?> constructor = clazz.getConstructor();
				AbstractInput instance = (AbstractInput) constructor.newInstance();
				return instance;
			}catch(Exception e){
				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
			}			
		}
		return null;				
	}


	private String getClassName(Experiment experiment, String type) {
		return "controllers.instruments.io."+experiment.instrument.typeCode.toLowerCase().replace("-", "")+"."+type;
	}
	
	public Result generateFile(String experimentCode){
		Experiment experiment = getExperiment(experimentCode);
		if(null == experiment)return badRequest("experiment not exist");
		
		DynamicForm filledForm = form();
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
		AbstractOutput output = getOutputInstance(experiment, contextValidation);
		if(!contextValidation.hasErrors()){
			try{
				File file = output.generateFile(experiment, contextValidation);
				if (!contextValidation.hasErrors() && null != file) {									
					response().setContentType("application/x-download");  
					response().setHeader("Content-disposition","attachment; filename="+file.filename);
					return ok(file.content);
				}
			}catch(Throwable e){
				contextValidation.addErrors("Error :", e.getMessage());
			}
		}		
		return badRequest(filledForm.errorsAsJson());
	}
	
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public Result importFile(String experimentCode){
		Experiment experiment = getExperiment(experimentCode);
		if(null == experiment)return badRequest("experiment not exist");
		
		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
		PropertyFileValue pfv = filledForm.get();
		if(null != pfv){
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			AbstractInput input = getInputInstance(experiment, contextValidation);
			if(!contextValidation.hasErrors()){
				try{
					experiment = input.importFile(experiment, pfv,contextValidation);
					if (!contextValidation.hasErrors()) {	
						return ok(Json.toJson(experiment));
					}
				}catch(Throwable e){
					contextValidation.addErrors("Error :", e.getMessage()+"");
				}
			}
			return badRequest(filledForm.errorsAsJson());
		}else{
			return badRequest("missing file");
		}		
	}
}
