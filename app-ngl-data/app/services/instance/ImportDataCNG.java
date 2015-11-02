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
		// 1er parametre=delai avant 1er declenchement, 2eme parametre=delai pour repetition
		// decaler les demarrerr pour eviter que les logg s'entrecroisent a l'affichage !!!
		
		new ProjectImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		new IndexImportCNG(Duration.create(20,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS));		
		//new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));	
		new ContainerImportCNG(Duration.create(30,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
	}
}