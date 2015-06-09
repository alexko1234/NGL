package models.laboratory.reagent.utils;

import org.apache.commons.lang3.StringUtils;

import models.sra.submit.util.SraCodeHelper;
import models.utils.CodeHelper;

public class ReagentCodeHelper extends CodeHelper{
	
	public ReagentCodeHelper()
	{}
	
    private static class SingletonHolder
	{
		private final static ReagentCodeHelper instance = new ReagentCodeHelper();
	}
    
	public static ReagentCodeHelper getInstance()
	{			
		return SingletonHolder.instance;
	}

	public synchronized String generateKitCatalogCode() {
		return generateBarCode();
	}

	public synchronized String generateBoxCatalogCode(String kitCatalogCode) {
		return StringUtils.stripAccents(kitCatalogCode + "-"
				+ generateBarCode());
	}

	public synchronized String generateReagentCatalogCode(String boxCatalogCode) {
		return boxCatalogCode + "-" + generateBarCode();
	}
	
	public synchronized String generateKitCode() {
		return generateBarCode();
	}

	public synchronized String generateBoxCode(String kitCode) {
		return StringUtils.stripAccents(kitCode + "-"
				+ generateBarCode());
	public synchronized String generateBoxCode() {
		return StringUtils.stripAccents(generateBarCode());
	}

	public synchronized String generateReagentCode() {
		return generateBarCode();
	}
}
