package fr.cea.ig.auto.submission;

import java.util.Set;

import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import models.sra.submit.util.SraException;

public class SubmissionServices implements ISubmissionServices{

	
	//TODO
	@Override
	public Set<ResourceProperties> getRawDataResources(String submissionCode)
	{
		//TODO
		//Call NGL SUB Services to get rawData resources from submission
		//Convert JSON to JobResource
		return null;
	}
	
	public void treatmentFileRelease(String ebiFileName)
	{
		
	}

	
}
