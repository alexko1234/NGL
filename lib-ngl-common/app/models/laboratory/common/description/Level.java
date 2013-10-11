package models.laboratory.common.description;

import models.laboratory.common.description.dao.LevelDAO;
import models.utils.Model;

public class Level extends Model<Level>{
	
	public enum CODE {Container, ContainerIn, ContainerOut, Content, ContentIn, ContentOut, Experiment, Instrument, 
		Project, Process, Run, Sample, SampleUsed, Lane, ReadSet, File,
		Read1, Read2, Single, Pairs, Default};
		
	public static Finder<Level> find = new Finder<Level>(LevelDAO.class.getName()); 
	
	public Level() {
		super(LevelDAO.class.getName());
	}
	
	public Level(CODE code) {
		super(LevelDAO.class.getName());
		this.name = code.name();
		this.code = this.name;
	}

	public String name;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Level other = (Level) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}	
	
	
	
}