package rules.services;

import akka.actor.UntypedActor;

public class RulesActor extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		
		//Receive RulesMessage with facts to call rules
		RulesMessage ruleMessage = (RulesMessage)message;
		
		RulesServices rulesServices = new RulesServices();
		rulesServices.callRules(ruleMessage.getKeyRules(),ruleMessage.getNameRule(), ruleMessage.getFacts());
		
	}

}
