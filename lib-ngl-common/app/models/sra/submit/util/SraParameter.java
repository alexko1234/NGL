package models.sra.submit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongojack.DBQuery;

import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class SraParameter  extends DBObject {
	public String type;
	public String code;
	public String value;
	
	public static Map <String, String> getParameter(String type) {
		Map<String, String> map = new HashMap<String, String>();
		type="libProcessTypeCodeValue_orientation";
		List<SraParameter> sraParam = MongoDBDAO.find(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, DBQuery.in("type", type)).toList();
		for (SraParameter param: sraParam){
			map.put(param.code, param.value);
		}
		return map;
	}
	
}