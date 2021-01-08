package ambit2.rest.substance.study.jsonld;

import java.util.List;

import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldNamespace;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldProperty;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;

@JsonldResource
@JsonldNamespace(name = "schema", uri = "https://schema.org/")
@JsonldType("schema:Dataset")
public class SubstanceStudyJsonldEntity {
	
	@JsonldId
	@JsonldProperty("https://schema.org/identifier")
	private String datasetId;
	
	@JsonldProperty("schema:url")
	private String url;
	
	@JsonldProperty("schema:citation")
	private String citation;

	@JsonldProperty("schema:variableMeasured")
	private List<EffectRecordJsonldEntity> variableMeasured;

	public SubstanceStudyJsonldEntity(){}

	public SubstanceStudyJsonldEntity(String datasetId, String url, String citation,
			List<EffectRecordJsonldEntity> variableMeasured) {
		super();
		this.datasetId = datasetId;
		this.url = url;
		this.citation = citation;
		this.variableMeasured = variableMeasured;
	}


	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCitation() {
		return citation;
	}

	public void setCitation(String citation) {
		this.citation = citation;
	}

	public List<EffectRecordJsonldEntity> getVariableMeasured() {
		return variableMeasured;
	}

	public void setVariableMeasured(List<EffectRecordJsonldEntity> variableMeasured) {
		this.variableMeasured = variableMeasured;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((citation == null) ? 0 : citation.hashCode());
		result = prime * result + ((datasetId == null) ? 0 : datasetId.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((variableMeasured == null) ? 0 : variableMeasured.hashCode());
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
		SubstanceStudyJsonldEntity other = (SubstanceStudyJsonldEntity) obj;
		if (citation == null) {
			if (other.citation != null)
				return false;
		} else if (!citation.equals(other.citation))
			return false;
		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (variableMeasured == null) {
			if (other.variableMeasured != null)
				return false;
		} else if (!variableMeasured.equals(other.variableMeasured))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubstanceStudyJsonldEntity [datasetId=" + datasetId + ", url=" + url + ", citation=" + citation
				+ ", variableMeasured=" + variableMeasured + "]";
	}

}
