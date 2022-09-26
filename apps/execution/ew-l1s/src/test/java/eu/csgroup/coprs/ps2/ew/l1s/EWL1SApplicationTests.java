package eu.csgroup.coprs.ps2.ew.l1s;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL1SApplicationTests {

    @Test
    void main() {
        EWL1SApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
