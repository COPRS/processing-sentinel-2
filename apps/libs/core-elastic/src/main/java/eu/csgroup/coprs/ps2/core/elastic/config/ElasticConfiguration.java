package eu.csgroup.coprs.ps2.core.elastic.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@Configuration
@EnableElasticsearchRepositories(basePackages = "eu.csgroup.coprs.ps2")
public class ElasticConfiguration {

    private final ElasticProperties elasticProperties;

    public ElasticConfiguration(ElasticProperties elasticProperties) {
        this.elasticProperties = elasticProperties;
    }

    @Bean
    public RestHighLevelClient client() {

        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticProperties.getHost() + ":" + elasticProperties.getPort())
                .build();

        return RestClients.create(clientConfiguration).rest(); // NOSONAR
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

}
