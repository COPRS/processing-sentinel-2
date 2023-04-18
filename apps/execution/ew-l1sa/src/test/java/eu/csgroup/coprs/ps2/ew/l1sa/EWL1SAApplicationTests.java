package eu.csgroup.coprs.ps2.ew.l1sa;

import eu.csgroup.coprs.ps2.ew.l1sa.EWL1SAApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL1SAApplicationTests {

    @Test
    void main() {
        EWL1SAApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
