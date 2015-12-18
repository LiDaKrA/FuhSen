package unibonn.eis.fuhsen.constansimport de.ddb.common.constants.FacetEnum;

public enum SettingEnum {
	
	RDF_TURTLE("TURTLE"),
	RDF_JSON_LD("JSON-LD"),	
	CT_TURTLE("text/turtle"),
	CT_JSON_LD("application/ld+json")
	
	private String value
	
	private SettingEnum(String value) {
		this.value = value
	}

	public String getValue() {
		return value
	}
}
