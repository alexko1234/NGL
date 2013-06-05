package workflows;

import models.laboratory.container.instance.Container;
import fr.cea.ig.MongoDBDAO;

public class Workflows {
	private static final String CONTAINER_COLL_NAME = "Container";

	/**
	 * Set a state of a container to A (Available)
	 * @param containerCode: the code of the container
	 */
	public static void setAvailable(String containerCode,String processTypeCode){
		Container container = MongoDBDAO.findByCode(CONTAINER_COLL_NAME, Container.class, containerCode);
		
		if(container != null && (container.stateCode.equals("IWP") || container.stateCode.equals("N"))){
			MongoDBDAO.updateSet(CONTAINER_COLL_NAME, container,"stateCode", "A");
			MongoDBDAO.updateSet(CONTAINER_COLL_NAME, container,"processTypeCode", processTypeCode);
		}
	}
}
