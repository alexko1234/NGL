package data.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import play.Logger;
import play.data.validation.ValidationError;
import validation.utils.ConstraintsHelper;

public class LoadDataHelper {
	
	
	public final static String PREFIX="properties.";
	public final static char SEPARATOR=';';
	public final static String FORMAT_DATE="MM/dd/yyyy";;
	
	public static void validateHeader(String[] firstLine, SampleType sampleType, ImportType importType, Map<String, List<ValidationError>> errors) {
		
		if (firstLine==null){
			ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "Header"), "HEADER VIDE",sampleType.code,importType.code);
			return;
		}
		
		String resultFirstLine=getFirstLine(sampleType, importType);
		
		if (resultFirstLine==null) {
			ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "Header"), "HEADER VIDE",sampleType.code,importType.code);
			return;
		}
				
		Collection<String> listColumns1=Arrays.asList(firstLine);
		Collection<String> listColumns2=Arrays.asList(resultFirstLine.split(String.valueOf(SEPARATOR)));
		
		Collection<String> similar = new HashSet<String>( listColumns1 );
        Collection<String> different = new HashSet<String>();

        different.addAll( listColumns1 );
        different.addAll( listColumns2 );
        
        similar.retainAll( listColumns2);
        different.removeAll( similar );
        
		for(String column : different){
			Logger.debug("Column error :"+column);
			ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "COLUMN "+column), "COLUMN ",column);
		}
	}

	// TODO mettre dans lib-ngl-common
	public static void validateFile(String fileName,Map<String, List<ValidationError>> errors){
		File file=new File(fileName);
		if(!file.exists()){
			Logger.debug("File not found :"+fileName);
			ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "Filename"), "FILE NOT EXIST",fileName);
		}
		
	}


	public static String getFirstLine(SampleType sampleType ,ImportType importType){

		System.err.println("SampleType :"+sampleType.propertiesDefinitions.size());
		String headerLine =Sample.HEADER;
		
		headerLine=getHeaderProperties(headerLine, sampleType.propertiesDefinitions,Sample.class);

		headerLine=headerLine.concat(String.valueOf(LoadDataHelper.SEPARATOR));
		headerLine=headerLine.concat(Container.HEADER);    	

		headerLine=getHeaderProperties(headerLine, importType.propertiesDefinitions,ImportType.class);
		return headerLine;

	}


	public static String getHeaderProperties(String headerLine,List<PropertyDefinition> propertiesDefinitions,Class rootClass){
		if(propertiesDefinitions!=null){

			for(PropertyDefinition p:propertiesDefinitions){
				//Logger.debug("Property :"+p.code);
				headerLine=headerLine.concat(SEPARATOR+rootClass.getSimpleName()+"."+PREFIX+p.code);
			}
		}

		return headerLine;

	}
	
	
	public static Container containerFromCSVLine(String[] firstLine,
			String[] nextLine,Sample sample,Map<String,PropertyDefinition> propertiesDefinition) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, DAOException {

		Container container = new Container();
		ContainerHelper.addContent(container, sample);
		Content content =container.contents.get(0);
		Class<Container> aClass=Container.class;
		Field field =null;
		String rootPrefix=aClass.getSimpleName()+".";
		String contentPrefix="Experiment."+PREFIX;

		for(int i=0;i<firstLine.length;i++)
		{

			//Logger.debug("Key :"+firstLine[i]+ ", values :"+nextLine[i]);

			System.err.println("Key :"+firstLine[i]+ ", values :"+nextLine[i]);
			if(firstLine[i].startsWith(contentPrefix) ) {

				if(content.properties==null) content.properties=new HashMap<String, PropertyValue>();
				String key=firstLine[i].substring((contentPrefix).length());
				if(!nextLine[i].isEmpty())
					content.properties.put(key, new PropertyValue(ConstraintsHelper.transformValue(propertiesDefinition.get(key).type, nextLine[i],FORMAT_DATE)));
			}
			else if(firstLine[i].startsWith(rootPrefix)) {

				if(firstLine[i].endsWith(".comments")){
					container.comments=InstanceHelpers.addComment(nextLine[i],container.comments);
				}
				else	{

					field = aClass.getField(firstLine[i].replaceFirst(aClass.getSimpleName()+".", ""));
					field.set(container,ConstraintsHelper.transformValue(field.getType().getName(), nextLine[i],FORMAT_DATE));
				}		  

			}
			
		}
		
		container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
		
		return container;
	}
	
	

	public static Sample sampleFromCSVLine(String[] firstLine, String[] nextLine,Map<String,PropertyDefinition> propertiesDefinition) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

		Class aClass=Sample.class;
		Sample sample = new Sample();
		Field field =null;
		String rootPrefix=aClass.getSimpleName()+'.';
		String type=null;
		for(int i=0;i<firstLine.length;i++)
		{

			Logger.debug("Key :"+firstLine[i]+ ", values :"+nextLine[i]);

			if(firstLine[i].startsWith(rootPrefix+PREFIX)) {

				if(sample.properties==null) sample.properties=new HashMap<String, PropertyValue>();
				String key=firstLine[i].substring((rootPrefix+PREFIX).length());
				if(!nextLine[i].isEmpty())	
					sample.properties.put(key, new PropertyValue(ConstraintsHelper.transformValue(propertiesDefinition.get(key).type, nextLine[i],FORMAT_DATE)));
			}
			else if(firstLine[i].startsWith(rootPrefix)) {

				if(firstLine[i].endsWith(".comments")){					
					sample.comments=InstanceHelpers.addComment(nextLine[i],sample.comments);
				}
				else if(firstLine[i].endsWith(".projectCodes")){					
					sample.projectCodes=InstanceHelpers.addCode(nextLine[i],sample.projectCodes);
				}
				else	{

					field = aClass.getField(firstLine[i].replaceFirst(rootPrefix, ""));
					field.set(sample,ConstraintsHelper.transformValue(field.getType().getName(), nextLine[i],FORMAT_DATE));
				}		  

			}
		}
		
		sample.traceInformation.setTraceInformation(InstanceHelpers.getUser());

		return sample;
	}

}
