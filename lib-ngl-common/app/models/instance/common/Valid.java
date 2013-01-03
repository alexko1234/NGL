package models.instance.common;

import java.io.Serializable;


/**
 * 
 * Valid is a type with 3 possibles values ( TRUE, FALSE and UNSET)
 * Call in class container 
 * 
 * @author mhaquell
 *
 */
public class Valid implements Serializable{
	
	private static final long serialVersionUID = 1155042742588005445L;
	
	public enum Value {
		TRUE(1), FALSE(0), UNSET(2) ;
		private int value; 
		private Value(int i){
			value = i;
		}
		public int getValue() {
			   return value;
			 }
	};
	

	public Value value;
	
	
}
