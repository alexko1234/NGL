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
import org.w3c.dom.Document;

import scala.xml.Node;
import utils.AbstractTestsSRA;
import play.libs.XML;
import play.libs.ws.*;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;
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
	@Test
	public void testhttp() throws IOException  {
		Promise<WSResponse> homePage = WS.url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id=1735743&retmote=xml").get();
		Promise<Node> xml = homePage.map(response -> {
			System.out.println();
			//Document d = XML.fromString(response.getBody());
			Node n = scala.xml.XML.loadString(response.getBody());
			System.out.println("J'ai une reponse ?"+ n.toString());
			return n;
		});
		
		
		System.out.println("J'ai une reponse ?"+ xml.toString());
	
		//Promise<WSResponse> result = WS.url("http://example.com").post("content");	
	
	}
}
