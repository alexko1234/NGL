package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.ProjectAPI;

public interface ProjectAPIHolder extends NGLApplicationHolder {
	
	default ProjectAPI getProjectAPI() { 
		return getNGLApplication().apis().project();
	}
	
}
