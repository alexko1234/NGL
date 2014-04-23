package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.PrepaflowcellImportCNS;
import services.instance.container.TubeImportCNS;
import services.instance.container.UpdateTaraPropertiesCNS;
import services.instance.parameter.IndexImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunImportCNS;

public class ImportDataCNS{

	public ImportDataCNS(){

		// Import Projects tous les jours à 16h00
		new ProjectImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(1,00),TimeUnit.SECONDS)
				,Duration.create(1,TimeUnit.DAYS));
		new IndexImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(3,00),TimeUnit.SECONDS)
				,Duration.create(7,TimeUnit.DAYS));
		
		//Update/Create Container
		new TubeImportCNS(Duration.create(1,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
		new PrepaflowcellImportCNS(Duration.create(2,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    new RunImportCNS(Duration.create(5,TimeUnit.MINUTES),Duration.create(60,TimeUnit.MINUTES));
	    
	    //Update State and Tara Properties
	    new UpdateTaraPropertiesCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(2,00),TimeUnit.SECONDS)
				,Duration.create(1,TimeUnit.DAYS));
	    
	}

}
