package controllers.migration.models.container;

import org.codehaus.jackson.annotate.JsonIgnore;

public class SampleUsedOld {
	// Reference Sample code
		public String sampleCode;
		// Reference SampleType code
		public String typeCode;
		// Reference SampleCategory code
		public String categoryCode;
		
		public SampleUsedOld(){
		
		}

		@JsonIgnore
		public SampleUsedOld(String sampleCode,String typeCode,String categoryCode){
			this.sampleCode=sampleCode;
			this.typeCode=typeCode;
			this.categoryCode=categoryCode;
			
		}
}
