package lims.cns.services;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lims.cns.dao.LimsAbandonDAO;
import lims.cns.dao.LimsExperiment;
import lims.cns.dao.LimsLibrary;
import lims.models.LotSeqValuation;
import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.experiment.illumina.BanqueSolexa;
import lims.models.experiment.illumina.DepotSolexa;
import lims.models.experiment.illumina.Flowcell;
import lims.models.experiment.illumina.Library;
import lims.models.instrument.Instrument;
import lims.models.runs.ResponProjet;
import lims.models.runs.TacheHD;
import lims.services.ILimsRunServices;
import mail.MailServiceException;
import mail.MailServices;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
//import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.Logger.ALogger;
import play.Play;
import fr.cea.ig.MongoDBDAO;


@Service
public class LimsRunServices implements ILimsRunServices{

	@Autowired
	LimsAbandonDAO dao;

	ALogger logger = Logger.of("CNS");

	private Map<String, Integer> crScoring;
	private Map<Integer, Integer> scoreMapping;

	/*
	 *
Conta:mat ori        					9	TAXO-contaMatOri
Qlte:duplicat>30    					42	Qlte-duplicat
Qlte:repartition bases       			41	Qlte-repartitionBases

Conta mat ori + duplicat>30				43	TAXO-contaMatOri ; Qlte-duplicat
Conta mat ori + rep bases				44	TAXO-contaMatOri ; Qlte-repartitionBases
Duplicat>30 + rep bases					45	Qlte-duplicat ; Qlte-repartitionBases
Conta mat ori + duplicat>30 + rep bases	46	TAXO-contaMatOri ; Qlte-duplicat ; Qlte-repartitionBases


	 */

	public LimsRunServices() {
		crScoring = new HashMap<String, Integer>();
		crScoring.put("TAXO-contaMatOri", 1);
		crScoring.put("Qlte-duplicat", 2);
		crScoring.put("Qlte-repartitionBases", 4);

		scoreMapping = new HashMap<Integer, Integer>();
		scoreMapping.put(1, 9);
		scoreMapping.put(2, 42);
		scoreMapping.put(4, 41);
		scoreMapping.put(3, 43);
		scoreMapping.put(5, 44);
		scoreMapping.put(6, 45);
		scoreMapping.put(7, 46);
	}

	@Override
	public List<Instrument> getInstruments() {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public Experiment getExperiments(Experiment experiment) {
		//NGL
		List<models.laboratory.experiment.instance.Experiment> nglExps =  MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, models.laboratory.experiment.instance.Experiment.class, 
				DBQuery.is("typeCode", "illumina-depot").in("inputContainerSupportCodes", experiment.containerSupportCode)).toList();
		if(nglExps.size() == 1){
			
			models.laboratory.experiment.instance.Experiment nglExp = nglExps.get(0);
			Experiment exp = new Experiment();
			exp.containerSupportCode = experiment.containerSupportCode;
			exp.instrument = new Instrument();
			exp.instrument.code = nglExp.instrument.code;
			exp.instrument.categoryCode = nglExp.instrument.typeCode;
			
			if(nglExp.experimentProperties.containsKey("runStartDate")){
				exp.date = (Date)nglExp.experimentProperties.get("runStartDate").value;
				
			}else{
				exp.date = nglExp.traceInformation.creationDate;
			}
			
			exp.nbCycles = (Integer)nglExp.instrumentProperties.get("nbCyclesRead1").value
						+ (Integer)nglExp.instrumentProperties.get("nbCyclesRead2").value
						+ (Integer)nglExp.instrumentProperties.get("nbCyclesReadIndex1").value
						+ (Integer)nglExp.instrumentProperties.get("nbCyclesReadIndex2").value;
					
			//exp.date = limsExp.date; //runStartDate
			//exp.nbCycles = limsExp.nbCycles; //instrument
			
			return exp;
		}else if(nglExps.size() > 1){
			return null;
		}	else{
			//old lims
			List<LimsExperiment> limsExps = dao.getExperiments(experiment);
			if(limsExps.size() == 1){
				LimsExperiment limsExp = limsExps.get(0);
				Experiment exp = new Experiment();
				exp.date = limsExp.date;
				exp.containerSupportCode = experiment.containerSupportCode;
				exp.instrument = new Instrument();
				exp.instrument.code = limsExp.code;
				exp.instrument.categoryCode = getInstrumentCategoryCode(exp);
				exp.nbCycles = limsExp.nbCycles;
				Logger.debug(limsExp.toString());		
				return exp;
			}else{
				return null;
			}
		}
	}

	private String getInstrumentCategoryCode(Experiment exp) {
		try {
			return models.laboratory.instrument.description.Instrument.find.findByCode(exp.instrument.code).typeCode;
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContainerSupport getContainerSupport(String supportCode) {
		List<LimsLibrary> limsReadSets = dao.geContainerSupport(supportCode);
		Flowcell flowcell = null;
		if (limsReadSets != null && limsReadSets.size() > 0) {
			flowcell = new Flowcell();
			flowcell.containerSupportCode = supportCode;

			Map<Integer, lims.models.experiment.illumina.Lane> lanes = new HashMap<Integer, lims.models.experiment.illumina.Lane>();

			for (LimsLibrary lrs : limsReadSets) {
				lims.models.experiment.illumina.Lane currentLane = lanes.get(lrs.laneNumber);
				if (null == currentLane) {
					currentLane = new lims.models.experiment.illumina.Lane();
					currentLane.number = lrs.laneNumber;
					currentLane.librairies = new ArrayList<Library>();
					lanes.put(lrs.laneNumber, currentLane);
				}

				Library lib = new Library();
				lib.sampleContainerCode = lrs.sampleBarCode;
				lib.sampleCode = lrs.sampleCode;
				lib.tagName = lrs.indexName;
				lib.tagSequence = lrs.indexSequence;
				lib.projectCode = lrs.projectCode;
				lib.insertLength = lrs.insertLength;
				lib.typeCode = lrs.experimentTypeCode;
				if(null != lrs.indexName && lrs.indexTypeCode != 3)lib.isIndex = Boolean.TRUE;
				else if(null != lrs.indexName)lib.isIndex = Boolean.FALSE;
				currentLane.librairies.add(lib);
			}
			flowcell.lanes = lanes.values();
		}
		return flowcell;
	}

	@Override
	public void valuationRun(Run run) {
		try{
			dao.updateRunAbandon(run.code, getAbandon(run.valuation, run.code), 47);
			for(Lane lane: run.lanes){
				dao.updatePisteAbandon(run.code, lane.number, getAbandon(lane.valuation, run.code), 47);
			}
		}catch(Throwable t){
			logger.error(run.code+" : "+t.getMessage());
		}
	}
	@Override
	public void valuationReadSet(ReadSet readSet, boolean firstTime) {
		try{
			
			//TODO send mail
			sendMailAgirs(readSet);
			
			Logger.info("valuationReadSet : "+readSet.code+" / "+firstTime);
			if(firstTime){
				List<TacheHD> taches = dao.listTacheHD(readSet.code);
				Integer tacheId = null;
				Integer cptreco = null;
				if(taches.size() > 1){
					logger.error(readSet.code+" : Plusieurs Taches");					
				}else if(taches.size() == 1){
					tacheId = taches.get(0).tacco;
				}else{
					LotSeqValuation lsv = dao.getLotsequenceValuation(readSet.code);
					if(null != lsv && null != lsv.tacco){
						tacheId = lsv.tacco;
						cptreco = lsv.cptreco;
					}else{
						logger.error(readSet.code+" : O Tache");
					}					
				}
				
				if(null == cptreco || cptreco == 47){ //used to manage history recovery
					cptreco = getCR(readSet.productionValuation);
				}
				
				try{
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), cptreco, tacheId, 55);
					if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
						dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
					}
				}catch(Throwable t){  //in case of deadlock situation or other error we retry
					logger.warn(readSet.code+" : first : "+t.getMessage());
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), cptreco, tacheId, 55);
					if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
						dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
					}
				}
				
			}else{
				try{
					LotSeqValuation lsv = dao.getLotsequenceValuation(readSet.code);
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), lsv.tacco, 55);
					if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
						dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
					}
				}catch(Throwable t){ //in case of deadlock situation or other error we retry
					logger.warn(readSet.code+" : second : "+t.getMessage());
					LotSeqValuation lsv = dao.getLotsequenceValuation(readSet.code);
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), lsv.tacco, 55);
					if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
						dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
					}
				}
				
			}
		}catch(Throwable t){
			logger.error(readSet.code+" : "+t.getMessage());
		}
	}


	private void sendMailAgirs(ReadSet readSet) throws MailServiceException {
		
		if(!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("runCode", readSet.runCode).notIn("state.code", "A", "UA"))){
			
			String biurl = "http://ngl-bi.genoscope.cns.fr";
			
			List<ReadSet> readsets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.is("runCode", readSet.runCode), getReadSetKeys()).toList();
			
			Map<String, List<ReadSet>> mReadSets = readsets.stream()
					 .collect(Collectors.groupingBy((ReadSet r) -> r.projectCode));
			
			StringBuffer message = new StringBuffer();
			message.append("<html>");
			message.append("<div>Bonjour,</br>"
					+ "</br>Le run <a href='"+biurl+"/runs/"+readSet.runCode+"'>"+readSet.runCode+"</a> a entièrement été évalué.</br>"
					+"</br>Vous trouverez ci-dessous les readsets qui le composent classés par projet.</br>"
					+"N'hésitez pas à cliquer sur le nom d'un readset pour voir le détails de ses traitements."
					+ "</div>");
			message.append("<h3 style='text-decoration: underline;'>").append(readSet.runCode).append("</h3>");
			
			for(String key : mReadSets.keySet()){				
				ResponProjet rp = dao.getResponProjet(key);				
				message.append("<h4 style='text-decoration: underline;'>Projet : ").append(key).append("</h4>");
				message.append("<div style='color:green;'>").append(rp.name).append("</div>");
				message.append("<div style='color:black;'>").append(rp.biomanager).append("</div>");
				message.append("<div style='color:black;'>").append(rp.infomanager).append("</div>").append("</br>");				
				mReadSets.get(key).forEach((ReadSet r) -> message.append("<a href='"+biurl+"/readsets/"+r.code+"'>").append(r.code).append("</a></br>"));
				message.append("</br>");
			}
			message.append("</br>Merci et à bientôt sur <a href='"+biurl+"'>NGL-BI</a> !");
			message.append("</html>");
			
			String alertMailExp = Play.application().configuration().getString("validation.mail.from"); 
			String alertMailDest = Play.application().configuration().getString("validation.mail.to");    	
			MailServices mailService = new MailServices();
			Set<String> destinataires = new HashSet<String>();
			destinataires.addAll(Arrays.asList(alertMailDest.split(",")));
			mailService.sendMail(alertMailExp, destinataires, "[NGL-BI] Run Evaluation Terminée : "+readSet.runCode, message.toString());
		}				
	}

	private static BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
	}

	
	private Integer getCR(Valuation valuation) {
		int score = 0;
		if(valuation.resolutionCodes !=null){
			for(String cr : valuation.resolutionCodes){
				score += (crScoring.get(cr) != null)?crScoring.get(cr).intValue():0;

			}
		}
		Integer crId = scoreMapping.get(score);
		return (crId != null) ? crId : 47;		
	}

	private Integer getAbandon(Valuation valuation, String code) {
		if(TBoolean.FALSE.equals(valuation.valid)){
			return 1; //abandon=true
		}else if(TBoolean.TRUE.equals(valuation.valid)){
			return 0; //abandon = false;
		}else{
			throw new RuntimeException("Abandon : Mise à jour abandon run ou readset ("+code+") dans lims mais valuation à UNSET");
		}
	}

	private Integer getSeqVal(Valuation valuation, String code) {
		if(TBoolean.FALSE.equals(valuation.valid)){
			return 0; //a abandonner
		}else if(TBoolean.TRUE.equals(valuation.valid)){
			return 1; //valide;
		}else{
			return 2;
		}
	}

	@Override
	public void insertRun(Run run, List<ReadSet> readSets, boolean deleteBeforeInsert) {
		try{
			
			if(deleteBeforeInsert){
				try{
					dao.deleteRun(run.code);
				} catch(Throwable t){
					
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			DepotSolexa ds = null;
			ds=dao.getDepotSolexa(run.containerSupportCode, sdf.format(run.sequencingStartDate));
			if(ds==null){
				ds=insertFlowcellNGL(run);
			}
			if(null != ds){
				Map<String, BanqueSolexa> mapBanques = new HashMap<String, BanqueSolexa>();
				for(BanqueSolexa banque:  dao.getBanqueSolexa(run.containerSupportCode)){
					String key = banque.prsco+"_"+banque.adnnom+"_"+banque.lanenum+"_"+banque.tagkeyseq;
					//Logger.debug("key banque = "+key);
					mapBanques.put(key, banque);
				}
				if(mapBanques.size()==0){
					for(BanqueSolexa banque:  dao.getBanqueSolexaFlowcellNGL(run.containerSupportCode)){
						String key = banque.prsco+"_"+banque.adnnom+"_"+banque.lanenum+"_"+banque.tagkeyseq;
						Logger.debug("key banque = "+key);
						mapBanques.put(key, banque);
					}
				}
				Map<String, ReadSet> mapReadSets = new HashMap<String, ReadSet>();
				for(ReadSet readSet:  readSets){
					String index = (readSet.code.contains("."))?readSet.code.split("\\.")[1]:"";
					String key = readSet.sampleCode+"_"+readSet.laneNumber+"_"+index;
					//Logger.debug("key readSet = "+key);
					mapReadSets.put(key, readSet);
					
					if(!mapBanques.containsKey(key)){
						throw new RuntimeException("ReadSet "+readSet.code+" not found in lims");
					}
				}
				Logger.debug("banques.size() != readSets.size() "+mapBanques.size()+" / "+mapReadSets.size());
				readSets = null;
				if(mapBanques.size() != mapReadSets.size()){
					throw new RuntimeException("banques.size() != readSets.size() "+mapBanques.size()+" / "+mapReadSets.size());
				}
				
				Logger.debug("Load DepotSolexa = "+ds);
				//Delete run if exist ???
				
				dao.insertRun(run, ds);
				dao.insertLanes(run.lanes, ds);
				for(Map.Entry<String, ReadSet> entry : mapReadSets.entrySet()){
					dao.insertReadSet(entry.getValue(), mapBanques.get(entry.getKey()));
					dao.insertFiles(entry.getValue(), false);
				}
				
				dao.dispatchRun(run);
				dao.updateRunInNGL(run);
				//passe l'etat à traite
				dao.updateRunEtat(run, 2);
			}else{
				throw new RuntimeException("DepotSolexa is null");
			}
	    	//TODO Etat
	    	//TODO RunInNGL
		
		}catch(Throwable t){
			logger.error("Synchro RUN : "+run.code+" : "+t.getMessage(),t);
		}
	}

	@Override
	public void updateReadSetAfterQC(ReadSet readset) {
		try{
			dao.updateReadSetEtat(readset, 2);
			dao.updateReadSetBaseUtil(readset);
			dao.insertFiles(readset, true);
			
		}catch(Throwable t){
			logger.error("Synchro READSET AfterQC: "+readset.code+" : "+t.getMessage(),t);
		}
	}

	@Override
	public void updateReadSetArchive(ReadSet readset) {
		try{
			dao.updateReadSetArchive(readset);
			
		}catch(Throwable t){
			logger.error("Synchro READSET Archive: "+readset.code+" : "+t.getMessage(),t);
		}
	}
	
	public void linkRunWithMaterielManip(){
		try{
			dao.linkRunWithMaterielManip();
			
		}catch(Throwable t){
			logger.error("Synchro LINK RUN / MATERIEL_MANIP: "+t.getMessage(),t);
		}
	}
	
	
	public DepotSolexa insertFlowcellNGL(Run run){
		List<models.laboratory.experiment.instance.Experiment> expPrepaflowcell=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, models.laboratory.experiment.instance.Experiment.class,DBQuery.in("outputContainerSupportCodes", run.containerSupportCode).is("typeCode", "prepa-flowcell")).toList();
		if(CollectionUtils.isEmpty(expPrepaflowcell)){
			throw new RuntimeException("Prepaflowcell Experiment with containerOutPut "+run.containerSupportCode+" not found in NGL");
		}
		
		List<models.laboratory.experiment.instance.Experiment> expDepotIllumina=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, models.laboratory.experiment.instance.Experiment.class,DBQuery.in("inputContainerSupportCodes", run.containerSupportCode).is("typeCode", "illumina-depot")).toList();
		if(CollectionUtils.isEmpty(expDepotIllumina)){
			throw new RuntimeException("DepotIllumina Experiment with containerOutPut "+run.containerSupportCode+" not found in NGL");
		}

		//Create Manip FlowcellNGL
			DepotSolexa ds=dao.insertFlowcellNGL(expPrepaflowcell.get(0),expDepotIllumina.get(0));
			return ds;

	}
}
