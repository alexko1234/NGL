package models.instance.container;

import utils.refobject.ObjectMongoDBReference;
import utils.refobject.ObjectSGBDReference;
import models.description.content.SampleCategory;
import models.description.content.SampleType;

public class SampleUsed {
	// Reference Sample code
	public String sampleCode;
	// Reference SampleType code
	public String typeCode;
	// Reference SampleCategory code
	public String categoryCode;
	
	

	public Sample getSample(){

		try {
			return new ObjectMongoDBReference<Sample>(Sample.class,sampleCode).getObject();
		} catch (Exception e) {
			//TODO
		}
		return null;

	}
	
	
	public SampleType getSampleType(){

		try {
			return new ObjectSGBDReference<SampleType>(SampleType.class,typeCode).getObject();
		} catch (Exception e) {
			//TODO
		}
		return null;

	}
	
	public SampleCategory getSampleCategory(){

		try {
			return new ObjectSGBDReference<SampleCategory>(SampleCategory.class,categoryCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}

}
