package controllers.experiments.api;

import controllers.ListForm;

public class ExperimentUpdateForm  extends ListForm{
	public boolean stopProcess;
	public boolean retry;
	public String nextStateCode;
}
