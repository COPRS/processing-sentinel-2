package eu.csgroup.coprs.ps2.pw.l0u;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractTest {

    protected PodamFactory podamFactory = new PodamFactoryImpl();
    protected AutoCloseable autoCloseable;

    public abstract void setup();

    @BeforeEach
    public void init() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        setup();
    }

    @AfterEach
    public void cleanup() throws Exception {
        autoCloseable.close();
    }

}
