package utils;

import models.laboratory.common.instance.State;
import models.sra.submission.instance.Submission;

public class SubmissionBuilder {

	Submission submission = new Submission();
	
	public SubmissionBuilder withCode(String code)
	{
		this.submission.code=code;
		return this;
	}
	
	public SubmissionBuilder withState(State state)
	{
		this.submission.state=state;
		return this;
	}
	
	public Submission build()
	{
		return submission;
	}
	
	
	
}
