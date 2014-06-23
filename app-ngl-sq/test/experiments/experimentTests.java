package experiments;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyImgValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.junit.Test;

import fr.cea.ig.MongoDBDAO;

import play.data.validation.ValidationError;

import utils.AbstractTests;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;

public class experimentTests extends AbstractTests{

	@Test
	public void validatePropertiesFileImgErr() {
		Experiment exp = new Experiment();
		exp.code = "TESTYANNEXP";
		exp.instrument = new InstrumentUsed();
		
		PropertyImgValue pImgValue = new  PropertyImgValue();
		byte[] data = new byte[] { (byte)0xe0, 0x4f, (byte)0xd0,
			    0x20, (byte)0xea, 0x3a, 0x69, 0x10, (byte)0xa2, (byte)0xd8, 0x08, 0x00, 0x2b,
			    0x30, 0x30, (byte)0x9d };
		pImgValue.value = data;
		pImgValue.fullname = "phylogeneticTree2.jpg";
		pImgValue.extension = "jpg";
		pImgValue.width = 250;
		pImgValue.height = 250;
		
		ContextValidation cv = new ContextValidation(); 
		cv.putObject("stateCode", "IP");
		
		PropertyDefinition pDef = getPropertyImgDefinition();
		
		Map<String, PropertyDefinition> hm= new HashMap<String, PropertyDefinition>();
		hm.put("restrictionEnzyme", pDef);
		
		cv.putObject("propertyDefinitions", hm.values());
		
		pImgValue.validate(cv);
		
		
		exp.instrumentProperties = new HashMap<String, PropertyValue>();
		exp.instrumentProperties.put("restrictionEnzyme", pImgValue);
		
		showErrors(cv);
		
		MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		
		System.out.println("save");
		
		Experiment expBase = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, "TESTYANNEXP");
		
		assertThat(expBase.instrumentProperties.get("restrictionEnzyme").value);
		
		ExperimentValidationHelper.validateInstrumentUsed(exp.instrument,exp.instrumentProperties,cv);
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("code", exp.code),
				DBUpdate.set("instrumentProperties",exp.instrumentProperties));
		
		System.out.println("update");
		
		Experiment expBase2 = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, "TESTYANNEXP");
		
		assertThat(expBase2.instrumentProperties.get("restrictionEnzyme").value);
		
		MongoDBDAO.deleteByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, "TESTYANNEXP");
	}
	
	public PropertyDefinition getPropertyImgDefinition() {
		PropertyDefinition pDef = new PropertyDefinition();
		pDef.code = "restrictionEnzyme";
		pDef.name = "restrictionEnzyme";		
		pDef.active = true;
		pDef.required = true;
		pDef.valueType = "File";
		//pDef.propertyType = "Img";
		return pDef;
	}
	
	private void showErrors(ContextValidation cv) {
		if(cv.errors.size() > 0){
			for(Entry<String, List<ValidationError>> e : cv.errors.entrySet()){
				System.out.println(e);
			}
		}
	}
	
}
