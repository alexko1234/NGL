package controllers.instruments.io.cns.brandlhs;


public class PlateSampleSheetLine  implements Comparable<PlateSampleSheetLine>{

	public String inputContainerCode;
	public String outputContainerCode;
	public String sampleName;
	
	public String dwell;
	public Integer dwellNum;
	
	public String inputVolume;
	public Double bufferVolume;
	public Double inputHighVolume;
	public Double bufferHighVolume;
	
	public String priority = "No";
	public String sampleType = "Standard";
	
	@Override
	public int compareTo(PlateSampleSheetLine o) {
		return this.dwellNum.compareTo(o.dwellNum);			
	}
	
	
}
