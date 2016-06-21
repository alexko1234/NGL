package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.utils.CodeHelper;
import validation.ContextValidation;

public class CommentsFieldConfiguration extends ObjectFieldConfiguration<Comment> {
	
	public CommentsFieldConfiguration() {
		super(AbstractFieldConfiguration.commentsType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation)
			throws Exception {
		
		Comment commentObject = new Comment();
		commentObject.creationDate = new Date();
		commentObject.createUser = contextValidation.getUser();
		commentObject.code = CodeHelper.getInstance().generateExperimentCommentCode(commentObject);

		populateSubFields(commentObject, rowMap, contextValidation);

		populateField(field, dbObject, Collections.singletonList(commentObject));		
	}

}
