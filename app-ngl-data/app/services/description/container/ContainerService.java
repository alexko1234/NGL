package services.description.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;

public class ContainerService {
	
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{
		DAOHelpers.removeAll(ContainerSupportCategory.class, ContainerSupportCategory.find);
		DAOHelpers.removeAll(ContainerCategory.class, ContainerCategory.find);

		saveContainerCategories(errors);
		saveContainerSupportCategories(errors);

	}
	
	
	/**
	 * Save All container categories
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveContainerCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ContainerCategory> l = new ArrayList<ContainerCategory>();
		l.add(DescriptionFactory.newSimpleCategory(ContainerCategory.class, "Tube", "tube"));
		l.add(DescriptionFactory.newSimpleCategory(ContainerCategory.class, "Puit", "well"));
		l.add(DescriptionFactory.newSimpleCategory(ContainerCategory.class, "Lane", "lane"));		
		DAOHelpers.saveModels(ContainerCategory.class, l, errors);
	}

	/**
	 * Save All support categories
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveContainerSupportCategories(Map<String, List<ValidationError>> errors) throws DAOException {
		List<ContainerSupportCategory> l = new ArrayList<ContainerSupportCategory>();
		l.add(newContainerSupportCategory("Tube", "tube", 1, 1, 1, ContainerCategory.find.findByCode("tube")));
		l.add(newContainerSupportCategory("Sheet 96", "sheet-96",12, 8, 96, ContainerCategory.find.findByCode("well")));
		l.add(newContainerSupportCategory("Sheet 384", "sheet-384",24,96,384, ContainerCategory.find.findByCode("well")));
		l.add(newContainerSupportCategory("Flowcell 8", "flowcell-8",8,1,8, ContainerCategory.find.findByCode("lane")));
		l.add(newContainerSupportCategory("Flowcell 2", "flowcell-2",2,1,2, ContainerCategory.find.findByCode("lane")));
		l.add(newContainerSupportCategory("Flowcell 1", "flowcell-1",1,1,1, ContainerCategory.find.findByCode("lane")));
		DAOHelpers.saveModels(ContainerSupportCategory.class, l, errors);
		
	}
}
