package models.sra.submit.sra.instance;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class RawData  implements IValidation {
	public String relatifName;            // nom du fichier != lotSeqName avec extention mais sans chemin
	public String directory;	          // chemin
	public String extention;              // extention .fastq, .fastq.gz, .sff
	public String md5;
	public String location;

	@Override
	public void validate(ContextValidation contextValidation) {
		// Verifer que les champs sont bien renseignés :
		if (ValidationHelper.required(contextValidation, this.relatifName, "relatifName")) {
			contextValidation.addKeyToRootKeyName("rawData.relatifName_" + this.relatifName + "");
		} else {
			contextValidation.addKeyToRootKeyName("rawData");
		}
		
		ValidationHelper.required(contextValidation, this.relatifName , "relatifName");
		ValidationHelper.required(contextValidation, this.directory , "directory");
		ValidationHelper.required(contextValidation, this.extention , "extention");
		ValidationHelper.required(contextValidation, this.md5 , "md5");
		ValidationHelper.required(contextValidation, this.location , "location");

		if (ValidationHelper.required(contextValidation, this.relatifName,"relatifName")) {
			contextValidation.removeKeyFromRootKeyName("rawData.relatifName_" + this.relatifName + "");
		} else {
			contextValidation.removeKeyFromRootKeyName("rawData");
		}
	}

}
