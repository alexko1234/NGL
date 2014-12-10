package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileAcServices {

	public Boolean traitementFileAC(File ebiFileAc) throws IOException {
		BufferedReader inputBuffer = null;
		try {
			inputBuffer = new BufferedReader(new FileReader(ebiFileAc));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String lg = null;
		
		while ((lg = inputBuffer.readLine()) != null) {
			if (lg.startsWith("<?")){
				// ignorer
			} else {
				//String formatLg = lg.replaceAll("\\/>", "\\n");
				String [] tab = lg.split(">");
				Boolean resultAC = false;
				System.out.println("ligne = '"+ lg+"'");
				
				String pattern_string = "<(\\S+)\\s+accession=\"(\\S+)\"\\s+alias=\"(\\S+)\"";
				java.util.regex.Pattern p = Pattern.compile(pattern_string);

				for(String info : tab) {
					System.out.println(info);
	//				if (info.matches("<RECEIPT\\s+receiptDate=\"\\S+\"\\s+submissionFile=\"\\S+\"\\s+success=\"(\\S+)\"")){
					
					
					
					Matcher m = p.matcher(info);
					// Appel de find obligatoire pour pouvoir récupérer $1 ...$n
					if ( ! m.find() ) {
						// declencher une erreur:
					} else {
						System.out.println("type='"+m.group(1)+"', accession='"+m.group(2)+"', alias='"+ m.group(3)+"'" );
					}
				}
			}
		}
		return true;
		
	}

}
