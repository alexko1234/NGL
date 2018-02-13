package rules.services;

import java.util.Arrays;
import java.util.List;



/**
 * Messages can be any kind of object but have to be immutable. Akka can’t enforce immutability (yet) so this has to be by convention.
 * 
 * @author ejacoby
 *
 */
public class RulesMessage {

	private final List<Object> facts;
	private final String keyRules;
	private final String nameRule;
	
	public RulesMessage(String keyRules, String nameRule, List facts) {
		// super();
		this.facts = facts;
		this.keyRules = keyRules;
		this.nameRule = nameRule;
	}
	
	public RulesMessage(String keyRules, String nameRule, Object... facts) {
		// super();
		this.facts = Arrays.asList(facts);
		this.keyRules = keyRules;
		this.nameRule = nameRule;
	}

	public List<Object> getFacts() {
		return facts;
	}

	public String getKeyRules() {
		return keyRules;
	}

	
	public String getNameRule() {
		return nameRule;
	}

	
	
}
