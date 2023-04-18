package eu.csgroup.coprs.ps2.core.pw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "eu.csgroup.coprs.ps2")
public class MongoConfiguration {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Autowired
    public void setMapKeyDotReplacement(MappingMongoConverter mongoConverter) {
        mongoConverter.setMapKeyDotReplacement("#");
    }

}
