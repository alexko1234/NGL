package SraValidation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTestsSRA;

public class ToolsTest extends AbstractTestsSRA {
		
	@Test
	public void SymbolicLinkSuccess() throws IOException  {
		String nameDirectory = "/env/cns/submit_traces/SRA/NGL_test/tests_liens/linkTest4";
		
		File dir = new File(nameDirectory);
		if (!dir.exists()) {
			if(!dir.mkdirs()){
				System.out.println("impossible de creer repertoire nameDirectory");
			} 
			
		}
		File fileCible = new File(nameDirectory + File.separator + "cible");
		if (fileCible.exists()) {
			fileCible.delete();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileCible));
			writer.write("maCible");
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		File fileLien = new File(nameDirectory + File.separator + "lien_3");
		if (fileLien.exists()){
			fileLien.delete();
		}
		Path lien = Paths.get(fileLien.getPath());
		Path cible = Paths.get(fileCible.getPath());
		Files.createSymbolicLink(lien, cible);
		Assert.assertTrue(fileLien.exists());
		
	}
}
