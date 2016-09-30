package models.utils;

import models.utils.code.Code;
import play.api.modules.spring.Spring;

//Singleton
public class CodeHelper {

	public static Code getInstance()
	{			
		return Spring.getBeanOfType(Code.class);

	}
}
