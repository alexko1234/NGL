package models.instance.common;

public enum TBoolean {
	TRUE(1),
	FALSE(0),
	UNSET(-1);

	public final Integer value;
	
	TBoolean(Integer value) {
		this.value = value;
	}
}
