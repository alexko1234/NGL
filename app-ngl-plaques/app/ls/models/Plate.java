package ls.models;

import java.util.Arrays;

import javax.validation.Valid;

public class Plate {
	
	public String code;
	public Integer typeCode; //emnco
	public String typeName;
	
	public Well[] wells;
	public Integer nbWells;
	@Override
	public String toString() {
		return "Plate [code=" + code + ", typeCode=" + typeCode + ", typeName="
				+ typeName + ", wells=" + Arrays.toString(wells) + ", nbWells="
				+ nbWells + "]";
	}
	
	
}
