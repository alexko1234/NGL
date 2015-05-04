package builder.data;

import models.laboratory.common.instance.State;
import models.sra.submit.sra.instance.Configuration;

public class ConfigurationBuilder {
	
	Configuration configuration = new Configuration();
	
	public ConfigurationBuilder withCode(String code)
	{
		configuration.code=code;
		return this;
	}
	
	public ConfigurationBuilder withState(State state)
	{
		configuration.state=state;
		return this;
	}
	
	public Configuration build()
	{
		return configuration;
	}
}
