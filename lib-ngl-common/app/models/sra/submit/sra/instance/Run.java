package models.sra.submit.sra.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.cea.ig.MongoDBDAO;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;

public class Run implements IValidation {
	// RunType
	public String code;            // champs alias required mais remplacé par code 
	public Date runDate;           
	public String runCenter;       // required pour nos stats valeur fixee à GSC 
	public String accession;       // numeros d'accession attribué par ebi 
	public String expCode;
	public String expAccession;
	public List <RawData> listRawData = new ArrayList<>();
	public String adminComment; // commentaire privé "reprise historique"				

	
	public void validateLight(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("run");
		// Verifier que runDate est bien renseigné :
		System.out.println("this.runDate: " + this.runDate);
		ValidationHelper.required(contextValidation, this.runDate , "runDate");
		System.out.println("this.runCenter: " + this.runCenter);

		SraValidationHelper.requiredAndConstraint(contextValidation, this.runCenter, VariableSRA.mapCenterName(), "runCenter");
		
		// verifier que code est bien renseigné
		if(StringUtils.isBlank(this.code)) {
			System.out.println("this.runCode: " + this.code);

			contextValidation.addErrors("run.code", " aucune valeur");
		} else {
			// Verifier si on est dans un contexte de creation d'objet, que run.code n'existe pas dans la database (dans collection Experiment)
			if(contextValidation.isCreationMode()){
				System.out.println("contexte creationMode: ");

				if(MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "run.code", this.code)) {
					System.out.println("En mode creation et run exitstant dans base pour " + this.code);
					contextValidation.addErrors("run.code ", this.code + " existe deja dans la base de données et MODE CREATION");
				}	
			}
			// Verifier si on est dans un contexte d'UPDATE d'objet, que run.code existe bien dans la database
			if(contextValidation.isUpdateMode() ) {
				if(! MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "run.code", this.code)) {
					contextValidation.addErrors("run.code",this.code + " n'existe pas dans la base de données et MODE UPDATE");
				}
			}
		}
		contextValidation.removeKeyFromRootKeyName("run");
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		validateLight(contextValidation);
		contextValidation.addKeyToRootKeyName("run");
		for(RawData rawData : listRawData) {
			rawData.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("run");
	}
}
