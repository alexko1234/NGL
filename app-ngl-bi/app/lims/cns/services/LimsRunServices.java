package lims.cns.services;


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

import lims.cns.dao.LimsAbandonDAO;
import lims.models.LotSeqValuation;
import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;
import lims.models.runs.TacheHD;
import lims.services.ILimsRunServices;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;


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
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public ContainerSupport getContainerSupport(String supportCode) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void valuationRun(Run run) {
		try{
			dao.updateRunAbandon(run.code, getAbandon(run.valuation, run.code), 47);
			for(Lane lane: run.lanes){
				dao.updatePisteAbandon(run.code, lane.number, getAbandon(lane.valuation, run.code), 47);
			}
		}catch(Throwable t){
			Logger.error(run.code+" : "+t.getMessage());
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
				dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), tacheId, 55);
				if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
					dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
				}
			}else{
				LotSeqValuation lsv = dao.getLotsequenceValuation(readSet.code);
				dao.updateLotsequenceAbandon(readSet.code, getSeqVal(readSet.productionValuation, readSet.code), getCR(readSet.productionValuation), lsv.tacco, 55);
				if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
					dao.updateLotsequenceAbandonBI(readSet.code, getAbandon(readSet.bioinformaticValuation, readSet.code));
				}
			}
		}catch(Throwable t){
			Logger.error(readSet.code+" : "+t.getMessage());
		}
	}


	private Integer getCR(Valuation valuation) {
		int score = 0;
		for(String cr : valuation.resolutionCodes){
			score += (crScoring.get(cr) != null)?crScoring.get(cr).intValue():0;
			
		}
		
		Integer crId = scoreMapping.get(score);
		Logger.debug("CR_ID= "+crId);
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
