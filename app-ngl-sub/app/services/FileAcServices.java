package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import java.util.Date;

import models.sra.experiment.instance.Experiment;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

public class FileAcServices {

	public Submission traitementFileAC(String submissionCode, File ebiFileAc) throws IOException, SraException {
		if ((submissionCode == null)|| (ebiFileAc == null)) {
			throw new SraException("traitementFileAC :: parametres d'entree à null" );
		}
		Submission submission = null;
		BufferedReader inputBuffer = null;
		try {
			inputBuffer = new BufferedReader(new FileReader(ebiFileAc));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String lg = null;
		String ligne;
		while ((lg = inputBuffer.readLine()) != null) {
			if (lg.startsWith("<?")){
				// ignorer
			} else {
				Boolean resultAC = false;
				System.out.println("ligne = '"+ lg+"'");
				String pattern_string = "<RECEIPT\\s+receiptDate=\"(\\S+)\"\\s+submissionFile=\"(\\S+)\"\\s+success=\"true\"";
				java.util.regex.Pattern pattern = Pattern.compile(pattern_string);
				Matcher m = pattern.matcher(lg);
				if ( ! m.find() ) {
					System.out.println("Erreur : Envoie d'un mail à admin");
					// mettre status à jour
					return submission;
				} 
				System.out.println("Traitement des AC :");
				//System.out.println("lg :'"+lg+"'");

				String [] tab = lg.split(">");
				String patternAc = "<(\\S+)\\s+accession=\"(\\S+)\"\\s+alias=\"(\\S+)\"";
				java.util.regex.Pattern pAc = Pattern.compile(patternAc);
				Map<String, String> mapStudy = new HashMap<String, String>(); 
				Map<String, String> mapSamples = new HashMap<String, String>(); 
				Map<String, String> mapExperiments = new HashMap<String, String>(); 
				Map<String, String> mapRuns = new HashMap<String, String>(); 
				Map<String, String> mapSubmission = new HashMap<String, String>();
				String ebiSubmissionCode = null;
				String submissionAc = null;
				String studyCode = null;
				String studyAc = null;
				for(String info : tab) {
					System.out.println(info);
					Matcher mAc = pAc.matcher(info);
					// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
					if ( ! mAc.find() ) {
						// autre ligne que AC.
					} else {
						//System.out.println("type='"+mAc.group(1)+"', accession='"+mAc.group(2)+"', alias='"+ mAc.group(3)+"'" );
						if (mAc.group(1).equalsIgnoreCase("RUN")){
							System.out.println("insertion dans mapRun");
							mapRuns.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("EXPERIMENT")){
							System.out.println("insertion dans mapExperiment");
							mapExperiments.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("SAMPLE")){
							System.out.println("insertion dans mapSample");
							mapSamples.put(mAc.group(3), mAc.group(2));
						} else if (mAc.group(1).equalsIgnoreCase("STUDY")){
							studyCode = mAc.group(3);
							studyAc = mAc.group(2);
							System.out.println("insertion dans mapStudy:"+studyAc);
						} else if (mAc.group(1).equalsIgnoreCase("SUBMISSION")){
							ebiSubmissionCode = mAc.group(3);
							submissionAc =  mAc.group(2);
							System.out.println("insertion dans mapSubmission:"+submissionAc);
						} else {
								
						}
					}
				}
			
				// Mise à jour des objets :
				// Recuperer l'objet submission :
				if (ebiSubmissionCode == null) {
					throw new SraException("Erreur pour la soumission " + submissionCode );
				}
				if (! ebiSubmissionCode.equals(submissionCode)) {
					throw new SraException("Incoherence entre parametre submissionCode (" 
				+ submissionCode + ") et ebiSubmissionCode (" + ebiSubmissionCode + ") dans le fichier " + ebiFileAc);
				}
				//Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class, submissionCode);
				// Met à jour tous les submissions ayant le code indique avec la valeur accession indiquee et la valeur traceInformation.modifyUser indiquee...:
				MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
						DBQuery.is("code", submissionCode),
						DBUpdate.set("accession", submissionAc).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	
				if (studyCode != null) {
					MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
							DBQuery.is("code", studyCode),
							DBUpdate.set("accession", studyAc).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));
				}
				for(Entry<String, String> entry : mapSamples.entrySet()) {
				    String code = entry.getKey();
				    String ac = entry.getValue();
				    MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
				    	DBQuery.is("code", code),
						DBUpdate.set("accession", ac).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 		
				}
				for(Entry<String, String> entry : mapExperiments.entrySet()) {
				    String code = entry.getKey();
				    String ac = entry.getValue();
				    MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
				    	DBQuery.is("code", code),
						DBUpdate.set("accession", ac).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 		
				}
				
				// verifier dans l'objet submission que tous les codes d'objets ont bien un AC.
				// mettre a jour status
				
			}
		}
		return submission;
	}

}
