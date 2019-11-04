package st3s0f.scaffoldermavenplugin.restclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import st3s0f.scaffoldermavenplugin.restclient.jakson.MvnCentralRepoResponse;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.function.BiFunction;

import static java.lang.String.format;

@SpringBootApplication
public class RestClient {

    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(RestClient.class, args);
        BiFunction<String,String,String> f = (BiFunction<String, String, String>) ac.getBean("getLatestVersionOf");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> {
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 3128));
                    requestFactory.setProxy(proxy);
                    return requestFactory;
                })
                .build();
    }

    @Bean
    public BiFunction<String,String,String> getLatestVersionOf(RestTemplate restTemplate) {
        return (g, a) -> {
            MvnCentralRepoResponse mcrr = restTemplate.getForObject(format(
                    "https://search.maven.org/solrsearch/select?q=g:\"%s\"a:\"%s\"",
                    g,
                    a
            ), MvnCentralRepoResponse.class);

            return mcrr == null
                    ? null
                    : mcrr.getResponse() == null
                    ? null
                    : mcrr.getResponse().getDocs() == null || mcrr.getResponse().getDocs().isEmpty()
                    ? null
                    : mcrr.getResponse().getDocs().get(0).getLatestVersion()
                    ;
        };
    }

}
