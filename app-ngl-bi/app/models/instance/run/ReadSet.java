package models.instance.run;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import models.instance.Utils;
import models.instance.common.PropertyValue;
import models.instance.common.TBoolean;

import play.data.validation.Constraints.Required;

public class ReadSet {

	@Required
	public String code;
	@Required
	public String sampleContainerCode; //code bar de la banque ou est l'echantillon
	@Required
	public String sampleCode; //nom de l'ind / ech
	@Required
	public String projectCode;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;	
	@Required
	public String path;	
	public String archiveId;
	public Date archiveDate;
	@Valid
	public List<File> files;
	
	public Map<String, PropertyValue> properties = Utils.getLazyMapPropertyValue();
	
	/*

	indexSequence 			tag lié au ls
	nbRead 					nombre de read de sequencage du ls
	???						ssid du ls (archivage)
	???						date d'archivage du ls
	nbClusterInternalFilter	nombre de clusters passant les filtres du ls
	nbBaseInternalFilter	nombre de bases correspondant au clusters passant les filtres du ls
	fraction				fraction de run du ls
	insertLength			id de la taille d'insert
	nbUsefulBase				nombre de bases utiles ls
	nbUsefulCluster			nombre de clusters utiles passant les filtres du ls
	q30 					q30 du ls
	score					score qualite moyen du ls

	 */
}
