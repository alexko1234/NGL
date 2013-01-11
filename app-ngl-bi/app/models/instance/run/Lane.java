package models.instance.run;

import java.util.Date;
import java.util.List;
import java.util.Map;

import models.instance.Utils;
import models.instance.common.PropertyValue;
import models.instance.common.TBoolean;

public class Lane {
	
	public Integer number;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;
	public List<ReadSet> readsets;
	public Map<String, PropertyValue> properties = Utils.getLazyMapPropertyValue();
	
	/*
	nbCycleRead1
	nbCycleReadIndex1
	nbCycleRead2
	nbCycleReadIndex2	
	nbCluster
	nbClusterInternalFilter 		nombre de clusters passant les filtres
	percentClusterInternalFilter 	pourcentage de clusters passant les filtres
	nbClusterIlluminaFilter 		nombre de clusters passant le filtre illumina
	percentClusterIlluminaFilter 	pourcentage de clusters passant le filtre illumina
	nbClusterTotal 					nombre de clusters
	nbBaseInternalFilter			nombre de bases total des sequences passant les filtres
	nbTiles 						nombre de tiles
	phasing
	prephasing		
	 */
}
