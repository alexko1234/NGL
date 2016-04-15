package controllers.instruments.io.miseq;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import org.w3c.dom.Document;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.parameter.Index;
import play.Logger;
import play.libs.XML;
import validation.ContextValidation;
import validation.utils.ValidationHelper;
import controllers.instruments.io.utils.AbstractInput;
import controllers.instruments.io.utils.InputHelper;

public class Input extends AbstractInput {
	
   /* Description du fichier a traiter: TXT CSV généré parlabchipGX
	*Well Label,Region[200-2000] Conc. (ng/ul),Region[200-2000] Size at Maximum [BP],User Comment
	*A01,3.7401558465,551.4705882353,	
	*A02...
	*   attention les valeurs [200-2000]  sont variables ne pas les prendre en compte pour la verification
	*   d'un entete correct
	*/
	
	@Override
	public Experiment importFile(Experiment experiment,PropertyFileValue pfv, ContextValidation contextValidation) throws Exception {	
			
		
		InputStream is = new ByteArrayInputStream(pfv.value);
		Document doc = XML.fromInputStream(is, "UTF-8");
		
		Logger.debug("doc = "+doc.getElementsByTagName("table").item(0).getChildNodes().getLength());
		
		return experiment;
	}
	
	
}
