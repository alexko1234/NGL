package lims.dummy.services;

import java.util.List;

import lims.models.experiment.ContainerSupport;
import lims.models.experiment.Experiment;
import lims.models.instrument.Instrument;
import lims.services.ILimsRunServices;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;

public class LimsRunServices implements ILimsRunServices {

	@Override
	public List<Instrument> getInstruments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Experiment getExperiments(Experiment experiment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerSupport getContainerSupport(String supportCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void valuationRun(Run run) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valuationReadSet(ReadSet readSet, boolean firstTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertRun(Run run, List<ReadSet> readSets,
			boolean deleteBeforeInsert) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateReadSetAfterQC(ReadSet readSet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateReadSetEtat(ReadSet readset, int etat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateReadSetArchive(ReadSet readset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void linkRunWithMaterielManip() {
		// TODO Auto-generated method stub

	}

}
