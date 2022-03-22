package eu.csgroup.coprs.ps2.stub.rmg.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("stub.rmg")
public class Parameters {

    /**
     * Some parameter
     */
    private String foo;

    /**
     * Another parameter
     */
    private String bar;

}
