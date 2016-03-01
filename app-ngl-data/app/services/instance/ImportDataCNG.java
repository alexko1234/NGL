package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.ContainerImportCNG;
import services.instance.parameter.IndexImportCNG;
import services.instance.project.ProjectImportCNG;
import services.instance.sample.UpdateSampleNCBITaxonCNG;

/**
 * @author dnoisett
 * Import samples and container from CNG's LIMS to NGL 
 */

public class ImportDataCNG {
	
	public ImportDataCNG(){
		// 1er parametre=delai avant 1er declenchement, 2eme parametre=delai pour repetition
		// decaler les demarragesr pour eviter que les logs s'entrecroisent !!!
		
		//vérifier s'il y a des projets a importer 1 fois par heure
		new ProjectImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		
		//vérifier s'il y a des index a importer 1 fois par jour
		new IndexImportCNG(Duration.create(20,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS));
		
		//FDS: pas fonctionnel ?? ni nécessaire ??
		//new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));	
		
		//vérifier s'il y a des containers a importer toutes les 10 minutes
		new ContainerImportCNG(Duration.create(15,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
		
		new UpdateSampleNCBITaxonCNG(Duration.create(1,TimeUnit.MINUTES),Duration.create(10,TimeUnit.MINUTES));
	}
}