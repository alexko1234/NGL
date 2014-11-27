package builder.data;

import java.util.ArrayList;

import models.sra.experiment.instance.RawData;
import models.sra.experiment.instance.Run;

public class RunBuilder {

	Run run = new Run();
	
	public RunBuilder withCode(String code)
	{
		run.code=code;
		return this;
	}
	
	public RunBuilder addRawData(RawData rawData)
	{
		if(run.listRawData==null)
			run.listRawData=new ArrayList<RawData>();
		run.listRawData.add(rawData);
		return this;
	}
	
	public Run build()
	{
		return run;
	}
	
	
}
