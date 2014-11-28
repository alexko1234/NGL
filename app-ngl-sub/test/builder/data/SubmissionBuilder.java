package builder.data;

import java.util.ArrayList;
import java.util.Date;

import models.laboratory.common.instance.State;
import models.sra.submission.instance.Submission;

public class SubmissionBuilder {

	Submission submission = new Submission();
	
	public SubmissionBuilder withCode(String code)
	{
		submission.code=code;
		return this;
	}
	
	public SubmissionBuilder withSubmissionDirectory(String submissionDirectory)
	{
		submission.submissionDirectory=submissionDirectory;
		return this;
	}
	
	public SubmissionBuilder withSubmissionDate(Date submissionDate)
	{
		submission.submissionDate=submissionDate;
		return this;
	}
	
	public SubmissionBuilder withState(State state)
	{
		submission.state=state;
		return this;
	}
	
	public SubmissionBuilder addExperimentCode(String code)
	{
		if(submission.experimentCodes==null)
			submission.experimentCodes=new ArrayList<String>();
		submission.experimentCodes.add(code);
		return this;
	}
	
	public Submission build()
	{
		return submission;
	}
	
	
	
}
