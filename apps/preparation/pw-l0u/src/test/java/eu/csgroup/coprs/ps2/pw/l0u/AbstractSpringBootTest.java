package eu.csgroup.coprs.ps2.pw.l0u;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("dev")
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractSpringBootTest extends AbstractTest {

}
