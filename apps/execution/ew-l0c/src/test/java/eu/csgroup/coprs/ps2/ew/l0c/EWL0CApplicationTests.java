package eu.csgroup.coprs.ps2.ew.l0c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EWL0CApplicationTests {

    @Test
    void main() {
        EWL0CApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
