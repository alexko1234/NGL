package ls.models;

import java.util.Arrays;

import javax.validation.Valid;

public class Plate {
	
	public String code;
	@Valid
	public Well[] wells;
	@Override
	public String toString() {
		return "Plate [code=" + code + ", wells=" + Arrays.toString(wells)
				+ "]";
	}
	
	public Integer nbWells;

}
