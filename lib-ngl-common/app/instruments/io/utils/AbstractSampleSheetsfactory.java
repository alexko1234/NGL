package instruments.io.utils;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import fr.cea.ig.MongoDBDAO;

public abstract class AbstractSampleSheetsfactory {
	protected Experiment experiment;
	
	public AbstractSampleSheetsfactory(Experiment varExperiment){
		this.experiment = varExperiment;
	}
	
	public abstract String generate();
	
	protected String format(String content){
		return content.trim().replaceAll("(?m)^\\s{1,}", "").replaceAll("\n{2,}", "\n");
	}
	
	protected List<Container> getContainersFromExperiment(){
		List<Container> containers = new ArrayList<Container>();
		for(int i=0; i<this.experiment.atomicTransfertMethods.size();i++){
			for(ContainerUsed cu : this.experiment.atomicTransfertMethods.get(i).getInputContainers()){
				containers.add(MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, cu.code));
			}
		}
		
		return containers;
	}
	
	public static Index getIndex(String categoryCode, String code){
		Index index  = MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("categoryCode", categoryCode).and(DBQuery.is("code", code)));
		return index;
	}
	
	public static String getContentProperty(Content content, String propertyName){
		return (String) content.properties.get(propertyName).value;
	}
	
	public static Double getContentDoubleProperty(Content content, String propertyName){
		return (Double) content.properties.get(propertyName).value;
	}
	
	public static String getContainerProperty(Container container, String propertyName){
		if(Boolean.class.isInstance(container.properties.get(propertyName).value)){
			if((Boolean) container.properties.get(propertyName).value){
				return "O";
			}
			return "N";
		}
		
		return (String) container.properties.get(propertyName).value;
	}
}
