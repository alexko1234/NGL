package models.sra.submit.wgs.instance;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class DataWgs  implements IValidation {
	public String relatifName;            // nom du fichier != lotSeqName avec extention mais sans chemin
	public String directory;	          // chemin
	public String extention;              // extention .fastq, .fastq.gz, .sff
	public String relatifNameMd5;

	@Override
	public void validate(ContextValidation contextValidation) {
		// Verifer que les champs sont bien renseignés :
		if (ValidationHelper.required(contextValidation, this.relatifName,"relatifName")) {
			contextValidation.addKeyToRootKeyName("wgsData::relatifName::" + this.relatifName + "::");
		} else {
			contextValidation.addKeyToRootKeyName("relatifName::");
		}
		
		ValidationHelper.required(contextValidation, this.relatifName , "relatifName");
		ValidationHelper.required(contextValidation, this.directory , "directory");
		ValidationHelper.required(contextValidation, this.extention , "extention");
		ValidationHelper.required(contextValidation, this.relatifNameMd5 , "relatifNameMd5");

		if (ValidationHelper.required(contextValidation, this.relatifName,"relatifName")) {
			contextValidation.removeKeyFromRootKeyName("rawData::relatifName::" + this.relatifName + "::");
		} else {
			contextValidation.removeKeyFromRootKeyName("wgsData::");
		}
	}

}
