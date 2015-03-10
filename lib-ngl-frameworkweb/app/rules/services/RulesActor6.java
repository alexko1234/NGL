package rules.services;

import akka.actor.UntypedActor;

public class RulesActor6 extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		
		//Receive RulesMessage with facts to call rules
		RulesMessage ruleMessage = (RulesMessage)message;
		RulesServices6.getInstance().callRules(ruleMessage.getKeyRules(),ruleMessage.getNameRule(), ruleMessage.getFacts());
		
	}

}
