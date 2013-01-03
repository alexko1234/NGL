package utils;

import static play.test.Helpers.fakeApplication;

import org.junit.After;
import org.junit.Before;

import play.test.FakeApplication;
import play.test.Helpers;

public abstract class AbstractTests {
	FakeApplication app;
	@Before
	public void start(){
		 app = fakeApplication();
		 Helpers.start(app);
		 init();
	}
	@After
	public void stop(){
		Helpers.stop(app);
	}
	
	public abstract void init();
}
