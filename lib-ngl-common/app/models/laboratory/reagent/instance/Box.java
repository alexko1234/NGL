package models.laboratory.reagent.instance;

import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;


public class Box {
	public String catalogCode;
	
	public String kitCode;
	
	public Date receptionDate;
	public Date expirationDate;
	
	public int stockNumber;
	
	public Date startToUseDate;
	public Date endUseDate;
	
	public TraceInformation traceInformation;
	
	public List<Comment> comments;
}
