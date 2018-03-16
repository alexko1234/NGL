package rules.services;

import java.util.HashMap;
import java.util.Map;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
//import play.Logger;

public class RulesAgendaFilter6 implements AgendaFilter {

	private String metadataKey;
	private String metadataValue;

	private static Map<String, RulesAgendaFilter6> rulesAgendaFilter = new HashMap<String, RulesAgendaFilter6>();
	
	public static RulesAgendaFilter6 getInstance(String metadataKey, String metadataValue) {
		if (!rulesAgendaFilter.containsKey(metadataValue)) 
			rulesAgendaFilter.put(metadataValue, new RulesAgendaFilter6(metadataKey, metadataValue));
		return rulesAgendaFilter.get(metadataValue);
	}
	
//	public static RulesAgendaFilter6 getInstance(String metadataKey, String metadataValue) {
//		RulesAgendaFilter6 raf = rulesAgendaFilter.get(metadataValue);
//		if (raf == null)
//			rulesAgendaFilter.put(metadataValue, raf = new RulesAgendaFilter6(metadataKey, metadataValue));
//		return raf;
//	}
	
	private RulesAgendaFilter6(String metadataKey, String metadataValue) {
//		super();
		this.metadataKey   = metadataKey;
		this.metadataValue = metadataValue;
	}

//	@Override
//	public boolean accept(Match match) {
//		String s = (String) match.getRule().getMetaData().get(metadataKey);
//		if (s!= null && s.equals(this.metadataValue)) {
//			return true;
//		}else{
//			return false;
//		}
//		
//	}
	@Override
	public boolean accept(Match match) {
		String s = (String) match.getRule().getMetaData().get(metadataKey);
		return s!= null && s.equals(metadataValue);
		// Probably just : return metadataValue.equals(s);
	}

}
