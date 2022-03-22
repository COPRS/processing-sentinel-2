package eu.csgroup.coprs.ps2.pw.l0c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PWL0CApplicationTests {

    @Test
    void main() {
        PWL0CApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
