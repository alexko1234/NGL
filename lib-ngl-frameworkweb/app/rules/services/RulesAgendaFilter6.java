package rules.services;



import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;



public class RulesAgendaFilter6 implements AgendaFilter{

	private String metadataKey;
	private String metadataValue;

	private static RulesAgendaFilter6 rulesAgendaFilter;
	
	public static RulesAgendaFilter6 getInstance(String metadataKey, String metadataValue) {
		if (rulesAgendaFilter == null)
			rulesAgendaFilter = new RulesAgendaFilter6(metadataKey, metadataValue);
		rulesAgendaFilter.metadataKey = metadataKey;
		rulesAgendaFilter.metadataValue = metadataValue;
		return rulesAgendaFilter;
	}
	
	private RulesAgendaFilter6(String metadataKey, String metadataValue) {
		super();
		this.metadataKey = metadataKey;
		this.metadataValue = metadataValue;
	}

	@Override
	public boolean accept(Match match) {
		String s = (String) match.getRule().getMetaData().get(metadataKey);
		if (s!= null && s.equals(this.metadataValue)) {
			//Logger.debug("rule " + activation.getRule().getName() + " is activated ");
			return true;
		}
		//Logger.debug("rule " + activation.getRule().getName() + " is not activated ");
		return false;
	}

	
	

}
