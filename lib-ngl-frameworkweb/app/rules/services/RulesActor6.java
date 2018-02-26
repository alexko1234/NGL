package rules.services;

import akka.actor.AbstractActor;

public class RulesActor6 extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder()
			   .match(RulesMessage.class, m -> {
				   RulesServices6.getInstance().callRules(m.getKeyRules(), m.getNameRule(), m.getFacts());	
				})
			   .build();
	}

}

////import play.Logger;
//import akka.actor.UntypedActor;
//
//public class RulesActor6 extends UntypedActor {
//
//	@Override
//	public void onReceive(Object message) throws Exception {
//		//Receive RulesMessage with facts to call rules
//		RulesMessage ruleMessage = (RulesMessage)message;
//		RulesServices6.getInstance().callRules(ruleMessage.getKeyRules(), ruleMessage.getNameRule(), ruleMessage.getFacts());		
//	}
//
//}
