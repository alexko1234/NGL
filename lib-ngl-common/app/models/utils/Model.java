package models.utils;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;
import play.Logger;
import play.api.modules.spring.Spring;

public class Model<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3835776102653386895L;
	public Long id;
	public String code;
	protected String classNameDAO;
    
	@JsonIgnore
	public Model() {
		super();
	}
	@JsonIgnore
	public Model(String classNameDAO) {
      this.classNameDAO = classNameDAO;
    }
	@JsonIgnore
    @SuppressWarnings("unchecked")
	public void update() throws DAOException
    {
    	getInstance().update((T)this);
    }
	@JsonIgnore
    @SuppressWarnings("unchecked")
	public long save() throws DAOException
    {
    	return getInstance().save((T)this);
    	
    }
	@JsonIgnore
    @SuppressWarnings("unchecked")
	public void remove() throws DAOException
    {
    	getInstance().remove((T)this);
    }
	@JsonIgnore
    @SuppressWarnings("unchecked")
	public AbstractDAO<T> getInstance() throws DAOException
    {
    	try {
			return (AbstractDAO<T>) Spring.getBeanOfType(Class.forName(classNameDAO));
		} catch (ClassNotFoundException e) {
			Logger.error("Class error: "+e.getMessage(),e);;
			throw new DAOException(e);
		}catch(Exception e){
			Logger.error("DAO error: "+e.getMessage(),e);
			throw new DAOException(e);
		}
    }
    
    public static class Finder<T>
    {
		
		private String className;

		@JsonIgnore
		public Finder(String className) {
			this.className = className;
		}
		
		@JsonIgnore
		public T findByCode(String code) throws DAOException {
			return getInstance().findByCode(code);
		}
		
		@JsonIgnore
		public List<T> findByCodes(List<String> codes) throws DAOException {
			return getInstance().findByCodes(codes);
		}
		
		@JsonIgnore
		public Boolean isCodeExist(String code) throws DAOException {
			return getInstance().isCodeExist(code);
		}

		@JsonIgnore
		public List<T> findAll() throws DAOException {
			return getInstance().findAll();
		}

		@JsonIgnore
		public T findById(Long id) throws DAOException {
			return getInstance().findById(id);
		}

		@JsonIgnore
		@SuppressWarnings("unchecked")
		public AbstractDAO<T> getInstance() throws DAOException {
			try {
				return (AbstractDAO<T>) Spring.getBeanOfType(Class
						.forName(className));
			} catch (ClassNotFoundException e) {
				throw new DAOException(e);
			}
		}
		
		public String getClassName() {
			return className;
		}		
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Model<T> other = (Model<T>) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
   
}
