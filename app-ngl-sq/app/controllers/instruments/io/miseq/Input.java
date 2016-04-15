package controllers.instruments.io.miseq;

import java.io.ByteArrayInputStream;
import java.io.InputStream;





import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.experiment.instance.Experiment;

import org.w3c.dom.Document;

import au.com.bytecode.opencsv.CSVReader;
import play.Logger;
import play.libs.XML;
import play.libs.XPath;
import validation.ContextValidation;
import controllers.instruments.io.utils.AbstractInput;

public class Input extends AbstractInput {
	
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		
		List<String[]> all = reader.readAll();
		
		all.forEach(array -> {
			Logger.debug(Arrays.asList(array).toString());
		});
		
		reader.close();
		return experiment;
	}
	
	
}
