package services.io.reception;




import validation.ContextValidation;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.FileType;

public class ReceptionFileService {
	
	
	public static FileService getFileService(ReceptionConfiguration configuration,
			PropertyFileValue fileValue, ContextValidation contextValidation) {
		
		if(FileType.excel.equals(configuration.fileType)){
			ExcelFileService efs = new ExcelFileService(configuration, fileValue, contextValidation);
			return efs;
		}else{
			contextValidation.addErrors("Error", "FileType : "+configuration.fileType.toString());
			throw new UnsupportedOperationException("FileType : "+configuration.fileType.toString());
		}
		
		
	}
	
	
}
