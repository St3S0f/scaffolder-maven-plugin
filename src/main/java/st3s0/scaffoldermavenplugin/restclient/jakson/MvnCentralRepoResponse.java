package st3s0.scaffoldermavenplugin.restclient.jakson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MvnCentralRepoResponse {
    Response response;
}
