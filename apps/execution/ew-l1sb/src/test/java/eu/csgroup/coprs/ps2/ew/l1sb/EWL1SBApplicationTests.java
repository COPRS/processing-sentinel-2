package eu.csgroup.coprs.ps2.ew.l1sb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL1SBApplicationTests {

    @Test
    void main() {
        EWL1SBApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
