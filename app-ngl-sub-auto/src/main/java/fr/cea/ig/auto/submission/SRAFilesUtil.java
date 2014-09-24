package fr.cea.ig.auto.submission;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class SRAFilesUtil {

	
	public static String getLocalDirectoryParameter(String directory, String[] filterExtensions)
	{
		String param = "";
		File dir = new File(directory);
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>) FileUtils.listFiles(dir, filterExtensions, true);
		//Get list extension
		Set<String> extensions = new HashSet<String>();
		for(File file : files){
			extensions.add(FilenameUtils.getExtension(file.getName()));
		}
		for(String ext : extensions){
			if(ext.equals("gz"))
				param+=directory+"/*.fastq.gz ";
			else
				param+=directory+"/*."+ext+" ";
		}
		
		return param;
	}
	
	public static void main(String[] args)
	{
		String[] extensions = new String[] { "fastq.gz", "sff" , "srf" };
		String param = SRAFilesUtil.getLocalDirectoryParameter("/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/test_15_04_2014",extensions);
		System.out.println("Param "+param);
	}
}
