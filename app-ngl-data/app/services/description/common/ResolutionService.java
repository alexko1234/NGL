package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;

public class ResolutionService {
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		saveResolutionCategories(errors);	
		saveResolutions(errors);	
	}
		
	public static void saveResolutionCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ResolutionCategory> l = new ArrayList<ResolutionCategory>();
		
		for (ResolutionCategory.CODE code : ResolutionCategory.CODE.values()) {
			l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, code.name(), code.name()));
		}
		DAOHelpers.saveModels(ResolutionCategory.class, l, errors);
	}
	
	public static void saveResolutions(Map<String,List<ValidationError>> errors) throws DAOException{
		List<Resolution> l = new ArrayList<Resolution>();
				
		l.add(newResolution("reso1","exp_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		l.add(newResolution("reso2","exp_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		l.add(newResolution("reso3","exp_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		
		l.add(newResolution("reso1","pro_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name())));
		l.add(newResolution("reso2","pro_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name())));
		l.add(newResolution("reso3","pro_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name())));
		
		l.add(newResolution("reso1","cont_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name())));
		l.add(newResolution("reso2","cont_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name())));
		l.add(newResolution("reso3","cont_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name())));
		
		
		l.add(newResolution("reso1","proj_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name())));
		l.add(newResolution("reso2","proj_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name())));
		l.add(newResolution("reso3","proj_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name())));
		
		
		DAOHelpers.saveModels(Resolution.class, l, errors);
	}

}
