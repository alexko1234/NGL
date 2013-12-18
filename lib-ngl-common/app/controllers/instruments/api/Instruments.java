package controllers.instruments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class Instruments extends CommonController{
	final static Form<InstrumentsSearchForm> processTypeForm = form(InstrumentsSearchForm.class);

	public static Result list() throws DAOException{
		Form<InstrumentsSearchForm> processTypeFilledForm = filledFormQueryString(processTypeForm,InstrumentsSearchForm.class);
		InstrumentsSearchForm instrumentsSearch = processTypeFilledForm.get();

		List<Instrument> instruments;

		try{		
			if(instrumentsSearch.instrumentUsedTypeCode != null){
				instruments =  Instrument.find.findByInstrumentUsedTypeCode(instrumentsSearch.instrumentUsedTypeCode);
			}else{
				instruments = Instrument.find.findAll();
			}
			if(instrumentsSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<Instrument>(instruments, instruments.size()))); 
			}else if(instrumentsSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(Instrument et:instruments){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(instruments));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
