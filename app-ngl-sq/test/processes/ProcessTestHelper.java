package processes;

import models.laboratory.processes.instance.Process;

public class ProcessTestHelper {

	public static Process getFakeProcess(String categoryCode, String typeCode){
		Process p = new Process();
		p.typeCode  = typeCode;
		p.categoryCode = categoryCode;
		return p;
	}
}
