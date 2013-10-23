package services.instance;


public class ImportDataFactory {


	public AbstractImportData getImportData(){

		try {
			String institute=play.Play.application().configuration().getString("institute");

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
