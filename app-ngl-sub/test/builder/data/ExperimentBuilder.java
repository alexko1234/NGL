package builder.data;

import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.Run;

public class ExperimentBuilder {

	Experiment experiment = new Experiment();
	
	public ExperimentBuilder withCode(String code)
	{
		experiment.code=code;
		return this;
	}
	
	public ExperimentBuilder withRun(Run run)
	{
		experiment.run=run;
		return this;
	}
	
	public Experiment build()
	{
		return experiment;
	}
}
