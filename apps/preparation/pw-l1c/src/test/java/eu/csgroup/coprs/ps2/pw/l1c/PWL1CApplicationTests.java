package eu.csgroup.coprs.ps2.pw.l1c;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PWL1CApplicationTests {

    @Test
    void main() {
        PWL1CApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
