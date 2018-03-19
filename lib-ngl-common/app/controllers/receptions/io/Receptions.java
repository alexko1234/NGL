package controllers.receptions.io;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import javax.inject.Inject;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.utils.InstanceConstants;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Result;
import services.io.reception.FileService;
import services.io.reception.ReceptionFileService;
import validation.ContextValidation;
import controllers.TPLCommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;


// TODO: cleanup

// TODO: cleanup

public class Receptions extends TPLCommonController {

	final Form<PropertyFileValue> fileForm;// = form(PropertyFileValue.class);
	
//	private final NGLContext ctx;
	private final ReceptionFileService receptionFileService;

	@Inject
	public Receptions(NGLContext ctx, ReceptionFileService receptionFileService) {
		fileForm = ctx.form(PropertyFileValue.class);
//		this.ctx = ctx;
		this.receptionFileService = receptionFileService;
	}
	
	private ReceptionConfiguration getReceptionConfig(String code){
		return MongoDBDAO.findByCode(InstanceConstants.RECEPTION_CONFIG_COLL_NAME, ReceptionConfiguration.class, code);
	}
	
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	@Permission(value={"writing"})
	public Result importFile(String receptionConfigCode){
		ReceptionConfiguration configuration = getReceptionConfig(receptionConfigCode);
		if (configuration == null)
			return badRequest("ReceptionConfiguration not exist");
		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
		PropertyFileValue pfv = filledForm.get();
		if (pfv != null) {
//			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm);
			try {				
				FileService fileService = receptionFileService.getFileService(configuration, pfv, contextValidation);
				fileService.analyse();
			} catch(Throwable e) {
				e.printStackTrace();
				contextValidation.addErrors("Error", (e.getMessage() != null)?e.getMessage():"null");
			}
			if (contextValidation.hasErrors()) {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
			} else {
				return ok();
			}
		} else {
			return badRequest("missing file");
		}		
	}
	
}
