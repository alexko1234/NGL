package models.utils;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;

import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.lfw.utils.LazyLambdaSupplier;
import models.utils.dao.AbstractDAO;
import models.utils.dao.DAOException;
//import play.Logger;
import play.api.modules.spring.Spring;

// TODO: fix serialization uid but not serializable
// TODO: fix @JsonIgnore, seems overkill

//// Lighter version, only requires the DAO class.
//interface IDAOCSupplier<T,U extends AbstractDAO<T>> {
//	default U getDAO() { return Spring.getBeanOfType(daoClass()); }
//	Class<U> daoClass();
//}

//abstract class M2<T,U extends AbstractDAO<T>> implements IDAOSupplier<T,U> {	
//}

public abstract class Model<T> {

	private static final play.Logger.ALogger logger = play.Logger.of(Model.class);
	
	/**
	 * Serialization version id.  
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -3835776102653386895L;
	
	public Long id;
	
	public String code;
	
	// Should be Class<AbstractDAO<T>> or close to that
	protected String classNameDAO;

	// Some subclasses do not provide the dao class name.
	@JsonIgnore
	public Model() {
		// super(); // ??
	}

	@JsonIgnore
	public Model(String classNameDAO) {
		this.classNameDAO = classNameDAO;
	}

	// This is a covariance warning that cannot be suppressed even using
	// Model<T extends Model<T>>. Could require that subclasses provide the
	// proper definition so the subclasses can "prove" that they are indeed a T.
	@SuppressWarnings("unchecked") // solution is to abstract this method and define in subclasses
	public T self() { return (T)this; }
	
	@JsonIgnore
//	@SuppressWarnings("unchecked")
	public void update() throws DAOException {
//		getInstance().update((T) this);
		getInstance().update(self());
	}

	@JsonIgnore
//	@SuppressWarnings("unchecked")
	public long save() throws DAOException {
//		return getInstance().save((T) this);
		return getInstance().save(self());
	}

	@JsonIgnore
//	@SuppressWarnings("unchecked")
	public void remove() throws DAOException {
//		getInstance().remove((T) this);
		getInstance().remove(self());
	}

	// Get a copy of this object from the DB
	public Model<T> fromDB() throws DAOException {
		@SuppressWarnings("unchecked") // Can possibly be avoided using Model<T extends Model<T>> or not
		Model<T> m  = (Model<T>)getInstance().findByCode(code);
		return m;
	}
	
	@JsonIgnore
//	@SuppressWarnings("unchecked")
	// This is more a getDAO than a get instance.
	private AbstractDAO<T> getInstance() throws DAOException {
		try {
//			return (AbstractDAO<T>) Spring.getBeanOfType(Class.forName(classNameDAO));
			@SuppressWarnings("unchecked") // To much changes need to avoid this.
			AbstractDAO<T> dao = (AbstractDAO<T>) Spring.getBeanOfType(Class.forName(classNameDAO));
			return dao;
		} catch (ClassNotFoundException e) {
			logger.error("Class error: " + e.getMessage(), e);
			throw new DAOException(e);
		} catch (Exception e) {
			logger.error("DAO error: " + e.getMessage(), e);
			throw new DAOException(e);
		}
	}
	
	protected abstract Class<? extends AbstractDAO<T>> daoClass();
	
//	public AbstractDAO<T> getInstance() throws DAOException { return Spring.getBeanOfType(getDAOClass()); }
//	public abstract Class<? extends AbstractDAO<T>> getDAOClass();
	
	// TODO: 
	// - make Finder<T> an interface
	// - modify finders to inherit from the SQL version
	// - implement a mongo version, requires a mongo based abstract DAO implementation
	// - swap implementations
	
//	public static class Finder<T> {
//
//		private String className;
//
//		// Could take a class object.
//		@JsonIgnore
//		public Finder(String className) {
//			this.className = className;
//		}
//
//		@JsonIgnore
//		public T findByCode(String code) throws DAOException {
//			return getInstance().findByCode(code);
//		}
//		
//		@JsonIgnore
//		public List<T> findByCodes(List<String> codes) throws DAOException {
//			return getInstance().findByCodes(codes);
//		}
//
//		@JsonIgnore
//		public Boolean isCodeExist(String code) throws DAOException {
//			return getInstance().isCodeExist(code);
//		}
//
//		@JsonIgnore
//		public List<T> findAll() throws DAOException {
//			return getInstance().findAll();
//		}
//
//		@JsonIgnore
//		public T findById(Long id) throws DAOException {
//			return getInstance().findById(id);
//		}
//
//		@JsonIgnore
//		@SuppressWarnings("unchecked")
//		public AbstractDAO<T> getInstance() throws DAOException {
//			try {
//				return (AbstractDAO<T>)Spring.getBeanOfType(Class.forName(className));
//			} catch (ClassNotFoundException e) {
//				throw new DAOException(e);
//			}
//		}
//		
////		public String getClassName() {
////			return className;
////		}
//		
//	}
	
	// Could be parametric using the actual DAOclass type.
	public static class Finder<T, U extends AbstractDAO<T>> {

//		private String className;
//
//		// Could take a class object.
//		@JsonIgnore
//		public Finder(String className) {
//			this.className = className;
//		}

//		private final LazyLambdaSupplier<AbstractDAO<T>> daoRef;
//		private final Supplier<AbstractDAO<T>> daoRef;
//
//		public Finder(Supplier<AbstractDAO<T>> s) {
//			daoRef = s;
//		}
		private final Supplier<U> daoRef;

		public Finder(Supplier<U> s) {
			daoRef = s;
		}
		
		public Finder(Class<U> c) {
			this(() -> Spring.getBeanOfType(c));
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

//		public AbstractDAO<T> getInstance() {
//			return daoRef.get();
//		}
		public U getInstance() {
			return daoRef.get();
		}
		
//		@JsonIgnore
//		@SuppressWarnings("unchecked")
//		public AbstractDAO<T> getInstance() throws DAOException {
//			try {
//				return (AbstractDAO<T>)Spring.getBeanOfType(Class.forName(className));
//			} catch (ClassNotFoundException e) {
//				throw new DAOException(e);
//			}
//		}
		
//		public String getClassName() {
//			return className;
//		}
		
	}

	// Model equality and hashing is defined for code.
	
//	// Hashing using the same algorithm here and in subclasses.
//	protected int hash(int hash, Object toAdd) {
//		final int prime = 31;
//		int result = prime * hash;
//		if (code != null) 
//			result += code.hashCode();
//		return result;		
//	}
	
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}*/
	
	@Override
	public int hashCode() {
		return hash(1,code);
	}	

	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		/*@SuppressWarnings("unchecked")
//		Model<T> other = (Model<T>) obj;*/
//		Model<?> other = (Model<?>) obj;
//		if (code == null) {
//			if (other.code != null)
//				return false;
//		} else if (!code.equals(other.code))
//			return false;
//		return true;
		return typedEquals(Model.class, this, obj,
				           (a,b) -> objectEquals(a.code, b.code));
	}

}
