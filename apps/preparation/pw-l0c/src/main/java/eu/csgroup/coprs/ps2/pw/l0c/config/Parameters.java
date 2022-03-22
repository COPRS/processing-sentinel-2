package eu.csgroup.coprs.ps2.pw.l0c.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("pw.l0c")
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
