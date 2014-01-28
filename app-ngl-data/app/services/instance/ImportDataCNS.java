package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.PrepaflowcellImportCNS;
import services.instance.container.TubeImportCNS;
import services.instance.project.ProjectImportCNS;
import services.instance.run.RunImportCNS;

public class ImportDataCNS{

	public ImportDataCNS(){

		// Import Projects tous les jours à 5h45
		new ProjectImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(5,45),TimeUnit.SECONDS)
				,Duration.create(1,TimeUnit.DAYS));
		new TubeImportCNS(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new PrepaflowcellImportCNS(Duration.create(10,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
	    new RunImportCNS(Duration.create(15,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
	    
	}

}
