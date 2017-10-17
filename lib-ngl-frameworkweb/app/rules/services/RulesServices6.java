package rules.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
// import org.drools.runtime.StatefulKnowledgeSession;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
// import org.kie.api.builder.KieBuilder;
// import org.kie.api.builder.KieFileSystem;
// import org.kie.api.builder.KieRepository;
// import org.kie.api.builder.Message.Level;
// import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import play.Logger;
// import play.Play;
import play.Application;

import javax.inject.Inject;

public class RulesServices6 {
	
	/**
	 * Logger.
	 */
	private static final Logger.ALogger logger = Logger.of(RulesServices6.class);

	/**
	 * Singleton ref.
	 */
	private static class SingletonHolder {		
		// private final static RulesServices6 instance = new RulesServices6();
		// @Inject
		private static RulesServices6 instance;
	}

	private KieBase kbase;
	
	private String kbasename;
	// private static final String kbasename = Play.application().configuration().getString("rules.kbasename");
	
	// @Inject
	private RulesServices6(Application app) {
		kbasename = app.configuration().getString("rules.kbasename");
		buildKnowledgeBase(app);
	}
	
 
	public static void initSingleton(Application app) {
		logger.debug("initializaing singleton");
		SingletonHolder.instance = new RulesServices6(app);
		logger.debug("singleton initialized");
	}
	
	/** Point d'accès pour l'instance unique du singleton */
	public static RulesServices6 getInstance() {
		if (SingletonHolder.instance == null)
			throw new RuntimeException("RulesServices6 not intiailized, call initSingleton");
		return SingletonHolder.instance;
	}
	
	private void buildKnowledgeBase(Application app) {
		if (null == kbase && StringUtils.isNotBlank(kbasename)) {
			logger.info("Load Drools Rules for KBaseName = "+ kbasename);
			KieServices kieServices = KieServices.Factory.get();
			KieContainer kContainer = kieServices.newKieClasspathContainer(/*play.Play.application()*/app.classloader());
		    KieBaseConfiguration kbaseConf = kieServices.newKieBaseConfiguration();
		    kbase = kContainer.newKieBase(kbasename, kbaseConf); 		    
		} else if(StringUtils.isBlank(kbasename)) {
			logger.warn("Load Drools Rules : rules.kbasename is empty");
		}
	}

	private KieBase getKieBase() {
		if (kbase == null)
			// buildKnowledgeBase();
			throw new RuntimeException("KieBase instance should have been created at RulesServices6 creation");
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
			String ruleAnnotationName, List<Object> factsToInsert) {

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
