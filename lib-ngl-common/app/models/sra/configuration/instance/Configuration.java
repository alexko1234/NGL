package models.sra.configuration.instance;

import validation.IValidation;
import fr.cea.ig.DBObject;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;

//Declaration d'une collection Configuration (herite de DBObject)
public class Configuration  extends DBObject implements IValidation {

		//public String alias;            // required mais remplacé par code herité de DBObject, et valeur = projectCode_num
		public String projectCode = null; // required pour nos stats
		public String studyCode = null;

		/*Informations de library obligatoires mais qui peuvent apparaitres 
		 dans le fichier User_Experiments.csv si les valeurs sont specifiques 
		 de chaque experiment:
		 */
		public String librarySelection = null;// required et constraint: champs de la proc surchargeable dans conf ou par utilisateurs */
		public String libraryStrategy = null; // required et constraint. Valeur mise par defaut à "WGS" mais surchargeable */
		public String librarySource = null;   // required et constraint. Valeur mise par defaut à "GENOMIC" mais surchargeable */
		public String strategySample = null;  // required et constraint. 
		public String User_Experiments = null; // Nom complet du fichier donnant les informations de librairies specifiques de chaque experiment
		// si les données ne doivent pas etre prises dans le lims ou dans la configuration.
		

		public State state = new State(); // Reference sur "models.laboratory.common.instance.state" 
								 // pour gerer les differents etats de l'objet.
								 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
								 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
		public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 
			// pour loguer les dernieres modifications utilisateurs

		
		@Override
		public void validate(ContextValidation contextValidation) {
			// TODO Auto-generated method stub
		}

}
