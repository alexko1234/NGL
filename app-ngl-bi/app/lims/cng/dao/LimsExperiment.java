package lims.cng.dao;

import java.util.Date;

public class LimsExperiment {
	public Date date;
	public String code;
	public String categoryCode;

	@Override
	public String toString() {
		return "LimsExperiment [date=" + date + ", code=" + code + ", type="
				+ categoryCode + "]";
	}

}
