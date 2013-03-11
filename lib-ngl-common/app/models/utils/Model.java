package models.utils;

import java.util.List;

import models.utils.dao.AbstractCommonDAO;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;

public class Model<T> {

	public Long id;
	public String code;
	public String classNameDAO;
    
	
	public Model() {
		super();
	}

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
	public AbstractCommonDAO<T> getInstance() throws DAOException
    {
    	try {
			return (AbstractCommonDAO<T>) Spring.getBeanOfType(Class.forName(classNameDAO));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DAOException(e);
		}catch(Exception e){
			e.printStackTrace();
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
    	public AbstractCommonDAO<T> getInstance() throws DAOException
        {
        	try {
				return (AbstractCommonDAO<T>) Spring.getBeanOfType(Class.forName(className));
			} catch (ClassNotFoundException e) {
				throw new DAOException(e);
			}
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
    
    
}
