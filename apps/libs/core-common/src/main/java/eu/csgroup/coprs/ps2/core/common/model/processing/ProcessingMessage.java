package eu.csgroup.coprs.ps2.core.common.model.processing;

import eu.csgroup.coprs.ps2.core.common.config.ChainProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProcessingMessage {

    // --------------------------------------------- Mandatory ----------------------------------------------

    // Unique Event Identifier
    // Regex: "[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}"
    // Example: 34995e37-196c-4332-8075-c6c6891a69cc"
    @Size(min = 36, max = 36)
    @NotBlank
    @ToString.Include
    @EqualsAndHashCode.Include
    private String uid;

    // Date of the message creation
    // <YYYY>-<MM>-<DD>T<hh>:<mm>:<ss>.<microseconds>Z
    // "2021-02-22T16:19:04.707Z"
    @Size(min = 24, max = 24)
    @NotBlank
    @ToString.Include
    private String creationDate;

    @NotBlank
    private String rsChainVersion = ChainProperties.getChainVersion();

    // Sentinel Mission Name
    // “S1” | “S2” | “S3” | ""
    @Size(min = 0, max = 2)
    @NotNull
    private String missionId;

    // Sentinel Satellite Name
    // “A” | “B” | “C” | "D" | ""
    @Size(min = 0, max = 1)
    @NotNull
    private String satelliteId;

    // Key to identify a processing item (product, aux file, etc) - basically a file name
    // "S1__AUX_ICE_V20210112T120000_G20210113T044311.SAFE.zip"
    @Size(min = 0, max = 512)
    @NotNull
    private String keyObjectStorage;

    // Object storage URL path where the item can be found.
    // s3://rs-<mission>-<type><extension>
    @NotNull
    private String storagePath;

    // Format SCREAMING_SNAKE_CASE
    // Sentinel-1 sample: “L0_SEGMENT”, “SPP_OBS”, “SPP_OBS_ZIP”
    // Multi sentinel sample: “AUXILIARY_FILE_ZIP”
    @Size(max = 32)
    @NotBlank
    @ToString.Include
    private ProductFamily productFamily;

    // The POD that has created the message.
    // "s1pro-compression-worker-10"
    @Size(max = 64)
    @NotBlank
    private String podName;

    // Actions allowed on an event after a failed processing or a processing that ends with a warning.
    @Size(max = 4)
    @NotNull
    private EventAction[] allowedActions;

    // Number of retry for this event. The value is equal to 0 for the first time.
    @Max(64)
    @NotNull
    private Integer retryCounter;

    // Additional parameters that are specific to the service.
    // Nested JSON
    @NotNull
    private Map<String, Object> additionalFields = new HashMap<>();

    // --------------------------------------------- Optional -----------------------------------------------

    // Metadata information
    // Nested JSON
    private Map<String, Object> metadata = new HashMap<>();


    // Processing workflow follow-up
    // By default, the demand type is equal to "NOMINAL".
    private DemandType demandType;

    // Debug mode
    private Boolean debug;

    // Amalfi status.
    private OqcFlag oqcFlag;

    // Product timeliness
    private Timeliness timeliness;

}
