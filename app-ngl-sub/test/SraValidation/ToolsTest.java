package SraValidation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import utils.AbstractTestsSRA;

public class ToolsTest extends AbstractTestsSRA {
		
	@Test
	public void SymbolicLinkSuccess() throws IOException  {
		String directory = "/env/cns/submit_traces/SRA/ngl-sub/linkTest";
		File dir = new File("/env/cns/submit_traces/SRA/ngl-sub/linkTest");
		dir.mkdirs();
		
		File fileCible = new File(directory + File.separator + "cible");
		File fileLien = new File("/env/cns/submit_traces/SRA/ngl-sub/linkTest/lien_2");
		Path lien = Paths.get(fileLien.getPath());
		Path cible = Paths.get(fileCible.getPath());
		Files.createSymbolicLink(lien, cible);

		
	}
}
