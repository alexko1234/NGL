package models.laboratory.common.description;

import models.laboratory.common.description.dao.LevelDAO;
import models.utils.Model;

public class Level extends Model<Level>{
	
	public enum CODE {Sample, SampleUsed, SampleAndSampleUsed, SampleAndContent, Experiment, Container, ContainerIn, ContainerOut,
		Content, ContentIn, ContentOut, Instrument, Project, Process};
		
	public static Finder<Level> find = new Finder<Level>(LevelDAO.class.getName()); 
	
	public Level() {
		super(LevelDAO.class.getName());
	}
	
	public String name;
}