package eu.csgroup.coprs.ps2.core.common.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("dev")
@SpringBootTest(classes = TestApplication.class)
@TestPropertySource("classpath:application-test.properties")
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yaml")
public abstract class AbstractSpringBootTest extends AbstractTest {

}
