package fr.cea.ig.auto.submission;

public class Tools {
	public Boolean isNotBlank(String string) {
		if (string == null){
			return false;
		} else if (string.equals("")) {
			return false;
		} else {
			return true;
		}
	}
}
