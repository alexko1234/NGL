package services.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.protocol.instance.Protocol;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;

public class InstanceFactory {
	
	/**
	 * 
	 * @param code
	 * @param name
	 * @param path
	 * @param version
	 * @param cat
	 * @return
	 */
	public static Protocol newProtocol( String code,String name,
			String path, String version, String cat, List<String> exp) {
		Protocol p = new Protocol();
		p.code = code.toLowerCase().replace("\\s+", "-");
		p.name = name;
		p.filePath = path;
		p.version = version;
		p.categoryCode = cat;
		p.experimentTypeCodes = exp;
		return p;
	}
	
	
	public static List<String> setExperimentTypeCodes(String...exp){
		List<String> lp = new ArrayList<String>();
		for(String s:exp){
			lp.add(s);
		}
		
		return lp;
	}
	

	/**
	 * define a resolution in MongoDB (with specific level) 
	 * @param name
	 * @param code
	 * @param categoryName
	 * @param displayOrder
	 * @param level
	 * @param categoryDisplayOrder
	 * @return
	 */
	public static Resolution newResolution(String name, String code,
			 ResolutionCategory rc, Short displayOrder, String level) {
			Resolution ir = new Resolution();
			ir.code = code;
			ir.name = name;
			ir.displayOrder = displayOrder;
			ir.category = new ResolutionCategory(rc.name, rc.displayOrder); 
			ir.level = level;
			return ir;
		}

	/**
	 * define a resolution in MongoDB
	 * @param name
	 * @param code
	 * @param categoryName
	 * @param displayOrder
	 * @param categoryDisplayOrder
	 * @return
	 */
	public static Resolution newResolution(String name, String code, ResolutionCategory rc, Short displayOrder) {
			Resolution ir = new Resolution();
			ir.code = code;
			ir.name = name;
			ir.displayOrder = displayOrder;
			ir.category = new ResolutionCategory(rc.name, rc.displayOrder); 
			return ir;
		}


}
