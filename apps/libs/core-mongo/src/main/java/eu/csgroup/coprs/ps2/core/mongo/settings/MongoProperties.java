package eu.csgroup.coprs.ps2.core.mongo.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("mongo")
public class MongoProperties {

    private String authenticationDatabase = "admin";
    private String username;
    private String password;
    private String database;
    private String port;
    private String host;

}
