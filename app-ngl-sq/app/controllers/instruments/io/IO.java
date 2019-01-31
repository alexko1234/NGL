//<<<<<<< HEAD
//package controllers.instruments.io;
//
//import static play.data.Form.form;
//
//import java.lang.reflect.Constructor;
//
//import models.laboratory.common.instance.property.PropertyFileValue;
//import models.laboratory.experiment.instance.Experiment;
//import models.laboratory.instrument.description.dao.InstrumentDAO;
//import models.laboratory.instrument.instance.InstrumentUsed;
//import models.utils.DescriptionHelper;
//import models.utils.InstanceConstants;
//import play.Logger;
//import play.data.DynamicForm;
//import play.data.Form;
//import play.libs.Json;
//import play.mvc.BodyParser;
//import play.mvc.Result;
//import validation.ContextValidation;
//import validation.utils.ValidationHelper;
//import controllers.TPLCommonController;
//import controllers.authorisation.Permission;
//import controllers.instruments.io.utils.AbstractInput;
//import controllers.instruments.io.utils.AbstractOutput;
//import controllers.instruments.io.utils.File;
//import fr.cea.ig.MongoDBDAO;
//
//public class IO extends TPLCommonController {
//	
//	final Form<PropertyFileValue> fileForm = form(PropertyFileValue.class);
//	
//	private Experiment getExperiment(String code){
//		return MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
//	}
//	
//	
//	private AbstractOutput getOutputInstance(Experiment experiment, ContextValidation contextValidation){
//		
//		if(ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
//				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")){
//			String className = getClassName(experiment, "Output");
//			
//			try{
//				Class<? extends AbstractOutput> clazz = (Class<? extends AbstractOutput>) Class.forName(className);
//				Constructor<?> constructor = clazz.getConstructor();
//				AbstractOutput instance = (AbstractOutput) constructor.newInstance();
//				return instance;
//			}catch(Exception e){
//				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
//			}			
//		}
//		return null;				
//	}
//	
//	private AbstractInput getInputInstance(Experiment experiment, ContextValidation contextValidation){
//		
//		if(ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
//				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")){
//			String className = getClassName(experiment, "Input");
//			
//			try{
//				Class<? extends AbstractInput> clazz = (Class<? extends AbstractInput>) Class.forName(className);
//				Constructor<?> constructor = clazz.getConstructor();
//				AbstractInput instance = (AbstractInput) constructor.newInstance();
//				return instance;
//			}catch(Exception e){
//				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
//			}			
//		}
//		return null;				
//	}
//
//
//	private String getClassName(Experiment experiment, String type) {
//		String institute = DescriptionHelper.getInstitute().get(0).toLowerCase();
//		
//		return "controllers.instruments.io."+institute+"."+experiment.instrument.typeCode.toLowerCase().replace("-", "")+"."+type;
//	}
//	
//	public Result generateFile(String experimentCode){
//		Experiment experiment = getExperiment(experimentCode);
//		if(null == experiment)return badRequest("experiment not exist");
//		
//		// GA/FDS 22/07/2016 ajout .bindFromRequest() + context....putAll pour recuperer un parametre de la query string...
//		DynamicForm filledForm = form().bindFromRequest(); 
//        ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
//        contextValidation.getContextObjects().putAll(filledForm.data());
//
//		AbstractOutput output = getOutputInstance(experiment, contextValidation);
//		
//		if(!contextValidation.hasErrors()){
//			try{
//				File file = output.generateFile(experiment, contextValidation);
//				if (!contextValidation.hasErrors() && null != file) {									
//					response().setContentType("application/x-download");  
//					response().setHeader("Content-disposition","attachment; filename="+file.filename);
//					//si le fichier a une extension .csv ou .txt, il est en UTF-8, alors on ajoute le BOM pour que le texte soit affiché correctement dans excel
//					String extensionFileName = file.filename.substring(file.filename.lastIndexOf(".") + 1);
//					Logger.debug("generateFile : extensionFileName = " + extensionFileName);
//					if (extensionFileName.equals("txt") || extensionFileName.equals("csv")){
//						return ok('\ufeff'+file.content);
//					}
//					return ok(file.content);
//				}
//			}catch(Throwable e){
//				Logger.error("IO Error :", e);
//				contextValidation.addErrors("Error :", e.getMessage());
//			}
//		}		
//		return badRequest(filledForm.errorsAsJson());
//	}
//	
//	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
//	@Permission(value={"writing"})
//	public Result importFile(String experimentCode){
//		Experiment experiment = getExperiment(experimentCode);
//		if(null == experiment)return badRequest("experiment not exist");
//		
//		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
//		PropertyFileValue pfv = filledForm.get();
//		
//		DynamicForm dynamicfilledForm = form().bindFromRequest(); 
//        ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
//        contextValidation.getContextObjects().putAll(dynamicfilledForm.data());
//		
//		if(null != pfv){
//			AbstractInput input = getInputInstance(experiment, contextValidation);
//			
//			if(!contextValidation.hasErrors()){
//				try{
//					experiment = input.importFile(experiment, pfv,contextValidation);
//					if (!contextValidation.hasErrors()) {	
//						return ok(Json.toJson(experiment));
//					}
//				}catch(Throwable e){
//					Logger.error(e.getMessage(),e);
//					contextValidation.addErrors("Error :", e.getMessage()+"");
//				}
//			}
//			return badRequest(filledForm.errorsAsJson());
//		}else{
//			return badRequest("missing file");
//		}		
//	}
//}
//=======
package controllers.instruments.io;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import controllers.TPLCommonController;
import controllers.authorisation.Permission;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import models.utils.DescriptionHelper;
import models.utils.InstanceConstants;
//import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import validation.utils.ValidationHelper;

public class IO extends TPLCommonController {

	private static final play.Logger.ALogger logger = play.Logger.of(IO.class);
	
	private final Form<PropertyFileValue> fileForm ; //= form(PropertyFileValue.class);
	private final NGLContext context;

	@Inject
	public IO(NGLContext context) {
		this.context  = context;
		this.fileForm = context.form(PropertyFileValue.class);
	}
	
	private Experiment getExperiment(String code){
		return MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
	}
	
	private AbstractOutput getOutputInstance(Experiment experiment, ContextValidation contextValidation) {	
		if (ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")) {
			String className = getClassName(experiment, "Output");
			try {
				@SuppressWarnings("unchecked") // Uncheckable reflection 
				Class<? extends AbstractOutput> clazz = (Class<? extends AbstractOutput>) Class.forName(className);
//				Constructor<?> constructor = clazz.getConstructor();
//				AbstractOutput instance = (AbstractOutput) constructor.newInstance();
				Constructor<? extends AbstractOutput> constructor = clazz.getConstructor();
				AbstractOutput instance = constructor.newInstance();
				return instance;
			} catch(Exception e) {
				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
			}			
		}
		return null;				
	}
	
	// FDS 25/10/2017 ajout parametre optionnel extraInstrument
	private AbstractInput getInputInstance(Experiment experiment, ContextValidation contextValidation, String extraInstrument) {
		if (ValidationHelper.required(contextValidation, experiment.instrument, "instrument") 
				&& ValidationHelper.required(contextValidation, experiment.instrument.typeCode, "instrument.code")) {
			String className; 
			
			if (extraInstrument == null || extraInstrument.equals("")){
				// ancien comportement
				className = getClassName(experiment, "Input");
			} else {
				// Class Input de extraInstrument
				String institute = DescriptionHelper.getInstitute().get(0).toLowerCase();
				className ="controllers.instruments.io." + institute + "." + extraInstrument + ".Input";
			}
			try {
				@SuppressWarnings("unchecked") // Uncheckable reflection
				Class<? extends AbstractInput> clazz = (Class<? extends AbstractInput>) Class.forName(className);
//				Constructor<?> constructor = clazz.getConstructor();
//				AbstractInput instance = (AbstractInput) constructor.newInstance();
				Constructor<? extends AbstractInput> constructor = clazz.getConstructor();
				AbstractInput instance = constructor.newInstance();
				return instance;
			} catch(Exception e) {
				contextValidation.addErrors("outputClass", "io.error.instance.notexist",className);
			}	
		}
		return null;				
	}

	private String getClassName(Experiment experiment, String type) {
		String institute = DescriptionHelper.getInstitute().get(0).toLowerCase();
		return "controllers.instruments.io." + institute + "." + experiment.instrument.typeCode.toLowerCase().replace("-", "") + "." + type;
	}
	
	public Result generateFile(String experimentCode) {
		Experiment experiment = getExperiment(experimentCode);
		if (experiment == null) 
			return badRequest("experiment not exist");
		// GA/FDS 22/07/2016 ajout .bindFromRequest() + context....putAll pour recuperer un parametre de la query string...
		DynamicForm filledForm = this.context.form().bindFromRequest(); 
//        ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
        ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm);
//        contextValidation.getContextObjects().putAll(filledForm.data());
        contextValidation.getContextObjects().putAll(filledForm.rawData());
		AbstractOutput output = getOutputInstance(experiment, contextValidation);
		
		if (!contextValidation.hasErrors()) {
			try {
				File file = output.generateFile(experiment, contextValidation);
				if (!contextValidation.hasErrors() && null != file) {									
					// response().setContentType("application/x-download");  
					response().setHeader("Content-disposition","attachment; filename=" + file.filename);
					return ok(file.content).as("application/x-download");
				}
			} catch(Throwable e) {
				logger.error("IO Error :", e);
				contextValidation.addErrors("Error :", e.getMessage());
			}
		}		
		// return badRequest(filledForm.errors-AsJson());
		return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
	}
	
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	@Permission(value={"writing"})
	public Result importFile(String experimentCode, String extraInstrument){ // FDS 25/10 ajout param optionnel pour instrument additionnel  (voir apinglsq.routes??)
		Experiment experiment = getExperiment(experimentCode);
		if (experiment == null)
			return badRequest("experiment not exist");
		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
		PropertyFileValue pfv = filledForm.get();
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm);
        fillDataWith(contextValidation.getContextObjects(), request().queryString());
		if (pfv != null) {
			AbstractInput input = getInputInstance(experiment, contextValidation, extraInstrument ); // FDS 25/10 ajout param optionnel pour instrument additionnel
			if (!contextValidation.hasErrors()) {
				try {
					experiment = input.importFile(experiment, pfv,contextValidation);
					if (!contextValidation.hasErrors()) {	
						return ok(Json.toJson(experiment));
					}
				} catch(Throwable e) {
					logger.error(e.getMessage(),e);
					contextValidation.addErrors("Error :", e.getMessage()+"");
				}
			}
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
		} else {
			return badRequest("missing file");
		}		
	}
	
}
//>>>>>>> V2.0.2
