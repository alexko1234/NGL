package sra.scripts.utils;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import sra.scripts.AbstractScript;

public class EbiAPI {
	
	private static final play.Logger.ALogger logger = play.Logger.of(EbiAPI.class);
	private final WSClient ws;
	
	// On demande à play d'instancier la classe en lui fournissant un objet WSClient
	@Inject
	public EbiAPI(WSClient ws) {
		this.ws = ws;
	}
	
	/**
	 * Verifie si lobjet dont le numeros d'accession est indiqué existe sur le serveur de l'EBI
	 * @param type : type d'objet : projects, studies, samples, experiments, runs ou submission
	 * @param ac   : numeros d'accession
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */	
	protected boolean ebiExists(String ac, String type) {
		String url = String.format("https://www.ebi.ac.uk/ena/submit/drop-box/%s/%s?format=xml", type, ac);
		logger.debug("url : %s", url);
		//ws.url(https://www.ebi.ac.uk/ena/submit/drop-box/samples/$ac?format=xml);
		WSRequest wr = ws.url(url);
		wr.setAuth("Webin-9", "Axqw16nI");
		try {
			WSResponse wre = wr.get().toCompletableFuture().get();
			//printfln("Reponse :%s", wre.getBody());
			return wre.getStatus() == 200;
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} 
	}		
	/**
	 * Verifie si l'objet project dont l'identifiant est indiqué existe sur le serveur de l'EBI
	 * @param   bioProjectId  : identifiant du project
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */
	public boolean ebiProjectExists(String projectId) {
		return ebiExists(projectId, "projects");
	}
	
	/**
	 * Verifie si le study dont le numeros d'accession est indiqué existe sur le serveur de l'EBI
	 * @param ac  : numeros d'accession
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */
	public boolean ebiStudyExists(String ac) {
		return ebiExists(ac, "studies");
	}
	
	/**
	 * Verifie si le sample dont le numeros d'accession est indiqué existe sur le serveur de l'EBI
	 * @param ac  : numeros d'accession
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */
	public boolean ebiSampleExists(String ac) {
		return ebiExists(ac, "samples");
	}	
	
	/**
	 * Verifie si l'experiment dont le numeros d'accession est indiqué existe sur le serveur de l'EBI
	 * @param ac  : numeros d'accession
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */
	public boolean ebiExperimentExists(String ac) {
		return ebiExists(ac, "experiments");
	}
	
	/**
	 * Verifie si le run dont le numeros d'accession est indiqué existe sur le serveur de l'EBI
	 * @param ac  : numeros d'accession
	 * @return  true si l'AC existe à l'EBI, false sinon 
	 */
	public boolean ebiRunExists(String ac) {
		return ebiExists(ac, "runs");
	}

}
