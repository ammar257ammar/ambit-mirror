package ambit2.rest.substance.study.jsonld;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldNamespace;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldProperty;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;

@JsonldResource
@JsonldNamespace(name = "schema", uri = "https://schema.org/")
@JsonldType("schema:PropertyValue")
public class EffectRecordJsonldEntity {
	
	@JsonldProperty("https://schema.org/name")
	private String name;
	
	@JsonldProperty("https://schema.org/unitText")
	private String unitText;

	public EffectRecordJsonldEntity() {}
	
	public EffectRecordJsonldEntity(String name, String unitText) {
		super();
		this.name = name;
		this.unitText = unitText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnitText() {
		return unitText;
	}

	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((unitText == null) ? 0 : unitText.hashCode());
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
		EffectRecordJsonldEntity other = (EffectRecordJsonldEntity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (unitText == null) {
			if (other.unitText != null)
				return false;
		} else if (!unitText.equals(other.unitText))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubstanceStudyEffectRecordEntity [name=" + name + ", unitText=" + unitText + "]";
	}

}
