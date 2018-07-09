package rules.services;

import java.util.List;
// import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import fr.cea.ig.lfw.utils.LazyLambdaSupplier;
import fr.cea.ig.play.migration.NGLConfig;

@Singleton
class LazyRulesKey extends LazyLambdaSupplier<String> {
	
	@Inject
	public LazyRulesKey(NGLConfig config) {
		super(() -> config.getRulesKey());
	}
	
}

// Is this a singleton ? Looks like it could be as the initializers were
// static.
@Singleton
public class LazyRules6Actor extends LazyLambdaSupplier<ActorRef> {
	
	private LazyRulesKey rulesKey;
	
	@Inject
	public LazyRules6Actor(ActorSystem actorSystem, LazyRulesKey rulesKey) {
		super(() -> actorSystem.actorOf(Props.create(RulesActor6.class)));
		this.rulesKey = rulesKey;
	}
	
	public void tellMessage(String rulesCode, List<Object> objects) {
		get().tell(new RulesMessage(rulesKey.get(), rulesCode, objects),null);
	}
	
	public void tellMessage(String rulesCode, Object... objects) {
		get().tell(new RulesMessage(rulesKey.get(), rulesCode, objects),null);
	}
	
}
