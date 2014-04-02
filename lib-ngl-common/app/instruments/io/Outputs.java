package instruments.io;

import static play.data.Form.form;
import instruments.io.utils.AbstractSampleSheetsfactory;
import instruments.io.utils.SampleSheetsFactoryHelper;
import models.laboratory.experiment.instance.Experiment;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import views.html.defaultpages.error;
import controllers.CommonController;

public class Outputs extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	public static Result sampleSheets(){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		
		AbstractSampleSheetsfactory sampleSheetFactory = (AbstractSampleSheetsfactory) SampleSheetsFactoryHelper.getSampleSheetsFactory("instruments.io."+exp.instrument.typeCode.toLowerCase()+".api.SampleSheetsFactory", exp);
		
		Logger.info("instruments.io."+exp.instrument.typeCode.toLowerCase()+".api.SampleSheetsFactory");
		
		if(sampleSheetFactory != null){
			return ok(sampleSheetFactory.generate());
		}
		
		return badRequest();
	}
}
