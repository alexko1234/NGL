package rules.services;


import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import play.Logger;
import play.Play;

public class RulesServices6 {
	
	
	private KieBase kbase;
	private static final String kbasename = Play.application().configuration().getString("rules.kbasename");
	
	
	private RulesServices6(){
		buildKnowledgeBase();
	}
	
	
	
	private static class SingletonHolder
	{		
		/** Instance unique non préinitialisée */
		private final static RulesServices6 instance = new RulesServices6();
	}
 
	/** Point d'accès pour l'instance unique du singleton */
	public static RulesServices6 getInstance()
	{
		return SingletonHolder.instance;
	}
	
	
	private void buildKnowledgeBase() {
		if(null == kbase){
			Logger.info("Load Drools Rules for KBaseName = "+kbasename);
			KieServices kieServices = KieServices.Factory.get();
			KieContainer kContainer = kieServices.newKieClasspathContainer(play.Play.application().classloader());
		    KieBaseConfiguration kbaseConf = kieServices.newKieBaseConfiguration();
		    kbase = kContainer.newKieBase(kbasename, kbaseConf); 		    
		}
	}

	private KieBase getKieBase() {
		if (kbase == null)
			buildKnowledgeBase();
		return kbase;
	}
	
	public void callRules(String keyRules, String ruleAnnotationName, List<Object> factsToInsert) {
		//Create new statefull session
		KieSession kSession = getKieBase().newKieSession();
		for (Object fact : factsToInsert) {
			kSession.insert(fact);
		}
		kSession.fireAllRules(RulesAgendaFilter6.getInstance(keyRules, ruleAnnotationName));
		//Close session
		kSession.dispose();		
	}
	
	public List<Object> callRulesWithGettingFacts(String keyRules,
			String ruleAnnotationName, List<Object> factsToInsert)
			 {

		// Create new session
		KieSession kSession = getKieBase().newKieSession();
		for (Object fact : factsToInsert) {
			kSession.insert(fact);
		}
		kSession.fireAllRules(RulesAgendaFilter6.getInstance(keyRules,ruleAnnotationName));
		List<Object> factsAfterRules = new ArrayList<Object>(kSession.getObjects());
		// Close session
		kSession.dispose();

		return factsAfterRules;

	}
}
