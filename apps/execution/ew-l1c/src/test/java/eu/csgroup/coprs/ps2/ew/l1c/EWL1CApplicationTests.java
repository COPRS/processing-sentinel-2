package eu.csgroup.coprs.ps2.ew.l1c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL1CApplicationTests {

    @Test
    void main() {
        EWL1CApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
