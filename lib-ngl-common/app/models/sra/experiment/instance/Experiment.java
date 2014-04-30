package models.sra.experiment.instance;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

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
	public BigInteger libraryLayoutNominalLength = null;// required : champs de la proc. bigInteger dans objetSRA mettre à 0 si null ou XX */
	public String libraryLayoutOrientation = null;	 // champs de la proc renseigné à "forward" ou "forward-reverse" ssi paired */
	public String libraryName = null;               // required 
	public String libraryConstructionProtocol = null; // fixé à "none provided" par defaut, mais forme libre
	public String typePlatform = "ILLUMINA";      // required et contrainte, L454 ou illumina en fonction de plateform_map de la proc
	// InstrumentUsedType de run ou code= machine et typeCode la techno
	public String instrumentModel = null;   // required et contrainte et depend de plateformType.
	// Actuellement forcement Illumina puisque collection Illumina
    public Integer lastBaseCoord = null; // valeur renseignee ssi illumina paired */
	public Long spotLength = null;    // required : champs spot_length de la proc et si 0 ou null ou XX mettre 0 en bigInteger */
	public String accession = null;         // numeros d'accession attribué par ebi */
 	public Date releaseDate = null;         // required, date de mise à disposition en public par l'EBI
 	//SPOTDECODESPEC.READSPEC a remplir en fonction de typePlatform, libraryLayout, libraryLayoutOrientation
 	// voir dans create_ExperimentType
	//Projects ref
	public String sampleCode = null;
	public String studyCode = null;
	public List<ReadSpec> readSpecs = new ArrayList<ReadSpec>();
	public String readSetCode = null;
	public Run run = null; // le run est rattache à l'experiment
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
