package st3s0f.scaffoldermavenplugin.restclient.jakson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Response {
    List<Doc> docs;
}
