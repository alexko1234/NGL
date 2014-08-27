package rules.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import play.Logger;
import play.Play;

public class RulesServices 
{
	private static final String pathChangesets = Play.application().configuration().getString("rules.changesets");
	private static KnowledgeBase knowledgeBase;
	
	public RulesServices() {
	}
	
	public void buildKnowledgeBase() throws RulesException
	{
		
		KnowledgeBuilderConfiguration kBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, play.Play.application().classloader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kBuilderConfiguration);
		URL url;
		try {
			url = new URL(pathChangesets);
			kbuilder.add(ResourceFactory.newUrlResource(url), ResourceType.CHANGE_SET);
		} catch (MalformedURLException e) {
			Logger.debug("Switching from  ResourceFactory.newUrlResource(url) to ResourceFactory.newClassPathResource(path) " + pathChangesets);
			kbuilder.add(ResourceFactory.newClassPathResource(pathChangesets), ResourceType.CHANGE_SET);
		}		
		Logger.debug("end of building kbuilder");
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				Logger.error(error.getMessage());
			}
			throw new RulesException("Could not parse knowledge.");
		}
		KnowledgeBaseConfiguration kbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, play.Play.application().classloader()); 
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConfig);
		knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
	}

	public KnowledgeBase getKnowledgeBase() throws RulesException{
		if (knowledgeBase == null)
			buildKnowledgeBase();
		return knowledgeBase;
	}
	
	/**
	 * Call rules and session management 
	 * @param keyRules
	 * @param ruleAnnotationName
	 * @param factsToInsert
	 * @throws RulesException
	 */
	public void callRules(String keyRules, String ruleAnnotationName, List<Object> factsToInsert) throws RulesException {
		
		//Create new session
		StatefulKnowledgeSession kSession = getKnowledgeBase().newStatefulKnowledgeSession();
		for (Object fact : factsToInsert) {
			kSession.insert(fact);
		}
		kSession.fireAllRules(RulesAgendaFilter.getInstance(keyRules, ruleAnnotationName));
		
		
		//Close session
		kSession.dispose();
		
		
	}
	
	/**
	 * Call rules
	 * @param keyRules
	 * @param ruleAnnotationName
	 * @param factsToInsert
	 * @return facts in rules session after calling rules
	 * @throws RulesException
	 */
	public List<Object> callRulesWithGettingFacts(String keyRules, String ruleAnnotationName,List<Object> factsToInsert) throws RulesException {
		
		//Create new session
		StatefulKnowledgeSession kSession = getKnowledgeBase().newStatefulKnowledgeSession();
				
		for (Object fact : factsToInsert) {
			kSession.insert(fact);
		}
		kSession.fireAllRules(RulesAgendaFilter.getInstance(keyRules, ruleAnnotationName));
		List<Object> factsAfterRules = new ArrayList<Object>(kSession.getObjects());
		//Close session
		kSession.dispose();
		return factsAfterRules;
		
	}
	
}
