package models.sra.submit.sra.instance;

import java.io.File;

import validation.IValidation;
import fr.cea.ig.DBObject;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.sra.SraValidationHelper;
import play.Logger;

//Declaration d'une collection Configuration (herite de DBObject)
public class Configuration  extends DBObject implements IValidation {

		//public String alias;            // required mais remplacé par code herité de DBObject, et valeur = conf_projectCode_num
		public String projectCode = null; // de type BAT, required pour nos stats
		//public String studyCode = null;
		public String strategySample = null;  // required et constraint :
		//("STRATEGY_SAMPLE_TAXON", "STRATEGY_SAMPLE_CLONE", "STRATEGY_NO_SAMPLE");
		public String strategyStudy = null; // required et contraint existingStudyType
		// Whole Genome Sequencing, Metagenomics, transcriptome analysis
		
		// Informations de library obligatoires qui sont prises dans le lims mais qui peuvent etre surchargées 
		// par le la configuration ou encore par le  fichier User_Experiments.csv si les valeurs sont specifiques 
		// de chaque experiment:
		 
		public String librarySelection = null;// required et constraint: champs de la proc surchargeable dans conf ou par utilisateurs */
		public String libraryStrategy = null; // required et constraint. Valeur mise par defaut à "WGS" mais surchargeable */
		public String librarySource = null;   // required et constraint. Valeur mise par defaut à "GENOMIC" mais surchargeable */
		public String userFileExperiments = null; // Nom complet du fichier donnant les informations de librairies specifiques de chaque experiment
		// si les données ne doivent pas etre prises dans le lims ou dans la configuration.
		

		public State state = new State(); // Reference sur "models.laboratory.common.instance.state" 
								 // pour gerer les differents etats de l'objet.
								 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
								 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
		public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 
			// pour loguer les dernieres modifications utilisateurs

		
		@Override
		public void validate(ContextValidation contextValidation) {
			// Ajouter l'objet au contexte de validation seulement si objet ext
			// pour ex pour validation de submission qui va avoir besoin d'ajouter des objets 
						
	    	//contextValidation.putObject("configuration", this);	   
			contextValidation.addKeyToRootKeyName("configuration");
			// verifier que projectCode est bien renseigné et existe dans lims :
			SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
			// verifier que champs contraints presents avec valeurs autorisees:
			SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
			SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryStrategy, VariableSRA.mapLibraryStrategy, "libraryStrategy");
			SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySource, VariableSRA.mapLibrarySource, "librarySource");
			SraValidationHelper.requiredAndConstraint(contextValidation, this.strategySample, VariableSRA.mapStrategySample, "strategySample");
			SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapStatus, "state.code");

			// Verifier que si User_Experiments est renseigné, la valeur correspond bien à un fichier present sur disque
			if (userFileExperiments != null) {
				if (! new File(userFileExperiments).isFile()){
					contextValidation.addErrors("userFileExperiments", this.userFileExperiments + " n'est pas un fichier");
				}
				if (! new File(userFileExperiments).canRead()){
					contextValidation.addErrors("userFileExperiments", this.userFileExperiments + " n'est pas un fichier lisible");
				}
			}		
			// verifier que code est bien renseigné
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_CONFIGURATION_COLL_NAME, contextValidation);
			SraValidationHelper.validateId(this, contextValidation);
			SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
			contextValidation.removeKeyFromRootKeyName("configuration");
		}

}
