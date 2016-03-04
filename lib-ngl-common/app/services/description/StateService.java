package services.description;

import models.utils.dao.DAOException;
import models.laboratory.common.description.State;

import org.springframework.stereotype.Service;

import play.cache.Cache;

@Service
public class StateService {

	
	
	public State getStateDescription(String  stateCode) {
		if(null != stateCode){
			try {
				String key = "state."+stateCode;
				State state = (State) Cache.get(key);
				if(null == state){
					state = State.find.findByCode(stateCode);
					Cache.set(key, state);
				}
				return state;
	
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}else{
			return null;
		}
	}
}
