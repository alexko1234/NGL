package sra.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.sra.submit.common.instance.Sample;
import models.utils.InstanceConstants;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;



public class RepriseHistoSample_extId extends AbstractScript {
	
	@Override
	public void execute() throws IOException, ParseException {
		Map <String, String> mapMois = new HashMap<>();
		mapMois.put("janv", "1");
		mapMois.put("f�vr", "2");
		mapMois.put("mars", "3");
		mapMois.put("avr" , "4");
		mapMois.put("mai" , "5");
		mapMois.put("juin", "6");
		mapMois.put("juil", "7");
		mapMois.put("ao�t", "8");
		mapMois.put("sept", "9");
		mapMois.put("oct" ,"10");
		mapMois.put("nov" ,"11");
		mapMois.put("d�c" ,"12");
		

		File ebiFile = new File("/env/cns/home/sgas/repriseHistoExtId/repriseHistoSample.csv");
		CSVParser csvParser = CSVParser.parse(ebiFile, Charset.forName("UTF-8"), CSVFormat.EXCEL.withDelimiter(';'));
		boolean first = true;
		int cp = 0;
		for (CSVRecord  record : csvParser) {
			if (first){
				first = false;
				continue;
			}
			cp++;
			// marche ssi pas de % dans args:
			//printf(csvRecord.get(0) + "  " + csvRecord.get(1) + "  " + csvRecord.get(4));
			// facon correcte d'ecrire
			//printf("%s  |  %s  |  %s  |  %s\n", record.get(0), record.get(1), record.get(3), record.get(4));
			String accession = record.get(0);
			String extId = record.get(1);
			String code = record.get(3);
			String oriDate = record.get(4);

			String [] tmp = oriDate.split("-");
			Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse(tmp[0]+ "/"+mapMois.get(tmp[1])+"/20"+tmp[2]);
			//print(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
			//Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse("1/1/18");
			printf("%s  |  %s  |  %s  |  %s", accession, extId, code, date);

			Date today = new Date();

			// consideres dans la base comme externalSample ???? revoir reprise histo ???
			
			if (accession.equals("ERS506046")){
			//	continue;
			}
			if (accession.equals("ERS905354")){
				//continue;
			}
			if (accession.equals("ERS1092158")){
				//continue;
			}
			
			
			Sample sample = MongoDBDAO.findOne(InstanceConstants.SRA_SAMPLE_COLL_NAME,
					Sample.class, DBQuery.and(DBQuery.is("accession", accession)));
			
			if (sample == null) {
				printf("***************ERREUR pour le sample %d avec code=%s et accession =%s qui n'existe pas dans base", cp, code, accession);
				continue;
			}
			
			if (sample._type.equals("ExternalSample")) {
				printf("***************ERREUR pour le sample %d avec code=%s et accession =%s qui existe dans base comme ExternalSample", cp, code, accession);
				continue;
			} 
			
			if (accession.equals("ERS506046")){
				printf("************Dans base: accession=%s,code=%s,_type=%s,  ", sample.accession, sample.code,sample._type);
		    }
			MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, 
					DBQuery.is("accession", accession),
					DBUpdate.set("externalId", extId).set("traceInformation.creationDate", date).set("traceInformation.modifyDate", today));
			
			if (sample != null) {
				printf("update ok  " + cp);
			}
			
			
		}
		
	}
	
	@Override
	public LogLevel logLevel() {
		return LogLevel.Debug;
	}
	
}
