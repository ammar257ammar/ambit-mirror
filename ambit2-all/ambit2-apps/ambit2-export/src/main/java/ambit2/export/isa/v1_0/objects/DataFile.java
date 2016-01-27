
package ambit2.export.isa.v1_0.objects;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * ISA data schema
 * <p>
 * JSON-schema representing a data file in the ISA model
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "@id",
    "name",
    "type",
    "comments"
})
public class DataFile {

    @JsonProperty("@id")
    public URI Id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public DataFile.Type type;
    @JsonProperty("comments")
    public List<Comment> comments = new ArrayList<Comment>();

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        RAW_DATA_FILE("Raw Data File"),
        DERIVED_DATA_FILE("Derived Data File"),
        IMAGE_FILE("Image File");
        private final String value;
        private static Map<String, DataFile.Type> constants = new HashMap<String, DataFile.Type>();

        static {
            for (DataFile.Type c: values()) {
                constants.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static DataFile.Type fromValue(String value) {
            DataFile.Type constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
