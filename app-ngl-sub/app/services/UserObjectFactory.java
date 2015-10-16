package services;

import models.sra.submit.common.instance.UserCloneType;

public class UserObjectFactory {

	/**
     * Create a new CnsFactory that can be used to create new instances of schema derived classes for package: fr.genoscope.lis.devsi.sra
     * 
     */
    public UserObjectFactory() {
    	
    }
    

    /**
     * Create an instance of UserCloneType
     * 
     */
    public UserCloneType createUserCloneType() {
        return new UserCloneType();
    }

    
}
