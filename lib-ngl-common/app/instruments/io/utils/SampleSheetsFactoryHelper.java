package instruments.io.utils;

import java.io.File;
import java.lang.reflect.Constructor;

import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.Instrument;
import models.utils.dao.DAOException;
import play.Logger;
import play.Play;

public class SampleSheetsFactoryHelper {
	public static Object getSampleSheetsFactory(String className, Experiment experiment){
		Object instance = null;
		try{
			Class<?> clazz = Class.forName(className);
			Constructor<?> constructor = clazz.getConstructor(Experiment.class);
			instance = constructor.newInstance(experiment);
		}catch(Exception e){
			Logger.error("Can't find sampleSheet class: error");
		}
		
		return instance;
	}
	
	public static String getSampleSheetFilePath(String instrumentCode){
		Instrument instrument = null;
		try {
			instrument = Instrument.find.findByCode(instrumentCode);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(instrument != null){
			if(Play.application().configuration().getString("ngl.path.instrument") != null){
				return Play.application().configuration().getString("ngl.path.instrument")+instrument.path+File.separator;
			}else{
				return instrument.path+File.separator;
			}
		}
		return null;
	}
}
