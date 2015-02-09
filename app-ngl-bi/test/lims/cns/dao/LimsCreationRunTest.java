package lims.cns.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lims.cns.services.LimsRunServices;
import lims.models.experiment.illumina.BanqueSolexa;
import lims.models.experiment.illumina.DepotSolexa;
import lims.models.runs.EtatTacheHD;
import lims.models.runs.TacheHD;
import models.laboratory.common.instance.property.PropertyFileValue;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import fr.cea.ig.MongoDBDAO;
import play.Logger;
import play.api.libs.Files;
import play.api.modules.spring.Spring;
import play.libs.XML;
import play.libs.XPath;
import utils.AbstractTestsCNG;
import utils.AbstractTestsCNS;

public class LimsCreationRunTest extends AbstractTestsCNS {
	
	
	private static final String RUN_CODE = "UNIT_TEST";

	@BeforeClass
	public static void getRunData()
	{
		try{
			Spring.getBeanOfType(LimsAbandonDAO.class).deleteRun(RUN_CODE);
		}catch(Throwable t){
			Logger.error(t.getMessage());
			
		}
	}
	
	
	public void getTacheHD() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
			assertNotNull(dao);
			List<TacheHD> taches = dao.listTacheHD("20626");
			assertTrue(taches.size() == 0);
		}
	}
	@Test
	public void getEtatTacheHD() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
			assertNotNull(dao);
			List<EtatTacheHD> etaches = dao.listEtatTacheHD();
			Logger.debug("Nb Etat tache = "+etaches.size());
			assertTrue(etaches.size() > 0);
		}
		
	}
	
	@Test
	public void getDepotSolexa() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
			assertNotNull(dao);
			DepotSolexa ds = dao.getDepotSolexa("A7V8W", "02/06/14");
			assertNotNull(ds);
			assertNotNull(ds.matmaco);
			Logger.debug("DepotSolexa = "+ds);			
		}
		
	}
	
	@Test
	public void getBanqueSolexa() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
			assertNotNull(dao);
			List<BanqueSolexa> ds = dao.getBanqueSolexa("A7V8W");
			assertNotNull(ds);
			Logger.debug("BanqueSolexa = "+ds);			
		}
		
	}
	@Test
	public void convertRunToRunSolexa() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
			assertNotNull(dao);
			DepotSolexa ds = dao.getDepotSolexa("A7V8W", "02/06/14");
			assertNotNull(ds);
			assertNotNull(ds.matmaco);
			
			Run run = MongoDBDAO.findByCode("ngl_bi.RunIllumina_initData", Run.class, "140602_MELISSE_A7V8W");
			
			dao.convertRunToRunSolexa(run, ds);
		}
		
	}
	
	@Test
	public void insertRunSolexa() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			
			LimsRunServices  lrs = Spring.getBeanOfType(LimsRunServices.class);
			assertNotNull(lrs);
			
			Run run = MongoDBDAO.findByCode("ngl_bi.RunIllumina_initData", Run.class, "140703_PHOSPHORE_C3MGGACXX");
			List<ReadSet> readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_initData", ReadSet.class, DBQuery.is("runCode", run.code)).toList();
			
			for(ReadSet readSet : readSets){
				readSet.runCode = RUN_CODE;
				for(File file: readSet.files){
					file.extension += "x";
				}
			}
			
			assertNotNull(run);
			run.code = RUN_CODE;
			lrs.insertRun(run, readSets, false);			
		}
		
	}
	
	@Test
	public void insertRunSolexaComplete() {
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			
			LimsRunServices  lrs = Spring.getBeanOfType(LimsRunServices.class);
			assertNotNull(lrs);
			/*
			(
					--'141127_SOUFRE_C4VN3ACXX',
					--'141203_PHOSPHORE_C4UJUACXX')
				
					--'141208_FLUOR_HAY78ADXX',
					--'141211_FLUOR_HAYJVADXX',
					--'141211_FLUOR_HAYK5ADXX',
					--'141216_FLUOR_HAYJ5ADXX',
					--'141216_FLUOR_HAYK8ADXX')
					--'141215_MELISSE_AC12E')
					--'141217_MELISSE_AC117',
					--'141218_MIMOSA_AC1LP')
					*/
			/*
			String[] runcodes = new String[]{"141127_SOUFRE_C4VN3ACXX","141203_PHOSPHORE_C4UJUACXX","141208_FLUOR_HAY78ADXX",
					"141211_FLUOR_HAYJVADXX","141211_FLUOR_HAYK5ADXX","141216_FLUOR_HAYJ5ADXX","141216_FLUOR_HAYK8ADXX","141215_MELISSE_AC12E",
					"141217_MELISSE_AC117","141218_MIMOSA_AC1LP"};
			
			List<Run> runs = MongoDBDAO.find("ngl_bi.RunIllumina", Run.class, DBQuery.in("code", runcodes)).toList();
			
			for(Run run:runs){
			
				//Run run = MongoDBDAO.findByCode("ngl_bi.RunIllumina", Run.class, "141216_FLUOR_HAYK8ADXX");
				List<ReadSet> readSets = MongoDBDAO.find("ngl_bi.ReadSetIllumina", ReadSet.class, DBQuery.is("runCode", run.code)).toList();
				
				assertNotNull(run);
				
				//RG
				lrs.insertRun(run, readSets, true);	
				
				//QC
				
				for(ReadSet rs : readSets){
					lrs.updateReadSetAfterQC(rs);
				}
				
				//Valuation
				
				lrs.valuationRun(run);
				
				for(ReadSet rs : readSets){
					lrs.valuationReadSet(rs,true);
					lrs.updateReadSetArchive(rs);
				}
				
			}
			*/
			//Très long à ne faire que rarement
			//lrs.linkRunWithMaterielManip();
		}
		
	}
	@Test
	public void compareDatarun() throws Exception{
		if (play.Play.application().configuration().getString("institute").equals("CNS")) {
			java.io.File fBefore = new java.io.File("H:/Windows/Desktop/ngsrgtests/before.txt");
			java.io.File fAfter = new java.io.File("H:/Windows/Desktop/ngsrgtests/after.txt");
			
			BufferedReader brB = new BufferedReader(new FileReader(fBefore));
			BufferedReader brA = new BufferedReader(new FileReader(fAfter));
			
			String[] headerB = brB.readLine().trim().split("[\\t\\s]+");
			String[] headerA = brA.readLine().trim().split("[\\t\\s]+");
			
			if(headerA.length != headerB.length)throw new RuntimeException("Pb Header Number");
			
			/*
			for(int i = 0 ; i <  headerB.length; i++){
				if(!headerB[i].equals(headerA[i]))throw new RuntimeException("Pb Header Label "+i);
			}
			*/
			
			Logger.debug(Arrays.toString(headerB));
			Logger.debug(Arrays.toString(headerA));
			brB.readLine();brA.readLine();
			String lineB, lineA, runCode="";
			while ((lineB = brB.readLine()) != null && (lineA = brA.readLine()) != null) {
			    
			   String[] arrayB = lineB.trim().split("[\\t\\s]+");
			   
			   if(!runCode.equals(arrayB[0])){
				   runCode = arrayB[0];
				   Logger.debug("RunCode = "+runCode);
			   }
			   
			   
			  // Logger.debug(Arrays.toString(arrayB));
			   String[] arrayA = lineA.trim().split("[\\t\\s]+");
			   FileLine listB = new FileLine(headerB, arrayB);
			   FileLine listA = new FileLine(headerA, arrayA);
			   
			   if(arrayA.length != arrayB.length && (arrayA.length != headerA.length || arrayB.length != headerB.length)){
				   Logger.debug(lineA);
				   Logger.debug(lineB);
				   
			   }
			   
			   /*
			   Logger.debug(Arrays.toString(arrayB));
			   Logger.debug(Arrays.toString(arrayA));
			   
			   for(int i = 0 ; i < headerB.length -1 ; i++){
				   if(!listB.map.get(headerB[i]).equals(listA.map.get(headerA[i]))){
					   Logger.debug("not equals "+headerB[i]+" / "+listB.map.get(headerB[i])+" / "+listA.map.get(headerA[i]));
				   }
			   }
			   */
			   MapDifference<String,String> diff = Maps.difference(listB.map, listA.map);
			   Logger.debug(diff.entriesDiffering().toString());
			   //	break;		   
			}
			
			brB.close();brA.close();
			
			
			
		}
	}
	
	@Test
	public void testTaxo() throws Exception{
		List<ReadSet> readsets = MongoDBDAO.find("ngl_bi.ReadSetIllumina_initData", ReadSet.class, DBQuery.is("code", "BCM_CFIOSW_4_C3MGGACXX.IND8")).toList();
		//List<ReadSet> readsets = MongoDBDAO.find("ngl_bi.ReadSetIllumina", ReadSet.class, DBQuery.is("code", "BII_ACQCOSW_3_C4VMAACXX.IND4")).toList();
		//List<ReadSet> readsets = MongoDBDAO.find("ngl_bi.ReadSetIllumina", ReadSet.class, DBQuery.is("code", "BFY_ADOSZ_1_AC093.IND5")).toList();
		for(ReadSet rs: readsets){
			String krona = new String(((PropertyFileValue)rs.treatments.get("taxonomy").results.get("read1").get("krona")).value);
			//Logger.debug("krona = "+krona);
			//Logger.debug(krona.indexOf("Fungi")+"");
			//Document dom = XML.fromString("");
			
			//Node node = XPath.selectNode("//node[@name='Fungi']", dom);			
			Pattern p = Pattern.compile(".*<node name=\"Fungi\">\\s+<magnitude><val>(\\d+)</val></magnitude>.*", Pattern.DOTALL);
			
			Matcher m = p.matcher(krona);
			if(m.matches()){
				Logger.debug("Group = "+m.group(1));
			}
			
			/*
			if(null != node){
				Logger.debug(node.toString());
			}else{
				Logger.debug("node is null");
			}
			*/
		}
		
	}
}
