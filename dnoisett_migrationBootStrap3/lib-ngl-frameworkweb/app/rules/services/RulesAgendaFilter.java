package rules.services;

import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaFilter;

import play.Logger;

public class RulesAgendaFilter implements AgendaFilter{

	private String metadataKey;
	private String metadataValue;

	private static RulesAgendaFilter rulesAgendaFilter;
	
	public static RulesAgendaFilter getInstance(String metadataKey, String metadataValue) {
		if (rulesAgendaFilter == null)
			rulesAgendaFilter = new RulesAgendaFilter(metadataKey, metadataValue);
		rulesAgendaFilter.metadataKey = metadataKey;
		rulesAgendaFilter.metadataValue = metadataValue;
		return rulesAgendaFilter;
	}
	
	private RulesAgendaFilter(String metadataKey, String metadataValue) {
		super();
		this.metadataKey = metadataKey;
		this.metadataValue = metadataValue;
	}

	@Override
	public boolean accept(Activation activation) {
		String s = (String) activation.getRule().getMetaData().get(metadataKey);
		if (s!= null && s.equals(this.metadataValue)) {
			//Logger.debug("rule " + activation.getRule().getName() + " is activated ");
			return true;
		}
		//Logger.debug("rule " + activation.getRule().getName() + " is not activated ");
		return false;

	}

	
	

}
