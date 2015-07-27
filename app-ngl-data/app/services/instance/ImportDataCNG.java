package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.ContainerImportCNG;
import services.instance.experiment.ExperimentImportCNG;
import services.instance.parameter.IndexImportCNG;
import services.instance.project.ProjectImportCNG;
import services.instance.project.ProjectImportCNS;

public class ImportDataCNG {
	
	public ImportDataCNG(){
		// on peut fixer l'heure de declenchement comme dans un cron ici 16h00 ??????
		// exemple: new ProjectImportCNS(Duration.create(ImportDataUtil.nextExecutionInSeconds(16,00),TimeUnit.SECONDS),Duration.create(4,TimeUnit.HOURS));
		
		// les differents imports sont lancés en meme temps==> les logg s'entrecroisent a l'affichage !!!
		
		//new ProjectImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new IndexImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));		
		//new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));	
		//new ContainerImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
	}

}