package models.sra.experiment.instance;

import java.math.BigInteger;
import java.util.List;

import validation.ContextValidation;
import validation.IValidation;

public class ReadSpec implements IValidation {
	public BigInteger readIndex;
	public String readClass;
	public String readType;
	public BigInteger baseCoord;
	public String readLabel;
	public List<String> expectedBaseCallTable;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
	}

}
