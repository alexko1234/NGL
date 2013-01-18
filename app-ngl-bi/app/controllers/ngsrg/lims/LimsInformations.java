package controllers.ngsrg.lims;

import java.util.List;

import lims.cng.services.LimsRunServices;
import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;
import play.data.Form;
import play.libs.Json;
import play.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Extract Information from the LIMS Sequencing
 * @author galbini
 *
 */
public class LimsInformations  extends Controller {
	
	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	/**
	 * Return the list of sequencers
	 * @return
	 */
	public static Result instruments() {
		LimsRunServices  limsRunServices = Spring.getBeanOfType(LimsRunServices.class);  
		List<Instrument> intruments = limsRunServices.getInstruments();		
		return ok(Json.toJson(intruments));
	}
	
	/**
	 * Return the experiment information about the run that will be transfer
	 * @param type
	 * @param barcode
	 * @return
	 */
	public static Result experiments() {
		LimsRunServices  limsRunServices = Spring.getBeanOfType(LimsRunServices.class);
		Form<Experiment> inputExpForm = experimentForm.bindFromRequest();
		if(inputExpForm.hasErrors()) {			
				return badRequest(inputExpForm.errorsAsJson());					
		} else {
				Experiment exp = limsRunServices.getExperiments(inputExpForm.get());
				if(null == exp){
					return notFound();
				}
				//System.out.println("SIZE = "+type+" "+barcode);
				return ok(Json.toJson(exp));
		}
	  }
	
	/**
	 * Return the container support information used in the experiment
	 * @param barcode
	 * @return
	 */
	public static Result containerSupport(String barcode) {
		LimsRunServices  limsRunServices = Spring.getBeanOfType(LimsRunServices.class);  		
		ContainerSupport containerSupport = limsRunServices.getContainerSupport(barcode);
		if(null != containerSupport){
			return ok(Json.toJson(containerSupport));
		}else{
			return notFound();
		}
	  }

}
