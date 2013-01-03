package lims.cng.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lims.cng.dao.LimsExperiment;
import lims.cng.dao.LimsLibrary;
import lims.cng.dao.LimsRunDAO;
import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.experiment.illumina.Flowcell;
import lims.models.experiment.illumina.Lane;
import lims.models.experiment.illumina.Library;
import lims.models.instrument.Instrument;
import lims.services.ILimsRunServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;

/**
 * CNG Implementation
 */
@Service
public class LimsRunServices implements ILimsRunServices {

	@Autowired
	LimsRunDAO dao;

	@Override
	public List<Instrument> getInstruments() {
		return dao.getInstruments();
	}

	@Override
	public Experiment getExperiments(Experiment experiment) {
		LimsExperiment limsExp = dao.getExperiments(experiment);
		Experiment exp = new Experiment();
		exp.date = limsExp.date;
		exp.containerSupportCode = experiment.containerSupportCode;
		exp.instrument = new Instrument();
		exp.instrument.code = limsExp.code;
		exp.instrument.categoryCode = limsExp.categoryCode;	
		Logger.debug(limsExp.toString());		
		return exp;
	}

	@Override
	public ContainerSupport getContainerSupport(String barCode) {
		List<LimsLibrary> limsReadSets = dao.geContainerSupport(barCode);
		Flowcell flowcell = null;
		if (limsReadSets.size() > 0) {
			flowcell = new Flowcell();
			flowcell.containerSupportCode = barCode;

			Map<Integer, Lane> lanes = new HashMap<Integer, Lane>();

			for (LimsLibrary lrs : limsReadSets) {
				Lane currentLane = lanes.get(lrs.laneNumber);
				if (null == currentLane) {
					currentLane = new Lane();
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
				if(null != lrs.indexName)lib.isIndex = Boolean.TRUE;
				currentLane.librairies.add(lib);
			}
			flowcell.lanes = lanes.values();
		}
		return flowcell;
	}
	
	
}
