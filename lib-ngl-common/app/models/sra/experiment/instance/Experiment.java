package models.sra.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import play.Logger;



import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;

// Declaration d'une collection Experiment (herite de DBObject)
public class Experiment extends DBObject implements IValidation {
	// ExperimentType
	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = projectCode_num
	public String projectCode = null;     // required pour nos stats
	public String title = null;	       // required et champs de la proc */
	public String librarySelection = null;// required et constraint: champs de la proc surchargeable dans conf ou par utilisateurs */
	public String libraryStrategy = null; // required et constraint. Valeur mise par defaut à "WGS" mais surchargeable */
	public String librarySource = null;   // required et constraint. Valeur mise par defaut à "GENOMIC" mais surchargeable */
	public String libraryLayout = null;   // required et constraint. Valeur du champs de la proc à SINGLE ou PAIRED */
	public Integer libraryLayoutNominalLength = null;// required : champs de la proc. bigInteger dans objetSRA mettre à 0 si null ou XX */
	public String libraryLayoutOrientation = null;	 // champs de la proc renseigné à "forward" ou ssi paired "forward-reverse"  ou "reverse-forward"*/
	public String libraryName = null;               // required 
	public String libraryConstructionProtocol = null; // fixé à "none provided" par defaut, mais forme libre
	public String typePlatform = "ILLUMINA";      // required et contrainte, L454 ou illumina en fonction de plateform_map de la proc
	// InstrumentUsedType de run ou code= machine et typeCode la techno
	public String instrumentModel = null;   // required et contrainte et depend de plateformType.
	// Actuellement forcement Illumina puisque collection Illumina
    public Integer lastBaseCoord = null; // valeur renseignee ssi illumina paired */
	public Long spotLength = null;    // required : champs spot_length de la proc et si 0 ou null ou XX mettre 0 en bigInteger */
	public String accession = null;         // numeros d'accession attribué par ebi */
 	//SPOTDECODESPEC.READSPEC a remplir en fonction de typePlatform, libraryLayout, libraryLayoutOrientation
 	// voir dans create_ExperimentType
	//Projects ref
	public String sampleCode = null;
	public String studyCode = null;
	public String readSetCode = null;
	public List<ReadSpec> readSpecs = new ArrayList<ReadSpec>();
	public Run run = null; // le run est rattache à l'experiment
	public State state = new State(); // Reference sur "models.laboratory.common.instance.state" 
							 // pour gerer les differents etats de l'objet.
							 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
							 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
	public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs

	
	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("experiment::");
		// Verifer que projectCode est bien renseigné et qu'il existe bien dans lims :
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		ValidationHelper.required(contextValidation, this.title , "title");
		// Verifer que librarySelection est bien renseigné avec bonne valeur :		
		SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySelection, VariableSRA.mapLibrarySelection, "librarySelection");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryStrategy, VariableSRA.mapLibraryStrategy, "libraryStrategy");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySource, VariableSRA.mapLibrarySource, "librarySource");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryLayout, VariableSRA.mapLibraryLayout, "libraryLayout");
		// Verifer que lastBaseCoord est bien renseigné ssi paired:
		if (this.libraryLayout != null && libraryLayout.equalsIgnoreCase("paired")){
			if (this.lastBaseCoord == null) {
				contextValidation.addErrors("lastBaseCoord", " aucune valeur et donnée pairée");
			}	
		}
		ValidationHelper.required(contextValidation, this.libraryLayoutNominalLength , "libraryLayoutNominalLength");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryLayoutOrientation, VariableSRA.mapLibraryLayoutOrientation, "libraryLayoutOrientation");
		ValidationHelper.required(contextValidation, this.libraryName , "libraryName");
		ValidationHelper.required(contextValidation, this.libraryConstructionProtocol , "libraryConstructionProtocol");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.typePlatform, VariableSRA.mapTypePlatform, "typePlatform");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.instrumentModel, VariableSRA.mapInstrumentModel, "instrumentModel");
		ValidationHelper.required(contextValidation, this.spotLength , "spotLength");
		ValidationHelper.required(contextValidation, this.sampleCode , "sampleCode");
		ValidationHelper.required(contextValidation, this.studyCode , "studyCode");
		ValidationHelper.required(contextValidation, this.readSetCode , "readSetCode");

		// Verifier les readSpec :
		SraValidationHelper.validateReadSpecs(contextValidation, this);
		// Verifier le run :
		this.run.validate(contextValidation);
		// verifier que code est bien renseigné
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_EXPERIMENT_COLL_NAME, contextValidation);
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		contextValidation.removeKeyFromRootKeyName("experiment::");

	}

	
}
