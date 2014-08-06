package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;
public class MeasureService {
	
	public static final String MEASURE_CAT_CODE_CONCENTRATION = "concentration";
	public static final String MEASURE_CAT_CODE_QUANTITY = "quantity";
	public static final String MEASURE_CAT_CODE_VOLUME = "volume";
	public static final String MEASURE_CAT_CODE_SIZE = "size";

	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		saveMesureCategories(errors);	
		saveMesureUnits(errors);	
	}
	
	
	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveMesureCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<MeasureCategory> l = new ArrayList<MeasureCategory>();
		l.add(DescriptionFactory.newSimpleCategory(MeasureCategory.class,"Concentration", MEASURE_CAT_CODE_CONCENTRATION));
		l.add(DescriptionFactory.newSimpleCategory(MeasureCategory.class,"Quantité", MEASURE_CAT_CODE_QUANTITY));
		l.add(DescriptionFactory.newSimpleCategory(MeasureCategory.class,"Volume", MEASURE_CAT_CODE_VOLUME));
		l.add(DescriptionFactory.newSimpleCategory(MeasureCategory.class,"Taille", MEASURE_CAT_CODE_SIZE));
		DAOHelpers.saveModels(MeasureCategory.class, l, errors);
	}
	
	
	/**
	 * Save all ExperimentCategory
	 * @param errors
	 * @throws DAOException 
	 */
	public static void saveMesureUnits(Map<String,List<ValidationError>> errors) throws DAOException{
		List<MeasureUnit> l = new ArrayList<MeasureUnit>();
		l.add(newMeasureUnit("mL","mL", true, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_VOLUME)));
		l.add(newMeasureUnit("µL","µL", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_VOLUME)));
		l.add(newMeasureUnit("nL","nL", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_VOLUME)));
		l.add(newMeasureUnit("pL","pL", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_VOLUME)));
		
		l.add(newMeasureUnit("µmol","µmol", true, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_QUANTITY)));
		l.add(newMeasureUnit("nmol","nmol", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_QUANTITY)));
		l.add(newMeasureUnit("pmol","pmol", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_QUANTITY)));
		l.add(newMeasureUnit("ng","ng", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_QUANTITY)));
		
		l.add(newMeasureUnit("ng/µl","ng/µl", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_CONCENTRATION)));
		l.add(newMeasureUnit("nM","nM", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_CONCENTRATION)));
		l.add(newMeasureUnit("pM","pM", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_CONCENTRATION)));
		
		l.add(newMeasureUnit("pb","pb", true,  MeasureCategory.find.findByCode(MEASURE_CAT_CODE_SIZE)));
		l.add(newMeasureUnit("Mb","Mb", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_SIZE)));
		l.add(newMeasureUnit("kb","kb", false, MeasureCategory.find.findByCode(MEASURE_CAT_CODE_SIZE)));
		
		
		DAOHelpers.saveModels(MeasureUnit.class, l, errors);
	}

}
