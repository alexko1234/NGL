package controllers.receptions.io;

import static play.data.Form.form;
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


public class Receptions extends TPLCommonController {

	final Form<PropertyFileValue> fileForm = form(PropertyFileValue.class);
	
	private ReceptionConfiguration getReceptionConfig(String code){
		return MongoDBDAO.findByCode(InstanceConstants.RECEPTION_CONFIG_COLL_NAME, ReceptionConfiguration.class, code);
	}
	
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@Permission(value={"writing"})
	public Result importFile(String receptionConfigCode){
		ReceptionConfiguration configuration = getReceptionConfig(receptionConfigCode);
		if(null == configuration)return badRequest("ReceptionConfiguration not exist");
		
		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
		PropertyFileValue pfv = filledForm.get();
		if(null != pfv){
			
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors());
			try{
				FileService fileService = ReceptionFileService.getFileService(configuration, pfv, contextValidation);
				fileService.analyse();
			}catch(Throwable e){
				e.printStackTrace();
				contextValidation.addErrors("Error :", e.getMessage()+"");
			}
			return badRequest(filledForm.errorsAsJson());
		}else{
			return badRequest("missing file");
		}		
	}
}
