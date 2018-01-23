package fr.cea.ig.ngl.support.api;

import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.daoapi.CodeLabelAPI;

public interface CodeLabelAPIHolder extends NGLApplicationHolder {
	
	default CodeLabelAPI getCodeLabelAPI() { 
		return getNGLApplication().apis().codeLabel();
	}
	
}
