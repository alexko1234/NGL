package SraValidation;

import java.io.File;
import java.io.IOException;

import models.sra.utils.SraException;

import org.junit.Test;

import services.FileAcServices;
import utils.AbstractTestsSRA;


public class FileAcServicesTest  extends AbstractTestsSRA {
	
	@Test
	public void FileAcServicesSuccess() throws IOException, SraException {
		String fileName = "/env/cns/submit_traces/SRA/ngl-sub/mesTests/RESULT_AC";
		FileAcServices fileAcServices = new FileAcServices();
		fileAcServices.traitementFileAC(new File(fileName));
	}
}
