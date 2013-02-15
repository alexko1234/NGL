package models.utils;

import java.util.List;

import models.utils.dao.DAOException;
import models.utils.dao.IDAO;
import play.modules.spring.Spring;

public class Model<T> {

	public Long id;
	public String code;
	public String classNameDAO;
    
	public Model(String classNameDAO) {
      this.classNameDAO = classNameDAO;
    }
    
    @SuppressWarnings("unchecked")
	public void update() throws DAOException
    {
    	getInstance().update((T)this);
    }
    
    @SuppressWarnings("unchecked")
	public long save() throws DAOException
    {
    	return getInstance().save((T)this);
    }
    
    @SuppressWarnings("unchecked")
	public void remove() throws DAOException
    {
    	getInstance().remove((T)this);
    }
    
    @SuppressWarnings("unchecked")
	public IDAO<T> getInstance() throws DAOException
    {
    	try {
			return (IDAO<T>) Spring.getBeanOfType(Class.forName(classNameDAO));
		} catch (ClassNotFoundException e) {
			throw new DAOException(e);
		}
    }
    
    public static class Finder<T>
    {
    	private String className;
    	public Finder(String className)
    	{
    		this.className = className;
    	}
    	
    	public T findByCode(String code) throws DAOException
        {
    		return getInstance().findByCode(code);
        }
        
        public List<T> findAll() throws DAOException
        {
        	return getInstance().findAll();
        }
        
        public T findById(Long id) throws DAOException
        {
        	return getInstance().findById(id);
        }
        
        @SuppressWarnings("unchecked")
    	public IDAO<T> getInstance() throws DAOException
        {
        	try {
				return (IDAO<T>) Spring.getBeanOfType(Class.forName(className));
			} catch (ClassNotFoundException e) {
				throw new DAOException(e);
			}
        }
    }
}
