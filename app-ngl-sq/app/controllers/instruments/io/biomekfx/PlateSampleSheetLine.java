package controllers.instruments.io.biomekfx;

import controllers.instruments.io.tecanevo100.SampleSheetPoolLine;


public class PlateSampleSheetLine  implements Comparable<PlateSampleSheetLine>{

	public String inputSupportCode;
	public String outputSupportCode;
	
	public String sourceADN;
	public Integer swellADN;
	
	public String sourceTP = "reservoir tampon";
	public Integer swellTP = 1;
	
	public String destination = "plaque a covariser";
	public Integer dwell;
	
	public Double inputVolume;
	public Double bufferVolume;
	
	@Override
	public int compareTo(PlateSampleSheetLine o) {
		return this.dwell.compareTo(o.dwell);			
	}
	
	
}
