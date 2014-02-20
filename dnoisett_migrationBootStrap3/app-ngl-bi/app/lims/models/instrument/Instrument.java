package lims.models.instrument;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include = Inclusion.NON_NULL)
public class Instrument {
	
	public String code;
	public String categoryCode;
	public Boolean active;
	public String path;

}
