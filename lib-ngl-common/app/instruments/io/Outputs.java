package instruments.io;

import static play.data.Form.form;
import instruments.io.utils.AbstractSampleSheetsfactory;
import instruments.io.utils.SampleSheetsFactoryHelper;

import java.io.File;

import models.laboratory.experiment.instance.Experiment;
import play.Logger;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Result;
import controllers.CommonController;

public class Outputs extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result sampleSheets(){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		
		Experiment exp = experimentFilledForm.get();
		
		AbstractSampleSheetsfactory sampleSheetFactory = (AbstractSampleSheetsfactory) SampleSheetsFactoryHelper.getSampleSheetsFactory("instruments.io."+exp.instrument.typeCode.toLowerCase().replace("-", "")+".api.SampleSheetsFactory", exp);
		
		Logger.info("instruments.io."+exp.instrument.typeCode.toLowerCase().replace("-", "")+".api.SampleSheetsFactory");
		
		if(sampleSheetFactory != null){
			File file = sampleSheetFactory.generate();
			response().setContentType("application/x-download");  
			response().setHeader("Content-disposition","attachment; filename="+file.getAbsolutePath());
			return ok(file);
		}
		
		return badRequest();
	}
}
