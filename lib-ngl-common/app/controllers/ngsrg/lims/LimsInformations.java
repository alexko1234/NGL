package controllers.ngsrg.lims;

import static play.data.Form.form;

import java.util.List;

import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;
import lims.services.ILimsRunServices;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.CommonController;

/**
 * Extract Information from the LIMS Sequencing
 * @author galbini
 *
 */
public class LimsInformations  extends CommonController {
	
	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	/**
	 * Return the list of sequencers
	 * @return
	 */
	//@Permission(value={"read_generation"})
	public static Result instruments() {
		ILimsRunServices  limsRunServices = Spring.getBeanOfType(ILimsRunServices.class);  
		List<Instrument> intruments = limsRunServices.getInstruments();		
		return ok(Json.toJson(intruments));
	}
	
	/**
	 * Return the experiment information about the run that will be transfer
	 * @param type
	 * @param code
	 * @return
	 */
	//@Permission(value={"read_generation"})
	public static Result experiments() {
		ILimsRunServices  limsRunServices = Spring.getBeanOfType(ILimsRunServices.class);
		Form<Experiment> inputExpForm = experimentForm.bindFromRequest();
		if(inputExpForm.hasErrors()) {			
				return badRequest(inputExpForm.errorsAsJson());					
		} else {
				Experiment exp = limsRunServices.getExperiments(inputExpForm.get());
				if(null == exp){
					return notFound();
				}
				//System.out.println("SIZE = "+type+" "+supportCode);
				return ok(Json.toJson(exp));
		}
	  }
	
	/**
	 * Return the container support information used in the experiment
	 * @param supportCode
	 * @return
	 */
	//@Permission(value={"read_generation"})
	public static Result containerSupport(String supportCode) {
		ILimsRunServices  limsRunServices = Spring.getBeanOfType(ILimsRunServices.class);  		
		ContainerSupport containerSupport = limsRunServices.getContainerSupport(supportCode);
		if(null != containerSupport){
			return ok(Json.toJson(containerSupport));
		}else{
			return notFound();
		}
	  }

	/**
	 * Return the container support information used in the experiment
	 * @param supportCode
	 * @return
	 */
	//@Permission(value={"read_generation"})
	public static Result isContainerSupport(String supportCode) {
		ILimsRunServices  limsRunServices = Spring.getBeanOfType(ILimsRunServices.class);  		
		ContainerSupport containerSupport = limsRunServices.getContainerSupport(supportCode);
		if(null != containerSupport){
			return ok();
		}else{
			return notFound();
		}
	  }
	
}
