package controllers.submissions.api;

import java.util.List;

import models.laboratory.run.instance.ReadSet;

// classe utilisee pour la recuperation des variables du formulaire submissions/create.scala.html
// attention à mettre les memes noms ici et dans le formulaire create.scala.html
public class SubmissionCreationForm {
	//public List<ReadSet> readsets;
	public String projCode;
	public String studyCode;
	public String configurationCode;
	public List<String> readSetCodes;
}
