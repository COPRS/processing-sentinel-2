package eu.csgroup.coprs.ps2.core.common.model.aux;

public interface AuxFile {

    // Used for Enums listing AUX files required at each level, with custom properties as required.
    // Enum name must match AuxProductType.

    AuxProductType getAuxProductType();
    AuxFolder getFolder();

}
