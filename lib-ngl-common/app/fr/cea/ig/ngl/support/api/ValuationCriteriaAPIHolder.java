package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.ValuationCriteriaAPI;

public interface ValuationCriteriaAPIHolder extends NGLApplicationHolder {
	
	default ValuationCriteriaAPI getValuationCriteriaAPI() {
		return getNGLApplication().apis().valuationCriteria();
	}

}
