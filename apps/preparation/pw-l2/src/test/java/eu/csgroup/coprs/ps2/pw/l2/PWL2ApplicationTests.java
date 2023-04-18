package eu.csgroup.coprs.ps2.pw.l2;

import eu.csgroup.coprs.ps2.pw.l2.PWL2Application;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PWL2ApplicationTests {

    @Test
    void main() {
        PWL2Application.main(new String[]{("--spring.profiles.active=dev,test")});
        assertTrue(true);
    }

}
