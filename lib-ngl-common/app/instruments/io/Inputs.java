package instruments.io;

import static play.data.Form.form;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;
import play.Logger;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;

public class Inputs extends CommonController{

	final static Form<PropertyFileValue> fileForm = form(PropertyFileValue.class);
	
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result importFile(String experimentCode) throws Exception{
		Logger.debug("Exp. Code : "+experimentCode);		
		
		
		Form<PropertyFileValue> filledForm = getFilledForm(fileForm,PropertyFileValue.class);
		PropertyFileValue pfv = filledForm.get();
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		
		
		Logger.debug("File Name : "+pfv.fullname);		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(0);
		Logger.debug("Sheet Name : "+sheet.getSheetName());
		Logger.debug("Last Row : "+sheet.getLastRowNum());
		
		
		for(int i = 31; i <= sheet.getLastRowNum(); i=i+4){
			
			Logger.debug("Cell 0 / 10 : "+getStringValue(sheet.getRow(i).getCell(1), ctxVal)+' '+getNumericValue(sheet.getRow(i).getCell(10), ctxVal));
		}
		
		return ok();
	}

	private static String getStringValue(Cell cell, ContextValidation ctxVal){
		if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return cell.getStringCellValue();
		}else{
			return null;
		}
	}
	
	private static Double getNumericValue(Cell cell, ContextValidation ctxVal) {
		
		if(Cell.CELL_TYPE_NUMERIC == cell.getCellType() || Cell.CELL_TYPE_FORMULA == cell.getCellType()){
			return cell.getNumericCellValue();
		}else if(Cell.CELL_TYPE_STRING == cell.getCellType()){
			return new Double(cell.getStringCellValue());
		}else{
			return null;
		}
		
	}
	
}
