package services.print;

import javax.print.attribute.PrintRequestAttribute;


public class InterleaveAttribute implements PrintRequestAttribute {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean interleaved  ;
	
	public InterleaveAttribute(PrintRequestAttributeSet set, boolean isInterleaved) {
		interleaved = isInterleaved ;
	}

	public Class<InterleaveAttribute> getCategory() {
		return InterleaveAttribute.class ;
	}

	public String getName() {
		return "interleave" ;
	}
	
	public boolean isInterleaved() {
		return interleaved;
	}

}