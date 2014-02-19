package instruments.io.utils;

import java.lang.reflect.Constructor;

import models.laboratory.experiment.instance.Experiment;
import play.Logger;

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
}
