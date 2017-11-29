package ngl.sq;

import static fr.cea.ig.play.test.JsonHelper.getJson;
import static fr.cea.ig.play.test.JsonHelper.remove;
import static fr.cea.ig.play.test.JsonHelper.set;

import com.fasterxml.jackson.databind.JsonNode;

import fr.cea.ig.play.test.JsonFacade;
import validation.utils.ValidationHelper;

public class ContainerFactory {
	
	// Modify a prototype to be linked with the sample. 
	public static JsonNode create_00(String code, JsonNode sample) {
		JsonFacade s = new JsonFacade(sample);
		JsonFacade n = JsonFacade
				.getJsonFacade("data/Container_1AIF37ID7_UAT")
				.delete("_id")
				// .delete("traceInformation") -> keep creation date and user
				//.delete("traceInformation/createUser")
				//.delete("traceInformation/creationDate")
				.delete("traceInformation/modifyUser")
				.delete("traceInformation/modifyDate")
				.set("code",code)
				.copy(s,"code","contents[0]/sampleCode")
				.copy(s,"projectCodes[0]","contents[0]/projectCode")
				.copy(s,"taxonCode","contents[0]/taxonCode")
				.copy(s,"ncbiScientificName","contents[0]/ncbiScientificName")
				.copy(s,"code","contents[0]/properties/sampleAliquoteCode/value");
		// Copy some parts of the sample into the container
		// sample -> contents[0], assumes ony one source
		// JsonNode content = get(n,"contents[0]");
		// remap stuff or possibly use the DAO to remap stuff.
		// We create the api level sample and the 
		// map("code","sampleCode");
		// Using full path, Json support should use single string paths
		// map("code","contents[0]/smapleCode");
		return n.jsonNode();
	}

}
