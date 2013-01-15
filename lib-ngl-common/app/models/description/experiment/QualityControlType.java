package models.description.experiment;

import java.util.List;

import models.description.common.CommonInfoType;

public class QualityControlType{

	public Long id;

	public List<Protocol> protocols; 
	
	public List<InstrumentUsedType> instrumentTypes;
	
	public CommonInfoType commonInfoType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Protocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<Protocol> protocols) {
		this.protocols = protocols;
	}

	public List<InstrumentUsedType> getInstrumentTypes() {
		return instrumentTypes;
	}

	public void setInstrumentTypes(List<InstrumentUsedType> instrumentTypes) {
		this.instrumentTypes = instrumentTypes;
	}

	public CommonInfoType getCommonInfoType() {
		return commonInfoType;
	}

	public void setCommonInfoType(CommonInfoType commonInfoType) {
		this.commonInfoType = commonInfoType;
	}

	
}
