package services.io.reception;




import validation.ContextValidation;

import javax.inject.Inject;

import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.FileType;

public class ReceptionFileService {
	
	private final NGLContext ctx;
	@Inject
	public ReceptionFileService(NGLContext ctx) {
		this.ctx = ctx;
	}
	public /*static*/ FileService getFileService(ReceptionConfiguration configuration,
			PropertyFileValue fileValue, ContextValidation contextValidation) {
		
		if(FileType.excel.equals(configuration.fileType)){
			ExcelFileService efs = new ExcelFileService(configuration, fileValue, contextValidation, ctx);
			return efs;
		}else{
			contextValidation.addErrors("Error", "FileType : "+configuration.fileType.toString());
			throw new UnsupportedOperationException("FileType : "+configuration.fileType.toString());
		}
		
		
	}
	
	
}
