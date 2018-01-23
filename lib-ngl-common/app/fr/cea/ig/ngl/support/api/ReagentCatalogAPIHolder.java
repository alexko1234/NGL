package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.ReagentCatalogAPI;

public interface ReagentCatalogAPIHolder extends NGLApplicationHolder {

	default ReagentCatalogAPI getReagentCatalogAPI() {
		return getNGLApplication().apis().reagentCatalog();
	}
	
}
