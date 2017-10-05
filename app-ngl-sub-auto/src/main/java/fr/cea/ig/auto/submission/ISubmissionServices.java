package fr.cea.ig.auto.submission;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.api.exception.JSONDeviceException;

public interface ISubmissionServices {

	public Set<ResourceProperties> getRawDataResources(String submissionCode) throws BirdsException, FatalException ;

	public boolean treatmentFileRelease(String ebiFileName, String submissionCode, String accessionStudy, String studyCode,
			String creationUser) throws FatalException, BirdsException, UnsupportedEncodingException;

	void createXMLRelease(String submissionCode, String submissionDirectory, String studyCode) throws BirdsException, IOException, FatalException;

	void createXMLSubmission(String submissionCode, String submissionDirectory, String studyCode, String sampleCodes,
			String experimentCodes, String runCodes) throws IOException, FatalException, JSONDeviceException;
}
