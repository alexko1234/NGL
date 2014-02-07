package models.utils.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.utils.DescriptionHelper;
import models.utils.HelperObjects;
import models.utils.Model;
import models.utils.Model.Finder;
import play.Logger;
import play.data.validation.ValidationError;

public class DAOHelpers {


	private static String SQLInstitute = null;

	/**
	 * Save an object Model in the description DB
	 * @param type
	 * @param models
	 * @return
	 * @throws DAOException
	 */
	public static <T extends Model> Map<String,List<ValidationError>>  saveModels(Class<T> type,Map<String, T > models) throws DAOException{

		Map<String,List<ValidationError>>errors=new HashMap<String, List<ValidationError>>();

		for(Entry<String,T> model : models.entrySet()){
			T samp = new HelperObjects<T>().getObject(type, model.getKey());
			if(samp != null){
				samp.remove(); //TODO Remove ???
			}

			Logger.debug(" Before save :"+model.getValue().code);
			model.getValue().save();
			Logger.debug(" After save :"+model.getValue().code);
			samp=new  HelperObjects<T>().getObject(type, model.getKey());
			Logger.debug(" After find :"+model.getValue().code);
		}	

		return errors;
	}


	public static <T extends Model> void removeAll(Class<T> type, Finder<T> finder) throws DAOException {
		List<T> list = finder.findAll();
		for(T t : list){
			Logger.debug("remove "+type.getName() + " : "+t.code);
			t.remove();
		}		
	}

	public static <T extends Model> T getModelByCode(Class<T> type, Finder<T> finder, String code) throws DAOException {
		return getModelByCodes(type, finder, code).get(0);
	}

	public static <T extends Model> List<T> getModelByCodes(Class<T> type, Finder<T> finder, String...codes) throws DAOException {
		List<T> l = new ArrayList<T>();
		for(String code : codes){
			Logger.debug("Load "+type.getName() + " : "+code);
			l.add(finder.findByCode(code));
		}
		return l;
	}
	/**
	 * Save an object Model in the description DB if not exist also nothing
	 * Used the code to find the object
	 * @param type
	 * @param models
	 * @return
	 * @throws DAOException
	 */
	public static <T extends Model> void saveModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
		T t = (T) model.getInstance().findByCode(model.code);
		if (t == null) {
			Logger.debug("Save "+type.getName() + " : "+model.code);
			model.save();
		}else{
			Logger.debug("Allready exists "+type.getName() + " : "+model.code);
		}
	}

	/**
	 * Save a list of models
	 * @param type
	 * @param models
	 * @param errors
	 * @throws DAOException 
	 */
	public static <T extends Model> void saveModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
		for(T model : models){
			saveModel(type, model, errors);
		}		
	}

	/**
	 * 
	 * @param type
	 * @param model
	 * @param errors
	 * @throws DAOException
	 */
	public static <T extends Model> void updateModel(Class<T> type, T model, Map<String,List<ValidationError>> errors) throws DAOException {
		T t = (T) model.getInstance().findByCode(model.code);
		if (t != null) {
			model.update();
		}else{
			Logger.debug("Not exists "+type.getName() + " : "+model.code);
		}
	}

	public static <T extends Model> void updateModels(Class<T> type, List<T> models, Map<String,List<ValidationError>> errors) throws DAOException {
		for(T model : models){
			updateModel(type, model, errors);
		}		
	}

	public static String getCommonInfoTypeSQLForInstitute(String aliasCommonInfoType){
		 
		List<String> institutes=DescriptionHelper.getInstitute();
		String SQLInstitute=" join common_info_type_institute ci on "+aliasCommonInfoType+".id =ci.fk_common_info_type "+
					" join institute i on i.id = ci.fk_institute ";
		//Prend en compte tous les instituts
		if(institutes.size()==0){
			return SQLInstitute ="";
			//Si un seul institut
		}else if(institutes.size()==1){
			return SQLInstitute+= " and i.code='" + DescriptionHelper.getInstitute().get(0)+"'";
		}else {
			// Si plusieurs instituts (clause in)
			SQLInstitute+="  and i.code in (";
			String comma="";
			for(int i=0;i<institutes.size();i++){
				if(i==1) comma=",";
				SQLInstitute+=comma+"'"+institutes.get(i)+"'";
			}
			return SQLInstitute+=")";
		}		
	}
	public static String getDefaultSQLForInstitute(){
		if(SQLInstitute==null){
			SQLInstitute = getCommonInfoTypeSQLForInstitute("t");
		}
		return SQLInstitute;
	}
}
