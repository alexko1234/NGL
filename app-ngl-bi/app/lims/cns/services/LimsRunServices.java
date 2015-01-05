package lims.cns.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.list.TreeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import play.Logger.ALogger;
import lims.cns.dao.LimsExperiment;
import lims.cns.dao.LimsLibrary;
import lims.cns.dao.LimsAbandonDAO;
import lims.models.LotSeqValuation;
import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.experiment.illumina.Flowcell;
import lims.models.experiment.illumina.Library;
import lims.models.instrument.Instrument;
import lims.models.runs.TacheHD;
import lims.services.ILimsRunServices;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
//import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.dao.DAOException;


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

			if(firstTime){
				List<TacheHD> taches = dao.listTacheHD(readSet.code);
				Integer tacheId = null;
				if(taches.size() > 1){
					logger.error(readSet.code+" : Plusieurs Taches");
					//TODO mail to prod ???
				}else if(taches.size() == 1){
					tacheId = taches.get(0).tacco;
				}else{
					logger.error(readSet.code+" : O Tache");
				}
				
				try{
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), tacheId, 55);
					if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
						dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
					}
				}catch(Throwable t){  //in case of deadlock situation or other error we retry
					logger.warn(readSet.code+" : first : "+t.getMessage());
					dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), tacheId, 55);
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

}
