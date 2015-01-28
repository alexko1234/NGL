package builder.data;

import models.sra.submit.common.instance.Study;

public class StudyBuilder {

	Study study = new Study();
	
	public StudyBuilder withCode(String code)
	{
		study.code=code;
		return this;
	}
	
	public Study build()
	{
		return study;
	}
}
