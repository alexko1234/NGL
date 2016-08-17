package controllers.instruments.io.cns.biomekfx;

import controllers.instruments.io.cns.tecanevo100.SampleSheetPoolLine;


public class PlateSampleSheetLine  implements Comparable<PlateSampleSheetLine>{

	public String inputContainerCode;
	public String outputContainerCode;
	
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
