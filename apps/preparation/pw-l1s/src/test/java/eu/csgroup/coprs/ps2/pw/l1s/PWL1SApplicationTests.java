package eu.csgroup.coprs.ps2.pw.l1s;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PWL1SApplicationTests {

    @Test
    void main() {
        PWL1SApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
