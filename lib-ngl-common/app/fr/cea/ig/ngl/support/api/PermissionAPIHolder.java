package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.PermissionAPI;

public interface PermissionAPIHolder extends NGLApplicationHolder {

	default PermissionAPI getPermissionAPI() {
		return getNGLApplication().apis().permission();
	}
	
}
