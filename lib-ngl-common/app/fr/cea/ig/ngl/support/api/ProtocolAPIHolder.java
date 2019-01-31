package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.dao.protocols.ProtocolAPI;

public interface ProtocolAPIHolder extends NGLApplicationHolder {
	
	default ProtocolAPI getProtocolAPI() { 
		return getNGLApplication().apis().protocol();
	}

}
