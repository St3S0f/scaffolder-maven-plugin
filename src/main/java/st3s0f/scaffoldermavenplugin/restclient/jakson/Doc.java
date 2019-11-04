package st3s0f.scaffoldermavenplugin.restclient.jakson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Doc {
    String g;
    String a;
    String latestVersion;
}
