package lims.models.experiment;

import java.util.Date;

import lims.models.instrument.Instrument;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import play.data.validation.Constraints.Required;

@JsonSerialize(include = Inclusion.NON_NULL)
public class Experiment {
	@Required
	public String containerSupportCode;
	
	public Date date;
	public Instrument instrument;	
}
