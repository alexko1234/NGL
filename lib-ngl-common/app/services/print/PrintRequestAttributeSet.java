package services.print;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;


public class PrintRequestAttributeSet extends HashPrintRequestAttributeSet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PrintRequestAttributeSet() {		
		add(new Copies(1)) ;
		add(new InterleaveAttribute(this, true)) ;
	}
	
	public PrintRequestAttributeSet(PrintRequestAttributeSet attributes) {
		super(attributes) ;
	}

	public void setCopies(int copies) {
		add(new Copies(copies)) ;
	}

	public void setInterleave(boolean interleave) {
		add(new InterleaveAttribute(this, interleave)) ;
	}

	public int getCopies() {
		return ((Copies)get(Copies.class)).getValue() ;
	}

	public boolean isInterleaved() {
		return ((InterleaveAttribute)get(InterleaveAttribute.class)).isInterleaved() ;
	}

}
