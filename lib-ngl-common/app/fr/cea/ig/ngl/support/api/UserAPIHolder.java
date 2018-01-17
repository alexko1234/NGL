package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.UserAPI;

public interface UserAPIHolder extends NGLApplicationHolder {
	
	default UserAPI getUserAPI() { 
		return getNGLApplication().apis().user();
	}

}
