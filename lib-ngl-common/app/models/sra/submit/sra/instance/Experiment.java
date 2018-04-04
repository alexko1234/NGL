package models.sra.submit.sra.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.cea.ig.DBObject;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;

// todo : ajouter  libraryName dans la validation

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
	public String typePlatform = null;      // required et contrainte, L454 ou illumina en fonction de plateform_map de la proc
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
	public String sampleAccession = null;
	public String studyAccession = null;
	public String readSetCode = null;
	public List<ReadSpec> readSpecs = new ArrayList<>();
	public Run run = null; // le run est rattache à l'experiment
	public State state = new State(); // Reference sur "models.laboratory.common.instance.state" 
							 // pour gerer les differents etats de l'objet.
	
	public String adminComment; // commentaire privé "reprise historique"				
	public TraceInformation traceInformation = new TraceInformation();// .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs
	public Date releaseDate; 
	public Date firstSubmissionDate; 

	// ajouter instrumentModel et libraryName.

	// Verifie validite de l'experiment sans verifier validite du run associé :
	public void validateLight(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("experiment");
		// Verifier que status est bien rensigne, et si != new alors libraryName renseigné :
		//System.out.println("Dans exp.validate, stateCode =" +state.code);
		SraValidationHelper.validateFreeText(contextValidation,"title", this.title);
		SraValidationHelper.validateState(ObjectType.CODE.SRASubmission, this.state, contextValidation);
		ValidationHelper.required(contextValidation, this.libraryName , "libraryName");
		// Verifer que projectCode est bien renseigné et qu'il existe bien dans lims :
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		ValidationHelper.required(contextValidation, this.title , "title");
        // Verifer que librarySelection libraryStrategy librarySource et libraryLayout sont bien renseignés avec bonne valeur :		
		SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySelection, VariableSRA.mapLibrarySelection(), "librarySelection");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryStrategy, VariableSRA.mapLibraryStrategy(), "libraryStrategy");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.librarySource, VariableSRA.mapLibrarySource(), "librarySource");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryLayout, VariableSRA.mapLibraryLayout(), "libraryLayout"); // single ou paired

		//ValidationHelper.required(contextValidation, this.libraryName , "libraryName");
		//ValidationHelper.required(contextValidation, this.libraryConstructionProtocol , "libraryConstructionProtocol");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.typePlatform, VariableSRA.mapTypePlatform(), "typePlatform");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.instrumentModel, VariableSRA.mapInstrumentModel(), "instrumentModel");
		if (this.sampleAccession == null) {
			ValidationHelper.required(contextValidation, this.sampleCode , "sampleCode");
		}
		if (this.studyAccession == null) {
			ValidationHelper.required(contextValidation, this.studyCode , "studyCode");
		}
		ValidationHelper.required(contextValidation, this.readSetCode , "readSetCode");

		System.out.println("platform = " + this.typePlatform);
		// Dans le cas des illumina ou LS454
		if (! "OXFORD_NANOPORE".equalsIgnoreCase(this.typePlatform)) {
			if ("illumina".equalsIgnoreCase(this.typePlatform)) {
			ValidationHelper.required(contextValidation, this.spotLength , "spotLength");
			}
			if (StringUtils.isNotBlank(this.libraryLayout) && libraryLayout.equalsIgnoreCase("paired")){
				// Verifer que lastBaseCoord est bien renseigné ssi Illumina et paired:
				ValidationHelper.required(contextValidation, this.lastBaseCoord , "lastBaseCoord");	
				ValidationHelper.required(contextValidation, this.libraryLayoutNominalLength , "libraryLayoutNominalLength");
				SraValidationHelper.requiredAndConstraint(contextValidation, this.libraryLayoutOrientation, VariableSRA.mapLibraryLayoutOrientation(), "libraryLayoutOrientation");
			}
		}
		// Si nanopore pas de readspec et pas spotLength, lastBasecoord, et libraryLayoutNominalLength
		// Verifier les readSpec :
		SraValidationHelper.validateReadSpecs(contextValidation, this);
		
		// verifier que code est bien renseigné
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_EXPERIMENT_COLL_NAME, contextValidation);
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		contextValidation.removeKeyFromRootKeyName("experiment");

		// todo :
		//if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", this.readSetCode)) {
			//throw new SraException("le readSetcode "+ experiment.run.code + " existe deja dans la collection Experiment de la base");
			//mess += "le readSetcode "+ this.run.code + " existe deja dans la collection Experiment de la base";
		//}

		
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		this.validateLight(contextValidation);
		if (! "OXFORD_NANOPORE".equalsIgnoreCase(this.typePlatform)) { 
			//pas mis dans validateLight car il existe dans reprise historique des illumina et LS454 sans lastBaseCoord 
			ValidationHelper.required(contextValidation, this.lastBaseCoord , "lastBaseCoord");	
		}
		contextValidation.addKeyToRootKeyName("experiment");
		// Verifier le run :
		if (this.run == null) {
			contextValidation.addErrors("run", " aucune valeur");
		} else {
			this.run.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("experiment");
	}

	
}
