package instruments.io.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import scala.io.Codec;

import fr.cea.ig.MongoDBDAO;

public abstract class AbstractSampleSheetsfactory {
	protected Experiment experiment;
	
	public AbstractSampleSheetsfactory(Experiment varExperiment){
		this.experiment = varExperiment;
	}
	
	public abstract File generate();
	
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
	
	public static Index getIndex(String typeCode, String code){
		Index index  = MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Index.class, DBQuery.is("typeCode", typeCode).and(DBQuery.is("code", code)));
		return index;
	}
	
	public static String getContentProperty(Content content, String propertyName){
		if(content.properties.get(propertyName) != null){
			return (String) content.properties.get(propertyName).value;
		}
		return "";
	}
	
	public static Double getContentDoubleProperty(Content content, String propertyName){
		if(content.properties.get(propertyName) != null){
			return  (Double) content.properties.get(propertyName).value;
		}
		return 0.0;
	}
	
	public static String getIntrumentBooleanProperties(Experiment experiment,String propertyName){
		if(Boolean.class.isInstance(experiment.instrumentProperties.get(propertyName).value)){
			if((Boolean) experiment.instrumentProperties.get(propertyName).value){
				return "O";
			}
		}
		return "N";
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
	
	public static void writeFile(File file, String content){
		Writer writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			writer = new OutputStreamWriter(fos, Codec.UTF8().name());
			writer.write(content);
			writer.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
