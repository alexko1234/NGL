package fr.cea.ig.ngl.test;

import java.util.function.Function;

import fr.cea.ig.play.test.ApplicationFactory;
import play.inject.Bindings;
import play.inject.guice.GuiceApplicationBuilder;
import rules.services.Rules6Component;
import rules.services.test.TestRules6Component;

/**
 * @author ajosso
 *
 */
public class TestAppWithDroolsFactory extends ApplicationFactory {

	public TestAppWithDroolsFactory(String configFileName) {
		super(configFileName);
		// TODO Auto-generated constructor stub
	}

	public TestAppWithDroolsFactory(ApplicationFactory f) {
		super(f);
		// TODO Auto-generated constructor stub
	}

	public TestAppWithDroolsFactory bindRulesComponent() {
		// requires overriding of super.bind(t,u) / super.mod(mod) / constructorClone()
		return (TestAppWithDroolsFactory) bind(Rules6Component.class, TestRules6Component.class);
	}
	
	private <T> TestAppWithDroolsFactory bind(Class<T> t) {
		return (TestAppWithDroolsFactory) this.mod(b -> b.overrides(Bindings.bind(t).toSelf()));
	}
	
	@Override
	public <T,U extends T> TestAppWithDroolsFactory bind(Class<T> t, Class<U> u) {
		return (TestAppWithDroolsFactory)super.bind(t,u);
	}
	
	@Override
	public TestAppWithDroolsFactory mod(Function<GuiceApplicationBuilder,GuiceApplicationBuilder> mod) {
		return (TestAppWithDroolsFactory)super.mod(mod);
	}
	
	@Override
	protected TestAppWithDroolsFactory constructorClone() {
		return new TestAppWithDroolsFactory(this);
	}
}
