package controllers.instruments.io.stratageneqpcr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;

public class Input extends AbstractInput {

	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv,
			ContextValidation contextValidation) throws Exception {		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(0);
		Map<String,Double> results = new HashMap<String,Double>(0);
		for(int i = 31; i <= sheet.getLastRowNum(); i=i+4){
			String sampleBarcode = getStringValue(sheet.getRow(i).getCell(1));
			Double concentration = getNumericValue(sheet.getRow(i).getCell(10));
			
			if(ValidationHelper.required(contextValidation, sampleBarcode, "nom échantillon : ligne ="+i)
					&& ValidationHelper.required(contextValidation, concentration, "Moy. concentration : ligne ="+i)){
				results.put(sampleBarcode.replaceAll("_\\d$",""), concentration);
			}			
		}
		//validation
		if(!contextValidation.hasErrors()){
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					if(!results.containsKey(icu.code)){
						contextValidation.addErrors("Erreurs fichier", "io.error.resultat.notexist","La Moy. concentration pour "+icu.code);
					}
				});
		}
		//update.
		if(!contextValidation.hasErrors()){
			experiment.atomicTransfertMethods
				.stream()
				.map(atm -> atm.inputContainerUseds.get(0))
				.forEach(icu -> {
					PropertySingleValue concentration = null;
					if(!icu.experimentProperties.containsKey("concentration1")){
						concentration = new PropertySingleValue();
						icu.experimentProperties.put("concentration1", concentration);
					}else{
						concentration = (PropertySingleValue)icu.experimentProperties.get("concentration1");
					}
					concentration.value = results.get(icu.code);
					concentration.unit = "nM";
				});
		}
		
		return experiment;
	}
	
	

}
