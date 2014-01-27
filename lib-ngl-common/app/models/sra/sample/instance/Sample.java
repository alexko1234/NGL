package models.sra.sample.instance;

import java.util.Date;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class Sample extends DBObject implements IValidation {

	// SampleType
	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = projectCode_num
	public String projectCode;     // required pour nos stats //Reference code de la collection project NGL  
	public int taxonId;            // required 
	public String classification;     
	public String commonName;     
	public String scientificName;  // required next soon 
	public String title;           // required next soon 
	public String description;      
	public String clone;           
	public String accession;       // numeros d'accession attribué par ebi 
	public Date releaseDate;       // required, date de mise à disposition en public par l'EBI
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub	
	}

}
