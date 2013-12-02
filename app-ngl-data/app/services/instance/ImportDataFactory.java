package services.instance;

import play.Logger;


public class ImportDataFactory { 

	public AbsImportData getImportData(){

		try {
			String institute=play.Play.application().configuration().getString("import.institute");
			Logger.debug("Import institute "+ institute);
			if(institute.equals("CNG")){
				return new ImportDataCNG();
			}else if (institute.equals("CNS")){
				return new ImportDataCNS();
			} else {
				throw new RuntimeException("La valeur de l'attribut institute dans application.conf n'a pas d'implementation");
			}
		}catch(Exception e){
			throw new RuntimeException("L'attribut institute dans application.conf n'est pas renseigné");
		}
	}

}
