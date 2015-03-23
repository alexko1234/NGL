package controllers.submissions.api;

import java.util.List;

import views.components.datatable.DatatableForm;

// classe utilisee pour la recuperation des variables du formulaire submissions/create.scala.html
// attention à mettre les memes noms ici et dans le formulaire create.scala.html
public class SubmissionsCreationForm  extends DatatableForm {
	//public List<ReadSet> readsets;
	public String projCode;
	public String studyCode;
	public String configurationCode;
	public List<String> readSetCodes;
}
