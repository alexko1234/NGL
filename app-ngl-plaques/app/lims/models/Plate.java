package lims.models;

import java.util.Arrays;

import javax.validation.Valid;

import models.laboratory.common.instance.TBoolean;

public class Plate {
	
	public String code;
	public Integer typeCode; //emnco
	public String typeName;
	
	public Well[] wells;
	public Integer nbWells;
	
	public TBoolean validQC = TBoolean.UNSET;
	public TBoolean validRun = TBoolean.UNSET;
	public String comment;
	@Override
	public String toString() {
	    return "Plate [code=" + code + ", typeCode=" + typeCode
		    + ", typeName=" + typeName + ", wells="
		    + Arrays.toString(wells) + ", nbWells=" + nbWells
		    + ", validQC=" + validQC + ", validRun=" + validRun
		    + ", comment=" + comment + "]";
	}
	
	
	
}
