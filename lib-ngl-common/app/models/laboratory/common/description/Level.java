package models.laboratory.common.description;

import static fr.cea.ig.lfw.utils.Hashing.hash;
import static fr.cea.ig.lfw.utils.Equality.objectEquals;
import static fr.cea.ig.lfw.utils.Equality.typedEquals;

import models.laboratory.common.description.dao.LevelDAO;
import models.utils.Model;
import models.utils.dao.AbstractDAO;

public class Level extends Model<Level> {
	
//	public static Model.Finder<Level> find = new Model.Finder<Level>(LevelDAO.class.getName()); 
	public static final Finder<Level,LevelDAO> find = new Finder<>(LevelDAO.class); 
	
	//not used ContentIn, ContentOut, ContainerSupportIn, ContainerSupportOut
	public enum CODE {
		Container, 
		ContainerIn, 
		ContainerOut, 
		Content, 
		ContainerSupport, 
		Experiment, 
		Instrument, 
		Project, 
		Process, 
		Run, 
		Sample, 
		Lane, 
		ReadSet, 
		File,
		Read1, 
		Read2, 
		Single, 
		Pairs, 
		Default, 
		Analysis
	}
			
	public String name;
	
	public Level() {
		super(LevelDAO.class.getName());
	}
	
	// TODO: fix doc generation error (CODE -> Level.CODE) 
	public Level(Level.CODE code) {
		super(LevelDAO.class.getName());
		this.code = code.name();
		this.name = code.name();	
	}

	@Override
	protected Class<? extends AbstractDAO<Level>> daoClass() {
		return LevelDAO.class;
	}	

	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
		return hash(super.hashCode(),name);
	}

	@Override
	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (!super.equals(obj))
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Level other = (Level) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		return true;
		return typedEquals(Level.class, this, obj,
				           (a,b) -> super.equals(obj) && objectEquals(a.name,b.name));
	}
	
}