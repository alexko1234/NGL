package services.io.reception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import fr.cea.ig.play.NGLContext;
import play.Logger;
import services.io.ExcelHelper;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.reception.instance.ReceptionConfiguration;
import validation.ContextValidation;

public class ExcelFileService extends FileService {
	
	public ExcelFileService(ReceptionConfiguration configuration,
			PropertyFileValue fileValue, ContextValidation contextValidation, NGLContext ctx) {
		super(configuration, fileValue, contextValidation, ctx);		
	}

	@Override
	public void analyse() {
		try{
			//compute header label and column position
			Sheet sheet = getSheet(0);
			this.headerByIndex = convertRow(sheet.getRow(0));
			updateHeaderConfiguration();
			if(null == headerByIndex){
				contextValidation.addErrors("Headers", "not found");
			}else{
				Iterator<Row> iti = sheet.rowIterator();
				iti.next();
				while(iti.hasNext()){
					Row row = iti.next();
					Map<Integer, String> rowMap = convertRow(row);
					contextValidation.addKeyToRootKeyName("line "+(row.getRowNum()+1));
					if(null != rowMap){
						//Logger.debug(row.getRowNum()+" : rowMap "+rowMap);
						treatLine(rowMap);
					}
					contextValidation.removeKeyFromRootKeyName("line "+(row.getRowNum()+1));
				}
				if(!contextValidation.hasErrors()){
					consolidateObjects();
					saveObjects();
				}				
			}
		}catch(Throwable e){
			Logger.error("Error import file "+e.getMessage(),e);
			contextValidation.addErrors("Exception contact your administrator", e.getMessage());
		}
	}

	

	/**
	 * return map for row with at least one cell not empty
	 * @param row
	 * @return
	 */
	private Map<Integer, String> convertRow(Row row) {
		Iterator<Cell> iti = row.cellIterator();
		Map<Integer, String> rowMap = new TreeMap<Integer, String>();
		boolean isBlankLine = true;
		while(iti.hasNext()){
			Cell cell = iti.next();
			int columnIndex = cell.getColumnIndex();
			String value = ExcelHelper.convertToStringValue(cell);
			
			if(StringUtils.isNotBlank(value)){
				value = value.replaceAll("\u00A0"," ").trim();
				isBlankLine = false;
				rowMap.put(columnIndex, value);
			}
						
		}
		if(!isBlankLine){
			return rowMap;
		}else{
			return null;
		}
	}

	private Sheet getSheet(Integer sheetNumber) throws IOException, InvalidFormatException {
//		InputStream is = new ByteArrayInputStream(fileValue.value);
		InputStream is = new ByteArrayInputStream(fileValue.byteValue());
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(sheetNumber);
		return sheet;
	}
	
	
}
