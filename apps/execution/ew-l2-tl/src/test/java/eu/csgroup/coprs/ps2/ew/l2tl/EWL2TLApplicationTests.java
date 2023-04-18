package eu.csgroup.coprs.ps2.ew.l2tl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL2TLApplicationTests {

    @Test
    void main() {
        EWL2TLApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
