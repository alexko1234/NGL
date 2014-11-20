package models.sra.experiment.instance;

import validation.ContextValidation;
import validation.IValidation;
import validation.utils.ValidationHelper;

public class RawData  implements IValidation {
	public String relatifName;            // nom du fichier != lotSeqName avec extention mais sans chemin
	public String path;	                  // chemin
	public String extention;              // extention .fastq, .fastq.gz, .sff
	public String md5;
	public String submissionRelatifName;  
	public String submissionPath;
	public String submissionMd5;
	@Override
	public void validate(ContextValidation contextValidation) {
		// Verifer que les champs sont bien renseignés :
		ValidationHelper.required(contextValidation, this.relatifName , "relatifName");
		ValidationHelper.required(contextValidation, this.path , "path");
		ValidationHelper.required(contextValidation, this.extention , "extention");
		ValidationHelper.required(contextValidation, this.md5 , "md5");
		ValidationHelper.required(contextValidation, this.submissionRelatifName , "submissionRelatifName");
		ValidationHelper.required(contextValidation, this.submissionPath , "submissionPath");
		ValidationHelper.required(contextValidation, this.submissionMd5 , "submissionMd5");
	}

}
