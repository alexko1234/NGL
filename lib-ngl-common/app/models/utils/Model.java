package models.utils;

import java.util.List;

import play.modules.spring.Spring;

public class Model<T,P> {

	public Class<P> dao;
    
	public Model(Class<P> dao) {
      this.dao = dao;
    }
    
    /*public T update()
    {
    	return getInstance().update((T) this);
    }
    
    public T add()
    {
    	return getInstance().add((T) this);
    }
    
    @SuppressWarnings("unchecked")
	public AbstractDAO<Long, T> getInstance()
    {
    	return (AbstractDAO<Long, T>) Spring.getBeanOfType(dao);
    }*/
    
    public static class Finder<P, T>
    {
    	public Class<P> dao;
    	
    	public Finder(Class<P> dao)
    	{
    		this.dao = dao;
    	}
    	
    	/*public T findByCode(String code)
        {
    		return getInstance().findByCode(code);
        }
        
        public List<T> findAll()
        {
        	return getInstance().findAll();
        }
        
        public T findById(Long id)
        {
        	return getInstance().findById(id);
        }
        
        @SuppressWarnings("unchecked")
    	public AbstractDAO<Long, T> getInstance()
        {
        	return (AbstractDAO<Long, T>) Spring.getBeanOfType(dao);
        }*/
    }
}
