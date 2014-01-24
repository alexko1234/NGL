package models.sra.experiment.instance;

import validation.ContextValidation;
import validation.IValidation;

public class RawData  implements IValidation {
	public String name;            // nom du fichier != lotSeqName avec extention mais sans chemin
	public String path;	           // chemin
	public String extention;       // extention .fastq, .fastq.gz
	public String md5;	           
	public int lotSeqCode; 
	public String projectCode; 
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub	
	}

}
