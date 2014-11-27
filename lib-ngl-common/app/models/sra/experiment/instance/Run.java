package models.sra.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class Run implements IValidation {
	// RunType
	public String code;            // champs alias required mais remplacé par code et valeur = projectCode_num
	public Date runDate;           
	public String runCenter;       // required pour nos stats valeur fixee à GSC 
	public String accession;       // numeros d'accession attribué par ebi 
	public List <RawData> listRawData = new ArrayList<RawData>();

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("run::");
		// Verifier que runDate est bien renseigné :
		ValidationHelper.required(contextValidation, this.runDate , "runDate");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.runCenter, VariableSRA.mapCenterName, "runCenter");
		for(RawData rawData : listRawData) {
			rawData.validate(contextValidation);
		}
		// verifier que code est bien renseigné
		if ((this.code == null) ||(this.code.matches("^\\s*$"))) {
			contextValidation.addErrors("run.code", " aucune valeur");
		} else {
			// Verifier si on est dans un contexte de creation d'objet, que run.code n'existe pas dans la database (dans collection Experiment)
			if(contextValidation.isCreationMode()){
				if(MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "runCode", this.code)) {
					contextValidation.addErrors("run.code ", this.code + " existe deja dans la base de données et MODE CREATION");
				}	
			}
			// Verifier si on est dans un contexte d'UPDATE d'objet, que run.code existe bien dans la database
			if(contextValidation.isUpdateMode() ) {
				if(! MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "runCode", this.code)) {
					contextValidation.addErrors("run.code",this.code + " n'existe pas dans la base de données et MODE UPDATE");
				}
			}
		}
		contextValidation.removeKeyFromRootKeyName("run::");
	}
}
