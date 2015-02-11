package processes;

import controllers.processes.api.ProcessesSaveForm;
import controllers.processes.api.ProcessesSearchForm;
import models.laboratory.processes.instance.Process;

public class ProcessTestHelper {

	public static Process getFakeProcess(String categoryCode, String typeCode){
		Process p = new Process();
		p.typeCode  = typeCode;
		p.categoryCode = categoryCode;
		return p;
	}
	
	public static ProcessesSearchForm getFakeProcessesSearchForm(String categoryCode, String typeCode){
		ProcessesSearchForm psf = new ProcessesSearchForm();
		psf.categoryCode = categoryCode;
		psf.typeCode = typeCode;
		return psf;
		
	}
	
	public static ProcessesSaveForm getFakeProcessesSaveForm(String supportCode, Process process){
		ProcessesSaveForm psf = new ProcessesSaveForm();
		psf.process = process;
		psf.supportCode = supportCode;
		return psf;
		
	}
}
