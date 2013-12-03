package controllers.experiments.api;

import controllers.ListForm;

public class ExperimentSearchForm extends ListForm{
	public String typeCode;
	@Override
	public String toString() {
		return "ExperimentSearch [typeCode=" + typeCode + "]";
	}
}
