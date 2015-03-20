package models.laboratory.reagent.instance;

import java.util.List;

import validation.ContextValidation;

import models.laboratory.common.instance.Comment;


public class Box extends AbstractDeclaration{
	public String catalogCode;
	
	public String kitCode;

	public int stockNumber;
	public String stockPlace;
	
	public List<Comment> comments;

	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}
}
