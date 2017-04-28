package services.instance;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import services.instance.container.ContainerImportCNG;
import services.instance.parameter.IndexImportCNG;
import services.instance.project.ProjectImportCNG;
import services.instance.sample.UpdateSampleNCBITaxonCNG;
import services.instance.sample.UpdateSamplePropertiesCNS;

/**
 * @author dnoisett
 * Import samples and container from CNG's LIMS to NGL 
 */

public class ImportDataCNG {
	
	public ImportDataCNG(){
		// 1er parametre=delai avant 1er declenchement, 2eme parametre=delai pour repetition
		// decaler les demarragesr pour eviter que les logs s'entrecroisent !!!
		
		//vérifier s'il y a des projets a importer 1 fois par heure
		new ProjectImportCNG(Duration.create(5,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));
		
		//vérifier s'il y a des index a importer 1 fois par jour
		new IndexImportCNG(Duration.create(10,TimeUnit.SECONDS),Duration.create(24,TimeUnit.HOURS));
		
		//FDS: pas fonctionnel ?? ni nécessaire ??
		//new ExperimentImportCNG(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES));	
		
		//vérifier s'il y a des containers a importer toutes les 10 minutes
		new ContainerImportCNG(Duration.create(30,TimeUnit.SECONDS),Duration.create(10,TimeUnit.MINUTES));
		
		//Mise a jour des info du NCBI pour les samples qui n'en ont pas
		new UpdateSampleNCBITaxonCNG(Duration.create(1,TimeUnit.MINUTES),Duration.create(6,TimeUnit.HOURS));
		 
		//11/04/2017 ajouter la propagation des modifications apportées aux samples...
		new UpdateSamplePropertiesCNS(Duration.create(15,TimeUnit.MINUTES),Duration.create(6,TimeUnit.HOURS));
	}
}