package models.sra.configuration.instance;

import java.io.File;

import validation.IValidation;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;

import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;

//Declaration d'une collection Configuration (herite de DBObject)
public class Configuration  extends DBObject implements IValidation {

		//public String alias;            // required mais remplacé par code herité de DBObject, et valeur = conf_projectCode_num
		public String projectCode = null; // de type BAT, required pour nos stats
		//public String studyCode = null;
		public String strategySample = null;  // required et constraint :
		//("STRATEGY_SAMPLE_TAXON", "STRATEGY_SAMPLE_CLONE", "STRATEGY_NO_SAMPLE");
		
		/*Informations de library obligatoires qui sont prises dans le lims mais qui peuvent etre surchargées 
		 * par le la configuration ou encore par le  fichier User_Experiments.csv si les valeurs sont specifiques 
		 de chaque experiment:
		 */
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
			VariableSRA variableSRA;
			
			// verifier que projectCode est bien renseigné :
			if ((projectCode == null) ||(projectCode.matches("^\\s*$"))) {
				contextValidation.addErrors("configuration.projectCode", " aucune valeur");
				
			}
			// verifier que strategySample est bien renseigne avec valeur autorisee
			if ( (strategySample == null) || (strategySample.matches("^\\s*$"))) {
				contextValidation.addErrors("configuration.strategySample", " aucune valeur");
			} else {
				if (! VariableSRA.mapStrategySample.containsKey(strategySample.toLowerCase())) {
					contextValidation.addErrors("configuration.strategySample n'appartient pas a la liste des valeurs autorisees :" , VariableSRA.mapStrategySample.keySet().toString());
				}
			}
			// verifier si librarySelection renseigné alors c'est bien une valeur autorisee 
			if (librarySelection != null) {					
				if (! VariableSRA.mapLibrarySelection.containsKey(librarySelection.toLowerCase())) {
					contextValidation.addErrors("configuration.librarySelection n'appartient pas a la liste des valeurs autorisees : ", VariableSRA.mapLibrarySelection.keySet().toString());
				}
			}
			// verifier si librarySource renseigné alors c'est bien une valeur autorisee 
			if (librarySource != null) {
				if (! VariableSRA.mapLibrarySource.containsKey(librarySource.toLowerCase())) {
						contextValidation.addErrors("configuration.librarySource n'appartient pas a la liste des valeurs autorisees :", VariableSRA.mapLibrarySource.keySet().toString());
				}
			}
			// verifier si libraryStrategy renseigné alors c'est bien une valeur autorisee 
			if (libraryStrategy != null) {
				if (! VariableSRA.mapLibraryStrategy.containsKey(libraryStrategy.toLowerCase())) {
						contextValidation.addErrors("configuration.libraryStrategy", this.libraryStrategy + " n'appartient pas a la liste des valeurs autorisees :" + VariableSRA.mapLibraryStrategy.keySet().toString());
				}
			}
			// Verifier que si User_Experiments est renseigné, la valeur correspond bien à un fichier present sur disque
			if (userFileExperiments != null) {
				if (! new File(userFileExperiments).isFile()){
					contextValidation.addErrors("configuration.userFileExperiments", this.userFileExperiments + " n'est pas un fichier");
				}
				if (! new File(userFileExperiments).canRead()){
					contextValidation.addErrors("configuration.userFileExperiments", this.userFileExperiments + " n'est pas un fichier lisible");
				}
			}		
			// verifier que code est bien renseigné
			if ((this.code == null) ||(this.code.matches("^\\s*$"))) {
				contextValidation.addErrors("configuration.code", " aucune valeur");
			} else {
				// Verifier si on est dans un contexte de creation d'objet, que configuration.code n'existe pas dans la database
				if(contextValidation.isCreationMode()){
					if(MongoDBDAO.checkObjectExist(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, "code", this.code)) {
						contextValidation.addErrors("configuration.code ", this.code + " existe deja dans la base de données et MODE CREATION");
					}	
				}
				// Verifier si on est dans un contexte d'UPDATE d'objet, que configuration.code existe bien dans la database
				if(contextValidation.isUpdateMode() ){
					if(! MongoDBDAO.checkObjectExist(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, "code", this.code)) {
						contextValidation.addErrors("configuration.code",this.code + " n'existe pas dans la base de données et MODE UPDATE");
					}
			}
				// Verifier si on est dans un contexte DELETE d'objet, que configuration.code existe bien dans la database
				if(contextValidation.isDeleteMode() ){
					if(! MongoDBDAO.checkObjectExist(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, "code", this.code)) {
						contextValidation.addErrors("configuration.code",this.code + " n'existe pas dans la base de données et MODE DELETE");
					}
				}
			}
		}

}
