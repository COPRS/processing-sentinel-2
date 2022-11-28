package eu.csgroup.coprs.ps2.ew.l2ds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL2DSApplicationTests {

    @Test
    void main() {
        EWL2DSApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
