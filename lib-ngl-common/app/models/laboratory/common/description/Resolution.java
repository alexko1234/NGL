package models.laboratory.common.description;


/**
 * Value of the resolution of final possible state
 * @author ejacoby
 *
 */
public class Resolution{

	public Long id;
	
	public String name;
	
	public String code;
	
	public Resolution() {
		super();
	}

	public Resolution(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resolution other = (Resolution) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
}
