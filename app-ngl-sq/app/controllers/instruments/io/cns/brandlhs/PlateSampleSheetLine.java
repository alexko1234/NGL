package controllers.instruments.io.cns.brandlhs;


public class PlateSampleSheetLine  implements Comparable<PlateSampleSheetLine>{

	public String inputContainerCode;
	public String outputContainerCode;
	public String sampleName;
	
	public String dwell;
	public Integer dwellNum;
	
	public Double inputVolume;
	public Double bufferVolume;
	
	public String priority = "No";
	public String sampleType = "Standard";
	
	@Override
	public int compareTo(PlateSampleSheetLine o) {
		return this.dwellNum.compareTo(o.dwellNum);			
	}
	
	
}
