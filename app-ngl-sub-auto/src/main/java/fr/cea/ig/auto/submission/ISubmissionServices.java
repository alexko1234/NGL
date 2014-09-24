package fr.cea.ig.auto.submission;

import java.util.Set;

import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;

public interface ISubmissionServices {

	public Set<ResourceProperties> getRawDataResources(String submissionCode) throws BirdsException, FatalException ;
}
