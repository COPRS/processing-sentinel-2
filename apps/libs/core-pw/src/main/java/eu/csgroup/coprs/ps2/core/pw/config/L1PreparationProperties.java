package eu.csgroup.coprs.ps2.core.pw.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class L1PreparationProperties implements PWProperties {

    /**
     * Path to the root of the filesystem shared between L1 containers
     */
    private String sharedFolderRoot;

}
