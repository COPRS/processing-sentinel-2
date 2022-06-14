package eu.csgroup.coprs.ps2.pw.l0c.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pw.l0c")
public class L0cPreparationProperties {

    /**
     * Folder where L0U input files (DS/GR in DT folders) are stored. Mount-point to shared disk.
     */
    private String inputFolderRoot;

}
