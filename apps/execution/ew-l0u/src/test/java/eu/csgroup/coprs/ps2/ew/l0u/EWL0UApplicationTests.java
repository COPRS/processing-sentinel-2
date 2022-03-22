package eu.csgroup.coprs.ps2.ew.l0u;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL0UApplicationTests {

    @Test
    void main() {
        EWL0UApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
