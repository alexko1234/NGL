package services;

import java.util.Map;

import models.sra.submit.util.SraException;

// Should probably be generic and return a properly type object. 
public interface IUserObjectFactory {
	
	public Object create(Map<String, String> line) throws SraException;

}
