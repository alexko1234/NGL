package models.sra.experiment.instance;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

// Declaration d'une collection Experiment (herite de DBObject)
public class Experiment extends DBObject implements IValidation {
	// ExperimentType
	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = projectCode_num
	public String projectCode;     // required pour nos stats
	public String title;	       // required et champs de la proc */
	public String librarySelection;// required et constraint: champs de la proc surchargeable dans conf ou par utilisateurs */
	public String libraryStrategy; // required et constraint. Valeur mise par defaut à "WGS" mais surchargeable */
	public String librarySource;   // required et constraint. Valeur mise par defaut à "GENOMIC" mais surchargeable */
	public String libraryLayout;   // required et constraint. Valeur du champs de la proc à SINGLE ou PAIRED */
	public BigInteger libraryLayoutNominalLength;// required : champs de la proc. bigInteger dans objetSRA mettre à 0 si null ou XX */
	public String libraryLayoutOrientation;	 // champs de la proc renseigné à "forward" ou "forward-reverse" ssi paired */
	public String libraryName;               // required 
	public String libraryConstructionProtocol; // fixé à "none provided" par defaut, mais forme libre
	public String typePlatform;      // required et contrainte, L454 ou illumina en fonction de plateform_map de la proc
	public String instrumentModel;   // required et contrainte et depend de plateformType
    public BigInteger lastBaseCoord; // valeur renseignee ssi illumina paired */
	public BigInteger spotLength;    // required : champs spot_length de la proc et si 0 ou null ou XX mettre 0 en bigInteger */
	public String accession;         // numeros d'accession attribué par ebi */
 	public Date releaseDate;         // required, date de mise à disposition en public par l'EBI
 	//SPOTDECODESPEC.READSPEC a remplir en fonction de typePlatform, libraryLayout, libraryLayoutOrientation
 	// voir dans create_ExperimentType
	//Projects ref
	public String sampleCode;
	public String studyCode;
	public ReadSpec[] tabReadSpec;
	public Run run; // le run est rattache à l'experiment

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
	}

}
