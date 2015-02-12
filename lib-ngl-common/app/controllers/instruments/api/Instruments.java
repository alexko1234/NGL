package controllers.instruments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class Instruments extends CommonController{
	final static Form<InstrumentsSearchForm> instrumentSearchForm = form(InstrumentsSearchForm.class);

	final static Form<Instrument> instrumentForm = form(Instrument.class);

	
	public static Result list() throws DAOException{
		Form<InstrumentsSearchForm> instrumentTypeFilledForm = filledFormQueryString(instrumentSearchForm,InstrumentsSearchForm.class);
		InstrumentsSearchForm instrumentsQueryParams = instrumentTypeFilledForm.get();

		List<Instrument> instruments = new ArrayList<Instrument>();

		try{		
			if(instrumentsQueryParams.getInstrumentsQueryParams().isAtLeastOneParam()){
				instruments = Instrument.find.findByQueryParams(instrumentsQueryParams.getInstrumentsQueryParams());
			}else{
				if(instrumentsQueryParams.experimentType!=null || instrumentsQueryParams.experimentTypes!=null){
				instruments = Instrument.find.findAll();
				}
				return notFound();
			}
			if(instrumentsQueryParams.datatable){
				return ok(Json.toJson(new DatatableResponse<Instrument>(instruments, instruments.size()))); 
			}else if(instrumentsQueryParams.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(Instrument et:instruments){
					lop.add(new ListObject(et.code, et.name));
				}
				return ok(Json.toJson(lop));
			}else{
				return ok(Json.toJson(instruments));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
	public static Result get(String code) throws DAOException{
		Instrument instrument =  Instrument.find.findByCode(code);
		if (instrument != null) {
			return ok(Json.toJson(instrument));
		}else{
			return notFound();
		}
		
	}
	
	public static Result update(String code) throws DAOException{
		Instrument instrument =  Instrument.find.findByCode(code);
		if (instrument == null) {
			return badRequest("Instrument with code "+code+" not exist");
		}
		Form<Instrument> filledForm = getFilledForm(instrumentForm, Instrument.class);
		Instrument instrumentInput = filledForm.get();
		
		if (code.equals(instrumentInput.code)) {
				instrumentInput.update();
				return ok(Json.toJson(instrumentInput));
			
		}else{
			return badRequest("instrument code are not the same");
		}		
	}
}
