package models.sra.submit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class SraParameter extends DBObject {
	private static final play.Logger.ALogger logger = play.Logger.of(SraParameter.class);
	public String type;
	public String code;
	public String value;
	
	public static Map <String, String> getParameter(String type) {
		Map<String, String> map = new HashMap<String, String>();
		List<SraParameter> sraParam = MongoDBDAO.find(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, DBQuery.in("type", type)).toList();
		if (sraParam.isEmpty()) {
			logger.error("Absence de données de type '" + type + "' dans la table SraParmeters");
		}
		for (SraParameter param: sraParam){
			map.put(param.code, param.value);
		}
		return map;
	}

}