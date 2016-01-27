
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ISA ontology reference schema
 * <p>
 * JSON-schema representing an ontology reference or annotation in the ISA model (for fields that are required to be ontology annotations)
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "comments",
    "annotationValue",
    "termSource",
    "termAccession"
})
public class MeasurementType {

    @JsonProperty("comments")
    public List<Comment> comments = new ArrayList<Comment>();
    @JsonProperty("annotationValue")
    public Object annotationValue;
    /**
     * The abbreviated ontology name. It should correspond to one of the sources as specified in the ontologySourceReference section of the Investigation.
     * 
     */
    @JsonProperty("termSource")
    public String termSource;
    @JsonProperty("termAccession")
    public URI termAccession;

}
