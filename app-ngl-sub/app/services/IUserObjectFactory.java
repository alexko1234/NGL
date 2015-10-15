package services;

import java.util.Map;

import models.sra.submit.util.SraException;

public interface IUserObjectFactory {
	public Object create(Map<String, String> line)throws SraException;

}
