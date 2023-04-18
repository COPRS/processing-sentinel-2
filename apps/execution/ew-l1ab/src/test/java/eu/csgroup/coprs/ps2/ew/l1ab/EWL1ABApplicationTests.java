package eu.csgroup.coprs.ps2.ew.l1ab;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL1ABApplicationTests {

    @Test
    void main() {
        EWL1ABApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
