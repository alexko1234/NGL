package models.utils.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.utils.DescriptionHelper;
//import models.utils.HelperObjects;
import models.utils.Model;
import models.utils.Model.Finder;
// import play.Logger;
import play.data.validation.ValidationError;

public class DAOHelpers {

	private static final play.Logger.ALogger logger = play.Logger.of(DAOHelpers.class);
	
	private static String SQLInstitute = null;

	/*
	 * Save an object Model in the description DB
	 * @param type
	 * @param models
	 * @return
	 * @throws DAOException
	 */
//	public static <T extends Model> Map<String,List<ValidationError>> saveModels(Class<T> type, Map<String, T> models) throws DAOException {
////	public static <T extends Model<T>> Map<String,List<ValidationError>> saveModels(Class<T> type, Map<String, T> models) throws DAOException {
//		Map<String,List<ValidationError>>errors = new HashMap<String, List<ValidationError>>();
//		for (Entry<String,T> model : models.entrySet()) {
//			T samp = new HelperObjects<T>().getObject(type, model.getKey());
//			if (samp != null) {
//				samp.remove(); // TODO: Remove ???
//			}
//			logger.debug(" Before save :" + model.getValue().code);
//			model.getValue().save();
//			logger.debug(" After save :" + model.getValue().code);
//			samp = new  HelperObjects<T>().getObject(type, model.getKey());
//			logger.debug(" After find :" + model.getValue().code);
//		}	
//		return errors;
//	}
	public static <T> Map<String,List<ValidationError>> saveModels(Class<T> type, Map<String, Model<T>> models) throws DAOException {
		Map<String,List<ValidationError>>errors = new HashMap<>();
		for (Entry<String,Model<T>> entry : models.entrySet()) {
			Model<T> model = entry.getValue();
//			T samp = new HelperObjects<T>().getObject(type, model.getKey());
//			if (samp != null) {
//				samp.remove(); // TODO: Remove ???
//			}
			Model<T> dbModel = model.fromDB();
			if (dbModel != null)
				dbModel.remove();
			logger.debug(" Before save :" + model.code);
			model.save();
			logger.debug(" After save :" + model.code);
			// samp = new  HelperObjects<T>().getObject(type, model.getKey());
			model = model.fromDB();
			logger.debug(" After find :" + model.code);
		}	
		return errors;
	}
	
//	public static <T extends Model> void removeAll(Class<T> type, Finder<T,? extends AbstractDAO<T>> finder) throws DAOException {
////	public static <T extends Model<T>> void removeAll(Class<T> type, Finder<T> finder) throws DAOException {
//		List<T> list = finder.findAll();
//		for(T t : list){
//			logger.debug("remove "+type.getName() + " : "+t.code);
//			t.remove();
//		}		
//	}
	
	public static <T,U extends Model<T>> void removeAll(Class<U> c, Finder<U,? extends AbstractDAO<U>> finder) throws DAOException {
		List<U> list = finder.findAll();
		for (U t : list) {
			// logger.debug("remove "+type.getName() + " : "+t.code);
			// t.remove();
			// finder.getInstance().remove(value);
			t.remove();
		}		
	}

//	public static <T extends Model> T getModelByCode(Class<T> type, Finder<T,? extends AbstractDAO<T>> finder, String code) throws DAOException {
////	public static <T extends Model<T>> T getModelByCode(Class<T> type, Finder<T> finder, String code) throws DAOException {
//		return getModelByCodes(type, finder, code).get(0);
//	}
	public static <T, U extends Model<T>> U getModelByCode(Class<U> type, Finder<U,? extends AbstractDAO<U>> finder, String code) throws DAOException {
		//		public static <T extends Model<T>> T getModelByCode(Class<T> type, Finder<T> finder, String code) throws DAOException {
		// return getModelByCodes(type, finder, code).get(0);
		return finder.findByCode(code);
	}

//	public static <T extends Model> List<T> getModelByCodes(Class<T> type, Finder<T,? extends AbstractDAO<T>> finder, String...codes) throws DAOException {
////	public static <T extends Model<T>> List<T> getModelByCodes(Class<T> type, Finder<T> finder, String...codes) throws DAOException {
//		List<T> l = new ArrayList<T>();
//		for (String code : codes) {
//			//Logger.debug("Load "+type.getName() + " : "+code);
//			l.add(finder.findByCode(code));
//		}
//		return l;
//	}
	public static <T, U extends Model<T>> List<U> getModelByCodes(Class<U> type, Finder<U,? extends AbstractDAO<U>> finder, String...codes) throws DAOException {
		//		public static <T extends Model<T>> List<T> getModelByCodes(Class<T> type, Finder<T> finder, String...codes) throws DAOException {
		List<U> l = new ArrayList<>();
		for (String code : codes) {
			//Logger.debug("Load "+type.getName() + " : "+code);
			l.add(finder.findByCode(code));
		}
		return l;
	}
	
	/*
	 * Save an object Model in the description DB if not exist also nothing
	 * Used the code to find the object
	 * @param type
	 * @param models
	 * @return
	 * @throws DAOException
	 */
//	public static <T extends Model> void saveModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
////	public static <T extends Model<T>> void saveModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
//		T t = (T) model.getInstance().findByCode(model.code);
////		T t = model.getInstance().findByCode(model.code);
//		if (t == null) {
//			logger.info("Save "+type.getName() + " : "+model.code);
//			model.save();
//		} else {
//			logger.info("Already exists "+type.getName() + " : "+model.code);
//		}
//	}
	public static <T,U extends Model<T>> void saveModel(Class<U> type, U model, Map<String,List<ValidationError>> errors) throws DAOException {
		//		public static <T extends Model<T>> void saveModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
		//			T t = (T) model.getInstance().findByCode(model.code);
//		T t = model.getInstance().findByCode(model.code);
		Model<T> t = model.fromDB();
		if (t == null) {
			logger.info("Save "+type.getName() + " : "+model.code);
			model.save();
		} else {
			logger.info("Already exists "+type.getName() + " : "+model.code);
		}
	}

	/*
	 * Save a list of models
	 * @param type
	 * @param models
	 * @param errors
	 * @throws DAOException 
	 */
//	public static <T extends Model> void saveModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
////	public static <T extends Model<T>> void saveModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
//		for (T model : models) {
//			saveModel(type, model, errors);
//		}		
//	}
	// Type argument is not needed
	public static <T, U extends Model<T>> void saveModels(Class<U> type, List<U> models, Map<String,List<ValidationError>> errors) throws DAOException {
//		public static <T extends Model<T>> void saveModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
		for (U model : models)
			saveModel(type, model, errors);
	}

	/*
	 * 
	 * @param type
	 * @param model
	 * @param errors
	 * @throws DAOException
	 */
//	public static <T extends Model> void updateModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
////	public static <T extends Model<T>> void updateModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
//		T t = (T) model.getInstance().findByCode(model.code);
////		T t = model.getInstance().findByCode(model.code);
//		if (t != null) {
//			model.update();
//		} else {
//			logger.debug("Not exists "+type.getName() + " : "+model.code);
//		}
//	}
	
	public static <T, U extends Model<T>> void updateModel(Class<U> type, U model, Map<String,List<ValidationError>> errors) throws DAOException {
		//		public static <T extends Model<T>> void updateModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
//		T t = model.getInstance().findByCode(model.code);
		//			T t = model.getInstance().findByCode(model.code);
		Model<T> t = model.fromDB();
		if (t != null) {
			model.update();
		} else {
			logger.debug("Not exists "+type.getName() + " : "+model.code);
		}
	}

//	public static <T extends Model> void updateModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
////	public static <T extends Model<T>> void updateModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
//		for (T model : models) {
//			updateModel(type, model, errors);
//		}		
//	}
	public static <T,U extends Model<T>> void updateModels(Class<U> type, List<U> models, Map<String,List<ValidationError>> errors) throws DAOException {
//		public static <T extends Model<T>> void updateModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
			for (U model : models) {
				updateModel(type, model, errors);
			}		
		}

	/*
	 * Create the sql to join with institute
	 * The rule is simple the join table name equals <main_table_name>_institute
	 * @param mainTable
	 * @param mainTableAlias
	 * @return
	 */
	public static String getSQLForInstitute(String mainTable, String mainTableAlias){
		 
		List<String> institutes = DescriptionHelper.getInstitute();
		String SQLInstitute=" inner join "+mainTable+"_institute as "+mainTable+"_join_institute on "+mainTable+"_join_institute.fk_"+mainTable+" = "
								+mainTableAlias+".id inner join institute as "+mainTable+"_inst on "+mainTable+"_inst.id = "+mainTable+"_join_institute.fk_institute ";
		//Prend en compte tous les instituts
		if (institutes.size() == 0) {
			return SQLInstitute ="";
			//Si un seul institut
		} else if (institutes.size() == 1) {
			return SQLInstitute+= " and "+mainTable+"_inst.code = '" + DescriptionHelper.getInstitute().get(0)+"' ";
		} else {
			// Si plusieurs instituts (clause in)
			SQLInstitute+="  and "+mainTable+"_inst.code in (";
			
			String comma="";
			for(int i=0;i<institutes.size();i++){
				if(i==1) comma=",";
				SQLInstitute+=comma+"'"+institutes.get(i)+"'";
			}
			return SQLInstitute+=") ";
		}		
	}
	
	public static String getInstrumentSQLForInstitute(String tableAlias){		 
		return getSQLForInstitute("instrument", tableAlias);		
	}
		
	public static String getCommonInfoTypeSQLForInstitute(String tableAlias){		 
		return getSQLForInstitute("common_info_type", tableAlias);		
	}
	
	public static String getCommonInfoTypeDefaultSQLForInstitute() {
		if (SQLInstitute == null)
			SQLInstitute = getCommonInfoTypeSQLForInstitute("t");
		return SQLInstitute;
	}
	
}
