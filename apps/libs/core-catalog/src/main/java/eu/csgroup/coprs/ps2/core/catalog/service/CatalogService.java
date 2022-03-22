package eu.csgroup.coprs.ps2.core.catalog.service;

import eu.csgroup.coprs.ps2.core.catalog.exception.CatalogQueryException;
import eu.csgroup.coprs.ps2.core.catalog.model.AuxCatalogData;
import eu.csgroup.coprs.ps2.core.catalog.model.AuxProductType;
import eu.csgroup.coprs.ps2.core.catalog.model.SessionCatalogData;
import eu.csgroup.coprs.ps2.core.catalog.settings.CatalogProperties;
import eu.csgroup.coprs.ps2.core.common.utils.DateUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class CatalogService {

    private static final String AUX_ROUTE = "/metadata/{productFamily}/search";
    private static final String SESSION_ROUTE = "/edrsSession/sessionId/{sessionId}";

    private final CatalogProperties catalogProperties;

    private WebClient webClient;

    public CatalogService(CatalogProperties catalogProperties) {
        this.catalogProperties = catalogProperties;
    }

    @PostConstruct
    public void init() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, catalogProperties.getTimeout() * 1000)
                .responseTimeout(Duration.ofSeconds(catalogProperties.getTimeout()))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(catalogProperties.getTimeout()))
                        .addHandlerLast(new WriteTimeoutHandler(catalogProperties.getTimeout())));

        webClient = WebClient.builder()
                .baseUrl(catalogProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }


    public Optional<AuxCatalogData> retrieveLatestAuxData(AuxProductType productType, String satellite, Instant from, Instant to) {

        log.debug("Retrieving latest AUX data for product type {}", productType.name());

        final List<AuxCatalogData> auxCatalogData = retrieveAuxData(productType, satellite, from, to);

        // TODO how to get the one ?
        // TODO bandIndexId ???

        return auxCatalogData.stream().findAny();

    }

    private List<AuxCatalogData> retrieveAuxData(AuxProductType productType, String satellite, Instant from, Instant to) {

        log.debug("Retrieving AUX data for product type {}", productType.name());

        List<AuxCatalogData> auxCatalogDataList = Collections.emptyList();

        AuxCatalogData[] data = query(
                uriBuilder -> uriBuilder
                        .path(AUX_ROUTE)
                        .queryParam("productType", productType.name())
                        .queryParam("mode", catalogProperties.getMode())
                        .queryParam("satellite", satellite)
                        .queryParam("t0", DateUtils.toLongDate(from))
                        .queryParam("t1", DateUtils.toLongDate(to))
                        .build(catalogProperties.getAuxProductFamily()),
                AuxCatalogData[].class);

        if (data != null) {
            auxCatalogDataList = Arrays.stream(data).toList();
        }

        log.debug("Found {} AUX data for product type {}", auxCatalogDataList.size(), productType.name());

        return auxCatalogDataList;
    }

    public List<SessionCatalogData> retrieveSessionData(String sessionId) {

        log.debug("Retrieving SESSION data for session {}", sessionId);

        List<SessionCatalogData> sessionCatalogDataList = Collections.emptyList();

        SessionCatalogData[] data = query(
                uriBuilder -> uriBuilder
                        .path(SESSION_ROUTE)
                        .build(sessionId),
                SessionCatalogData[].class
        );

        if (data != null) {
            sessionCatalogDataList = Arrays.stream(data).toList();
        }

        log.debug("Found {} SESSION data for session {}", sessionCatalogDataList.size(), sessionId);

        return sessionCatalogDataList;
    }

    private <T> T query(Function<UriBuilder, URI> uriFunction, Class<T> clazz) {

        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        response.createException()
                                .flatMap(e -> Mono.error(new CatalogQueryException("Error querying Catalog: Client Error", e)))
                )
                .onStatus(HttpStatus::is5xxServerError, response ->
                        response.createException()
                                .flatMap(e -> Mono.error(new CatalogQueryException("Error querying Catalog: Server Error", e)))
                )
                .bodyToMono(clazz)
                .retryWhen(
                        Retry.backoff(catalogProperties.getMaxRetry(), Duration.ofSeconds(2))
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new CatalogQueryException("Error querying Catalog", retrySignal.failure())))
                .block();
    }

}
