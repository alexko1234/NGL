package models.laboratory.container.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import models.utils.ObjectSGBDReference;

import org.codehaus.jackson.annotate.JsonIgnore;



/**
 * 
 * Embedded data in collection Container
 * Associate support and container with a position (x,y)
 * 
 * If container category is  tube, the position is (x,y)=(1,1) and support category is 'VIDE'
 * 
 * A support intance defines by unique barcode or name ?? are in many container support with different position 
 * 
 * @author mhaquell
 *
 */
public class ContainerSupport {
	
	// Support name
	public String name;
	public String barCode;
	
	public String categoryCode;

	// Position
	public String x;
	public String y;
	
	@JsonIgnore
	public ContainerSupportCategory getContainerSupportCategory(){
		try {
			//return new ObjectSGBDReference<ContainerSupportCategory>(ContainerSupportCategory.class, categoryCode).getObject();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
		return null;
	}
}
