package eu.csgroup.coprs.ps2.pw.l0u;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PWL0UApplicationTests {

    @Test
    void main() {
        PWL0UApplication.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
