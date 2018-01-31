package fr.cea.ig.ngl.test;

import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static play.inject.Bindings.bind;

import java.util.function.Function;

import fr.cea.ig.authentication.IAuthenticator;
import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.ngl.test.authentication.AuthenticatorAdmin;
import fr.cea.ig.ngl.test.authentication.AuthenticatorNobody;
import fr.cea.ig.ngl.test.authentication.AuthenticatorRead;
import fr.cea.ig.ngl.test.authentication.AuthenticatorReadWrite;
import fr.cea.ig.ngl.test.authentication.AuthenticatorWrite;
import fr.cea.ig.ngl.test.authentication.Identity;
import fr.cea.ig.ngl.test.authorization.TestAuthorizator;
import fr.cea.ig.play.test.ApplicationFactory;
import fr.cea.ig.play.test.WSHelper;
import play.Application;
import play.inject.Bindings;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http.Status;

// Takes an application factory and provides ways to configure the
// authentication.
// Authentication could be set at runtime using some global or 
// possibly some application related method.
public class TestAppAuthFactory extends ApplicationFactory {

	public TestAppAuthFactory(String cf) {
		super(cf);
	}
	
	protected TestAppAuthFactory(TestAppAuthFactory f) {
		super(f);
	}
	
	protected TestAppAuthFactory constructorClone() {
		return new TestAppAuthFactory(this);
	}
	
	// covariant overrides
	public TestAppAuthFactory mod(Function<GuiceApplicationBuilder,GuiceApplicationBuilder> mod) {
		return (TestAppAuthFactory)super.mod(mod);
	}
	
	@Override
	public <T,U extends T> TestAppAuthFactory bind(Class<T> t, Class<U> u) {
		return (TestAppAuthFactory)super.bind(t,u);
	}

	public TestAppAuthFactory as(Identity i) {
		switch (i) {
		case Nobody   : return bind(IAuthenticator.class,AuthenticatorNobody.class);
		case Read     : return bind(IAuthenticator.class,AuthenticatorRead.class);
		case Write    : return bind(IAuthenticator.class,AuthenticatorWrite.class);
		case ReadWrite: return bind(IAuthenticator.class,AuthenticatorReadWrite.class);
		case Admin    : return bind(IAuthenticator.class,AuthenticatorAdmin.class);
		default       : throw new RuntimeException("unhandled identity " + i); 
		}
	}
	
	public TestAppAuthFactory asWorse(Identity i) {
		return as(worsen(i));
	}

	// Test that the url is acessible for at least the given
	// permission.
	
	// Could define maps for constants.
	
	private static Identity worsen(Identity i) {
		switch (i) {
		case Nobody:    return Identity.Nobody;
		case Read:      return Identity.Nobody;
		case ReadWrite: return Identity.Read;
		case Write:     return Identity.Read;
		case Admin:     return Identity.ReadWrite;
		default:		throw new RuntimeException("no worsening defined for " + i);
		}
	}
	
	public void authURL(Identity i, String url) {
		as(i)     .ws(ws -> WSHelper.get(ws,url,Status.OK));
		asWorse(i).ws(ws -> WSHelper.get(ws,url,Status.FORBIDDEN));
	}
	
//	private static Application devapp(Identity i) {
//		switch (i) {
//		case Nobody:    return ngl.sub.Global.devapp(TestAuthConfig.asNobody);
//		case Read:      return ngl.sub.Global.devapp(TestAuthConfig.asRead);
//		case Write:     return ngl.sub.Global.devapp(TestAuthConfig.asWrite);
//		case ReadWrite: return ngl.sub.Global.devapp(TestAuthConfig.asReadWrite);
//		case Admin:     return ngl.sub.Global.devapp(TestAuthConfig.asAdmin);
//		default:        throw new RuntimeException("no auth config defined for " + i);
//		}
//	}
//	
//	private static void checkURL(Identity i, String url, int status) {
//	    testInServer(devapp(i),
//	    		ws -> {	    	
//	    			WSHelper.get(ws,url,status);
//	    		});
//	}

//	public static ApplicationFactory withIdentity(ApplicationFactory f, Identity i) {
//		switch (i) {
//		case Nobody   : return f.bind(IAuthenticator.class,AuthenticatorNobody.class);
//		case Read     : return f.bind(IAuthenticator.class,AuthenticatorRead.class);
//		case Write    : return f.bind(IAuthenticator.class,AuthenticatorWrite.class);
//		case ReadWrite: return f.bind(IAuthenticator.class,AuthenticatorReadWrite.class);
//		case Admin    : return f.bind(IAuthenticator.class,AuthenticatorAdmin.class);
//		default       : throw new RuntimeException("unhandled identity " + i); 
//		}
//	}
	
}
