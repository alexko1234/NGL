
package services.description.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;

public class ProjectServiceCNG extends AbstractProjectService{


	public void saveProjectCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ProjectCategory> l = new ArrayList<ProjectCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ProjectCategory.class,"defaut", "default"));
		DAOHelpers.saveModels(ProjectCategory.class, l, errors);
	}

	public void saveProjectTypes(Map<String, List<ValidationError>> errors) throws DAOException{
		List<ProjectType> l = new ArrayList<ProjectType>();

		l.add(DescriptionFactory.newProjectType("Defaut", "default-project", ProjectCategory.find.findByCode("default"), null, DescriptionFactory.getInstitutes(Institute.CODE.CNG)));

		DAOHelpers.saveModels(ProjectType.class, l, errors);

	}

}



