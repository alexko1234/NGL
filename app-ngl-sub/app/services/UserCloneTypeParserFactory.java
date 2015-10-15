package services;

import java.util.Map;
import models.sra.submit.util.SraException;

public class UserCloneTypeParserFactory  implements IUserObjectFactory {
	private UserObjectFactory factory = new UserObjectFactory(); // factory des objets User 

	@Override
	public Object create(Map<String, String> line)  throws SraException {
		UserCloneType userCloneType = factory.createUserCloneType();
		userCloneType.setAlias(line.get(UserCloneTypeParser.CLONE_ALIAS));
		if(line.get(UserCloneTypeParser.STUDY_AC) != null) {
			userCloneType.setStudyAc(line.get(UserCloneTypeParser.STUDY_AC));
		}
		if(line.get(UserCloneTypeParser.SAMPLE_AC) != null) {
			userCloneType.setSampleAc(line.get(UserCloneTypeParser.SAMPLE_AC));
		}
		return userCloneType;
	}


}
