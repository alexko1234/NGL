package controllers.experiments.api;

import java.util.List;

import controllers.ListForm;

public class ExperimentUpdateForm  extends ListForm{
	public boolean stopProcess;
	public boolean retry;
	public String nextStateCode;
	public List<String> processResolutionCodes;
}
