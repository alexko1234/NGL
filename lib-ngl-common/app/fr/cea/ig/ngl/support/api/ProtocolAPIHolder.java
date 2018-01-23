package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.ProtocolAPI;

public interface ProtocolAPIHolder extends NGLApplicationHolder {
	
	default ProtocolAPI getProtocolAPI() { 
		return getNGLApplication().apis().protocol();
	}

}
