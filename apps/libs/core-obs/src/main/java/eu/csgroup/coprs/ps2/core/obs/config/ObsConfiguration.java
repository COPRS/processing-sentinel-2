/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.csgroup.coprs.ps2.core.obs.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.transfer.s3.S3ClientConfiguration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;

import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

@Slf4j
@Configuration
public class ObsConfiguration {

    private final ObsProperties obsProperties;

    public ObsConfiguration(ObsProperties obsProperties) {
        this.obsProperties = obsProperties;
    }

    @Bean
    public S3Client s3Client() {
        log.debug("S3 Client enabled !");
        return S3Client.builder()
                .credentialsProvider(staticCredentialsProvider())
                .endpointOverride(URI.create(obsProperties.getEndpoint()))
                .region(Region.of(obsProperties.getRegion()))
                .serviceConfiguration(s3Configuration())
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        log.debug("S3 Async Client enabled !");
        return S3AsyncClient.builder()
                .credentialsProvider(staticCredentialsProvider())
                .endpointOverride(URI.create(obsProperties.getEndpoint()))
                .region(Region.of(obsProperties.getRegion()))
                .serviceConfiguration(s3Configuration())
                .asyncConfiguration(builder -> builder.advancedOption(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR, threadPoolExecutor()))
                .build();
    }

    @Bean
    public S3TransferManager s3TransferManager() {
        return S3TransferManager.builder()
                .s3ClientConfiguration(s3ClientConfiguration())
                .transferConfiguration(configuration -> configuration.executor(threadPoolExecutor()))
                .build();
    }

    public StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(obsProperties.getAccessKey(), obsProperties.getSecretKey()));
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
    }

    private S3ClientConfiguration s3ClientConfiguration() {
        return S3ClientConfiguration.builder()
                .credentialsProvider(staticCredentialsProvider())
                .region(Region.of(obsProperties.getRegion()))
                .endpointOverride(URI.create(obsProperties.getEndpoint()))
                .minimumPartSizeInBytes(obsProperties.getMinimumPartSize() * MB)
                .maxConcurrency(obsProperties.getMaxConcurrency())
                .targetThroughputInGbps(obsProperties.getMaxThroughput())
                .build();
    }

    private ThreadPoolExecutor threadPoolExecutor() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                8,
                16,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10_000),
                new ThreadFactoryBuilder().build());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }

}
