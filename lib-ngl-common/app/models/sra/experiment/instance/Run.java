package models.sra.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;

public class Run implements IValidation {
	// RunType
	public String code;            // champs alias required mais remplacé par code et valeur = projectCode_num
	public String projectCode;     // required pour nos stats                   varchar(10), 
	public Date runDate;           
	public String runCenter;       // required pour nos stats valeur fixee à GSC 
	public String accession;       // numeros d'accession attribué par ebi 
	public Date releaseDate;       // required, date de mise à disposition en public par l'EBI
	public List <RawData> listRawData = new ArrayList<RawData>();

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub	
	}

}
