package validation.resolution.instance;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.resolutions.instance.Resolution;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;

public class ResolutionValidationHelper extends CommonValidationHelper {
	
	
	public static void validationResolutions(List<Resolution> resolutions, ContextValidation contextValidation) {
		if(null != resolutions && resolutions.size() > 0) {
			int index = 0;
			Set<String> resolutionCodes = new TreeSet<String>();
			for (Resolution resolution : resolutions) {
				if (resolution != null) {
					contextValidation.addKeyToRootKeyName("resolutions[" + index + "]");
					resolution.validate(contextValidation);
					if(resolutionCodes.contains(resolution.code)){
						contextValidation.addErrors("code", ValidationConstants.ERROR_NOTUNIQUE_MSG, resolution.code);
					}
					resolutionCodes.add(resolution.code);
					contextValidation.removeKeyFromRootKeyName("resolutions[" + index + "]");
				}
				index++;
			}
		}
	}

}
