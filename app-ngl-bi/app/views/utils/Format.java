package views.utils;

public class Format {
	
	public static String formatClusters(Object o){
		Long l = (Long)o;
		
		return String.valueOf(l.floatValue() / 1000000000);
	}
	
	
	public static String formatBases(Object o){
		Long l = (Long)o;
		return String.valueOf(l.floatValue() / 1000000000);
	}

}
