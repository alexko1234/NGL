package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import models.sra.submission.instance.Submission;

public class XmlServices {
	public void createXmlSubmission (Submission submission) {
		
	}
	   
	    public void testProcessBuilder() throws IOException {
	        String[] command = {"ls", "-al"}; // commande et option ou argument
	        ProcessBuilder processBuilder = new ProcessBuilder(command);

	        //You can set up your work directory
	        //processBuilder.directory(new File("test"));
	        Process process = processBuilder.start();
	        //Read out dir output
	        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;

	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }

	        //Wait to get exit value
	        try {
	            int exitValue = process.waitFor();
	            System.out.println("\n\nExit Value is " + exitValue);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    } 
}
