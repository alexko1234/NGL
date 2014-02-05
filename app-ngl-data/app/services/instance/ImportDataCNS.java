package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.PrepaflowcellImportCNS;
import services.instance.container.TubeImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunImportCNS;

public class ImportDataCNS{

	public ImportDataCNS(){

		// Import Projects tous les jours à 16h00
		new ProjectImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(16,00),TimeUnit.SECONDS)
				,Duration.create(1,TimeUnit.DAYS));
		new TubeImportCNS(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		// Voir avec julie la frequence pour la mise en prod
		new PrepaflowcellImportCNS(Duration.create(10,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
	    new RunImportCNS(Duration.create(15,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
	   // update Tara
	   // update SSID archive
	    
	}

}
