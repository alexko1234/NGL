package models.laboratory.common.description;

import models.laboratory.common.description.dao.LevelDAO;
import models.utils.Model;

public class Level extends Model<Level> {
	
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
	};
		
//	public static Model.Finder<Level> find = new Model.Finder<Level>(LevelDAO.class.getName()); 
	public static Model.Finder<Level,LevelDAO> find = new Model.Finder<>(LevelDAO.class); 
	
	public Level() {
		super(LevelDAO.class.getName());
	}
	
	// TODO: fix doc generation error (CODE -> Level.CODE) 
	public Level(Level.CODE code) {
		super(LevelDAO.class.getName());
		this.code = code.name();
		this.name = code.name();	
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