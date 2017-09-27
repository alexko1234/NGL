package services.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.parameter.printer.BBP11;
import models.laboratory.parameter.printer.BarcodePosition;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionCategory;

public class InstanceFactory {
	
	
	public static Map<String,PropertyValue> newPSV(String key, Object value){
		PropertySingleValue psv = new PropertySingleValue();
		psv.value = value;
		
		Map<String,PropertyValue> map = new HashMap<String,PropertyValue>(1);
		map.put(key, psv);
		return map;
	}
	
	
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
		return newProtocol(code, name, path, version, cat, exp, null,true	);
	}

	public static Protocol newProtocol( String code,String name,
			String path, String version, String cat, List<String> exp,  Map<String, PropertyValue> properties) {
		return newProtocol(code, name, path, version, cat, exp, properties, true);
	}

	public static Protocol newProtocol( String code,String name,
			String path, String version, String cat, List<String> exp,  Boolean active) {
		return newProtocol(code, name, path, version, cat, exp, null, active);
	}

	
	public static Protocol newProtocol( String code,String name,
			String path, String version, String cat, List<String> exp,  Map<String, PropertyValue> properties, Boolean active) {
		Protocol p = new Protocol();
		p.code = code.toLowerCase().replace("\\s+", "-");
		p.name = name;
		p.filePath = path;
		p.version = version;
		p.categoryCode = cat;
		p.experimentTypeCodes = exp;
		p.properties = properties;
		p.active=active;
		return p;
	}
	
	public static BBP11 newBBP11(String name, String location, String ipAdress, Integer port, String defaultSpeed, String defaultDensity, String defaultBarcodePositionId, boolean inverseList, List<BarcodePosition> barcodePositions){
		BBP11 bbp11 = new BBP11();
		bbp11.code = name;
		bbp11.name = name;
		bbp11.categoryCode = "printer";
		bbp11.model = "BBP11";
		bbp11.location = location;
		bbp11.ipAdress = ipAdress;
		bbp11.port = port;	
		bbp11.defaultSpeed = defaultSpeed;
		bbp11.defaultDensity = defaultDensity;
		bbp11.defaultBarcodePositionId = defaultBarcodePositionId;	
		bbp11.inverseList = inverseList ;
		bbp11.barcodePositions = barcodePositions;
		return bbp11;
	}
	
	public static BarcodePosition newBarcodePosition(String id, String barcodePositionName, Integer labelWidth, String labelCommand, String barcodeCommand, boolean barcodeBottom, boolean is2d) {
		BarcodePosition barcodePosition = new BarcodePosition();
		barcodePosition.id = id;
		barcodePosition.barcodePositionName = barcodePositionName;
		barcodePosition.labelWidth = labelWidth;
		barcodePosition.labelCommand = labelCommand;
		barcodePosition.barcodeCommand = barcodeCommand;
		barcodePosition.barcodeBottom = barcodeBottom;
		barcodePosition.is2d = is2d;
		return barcodePosition;
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
