package eu.csgroup.coprs.ps2.core.common.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractTest {

    protected PodamFactory podamFactory = new PodamFactoryImpl();
    protected AutoCloseable autoCloseable;

    public abstract void setup() throws Exception;

    public abstract void teardown() throws Exception;

    @BeforeEach
    public void init() throws Exception {
        autoCloseable = MockitoAnnotations.openMocks(this);
        setup();
    }

    @AfterEach
    public void cleanup() throws Exception {
        teardown();
        autoCloseable.close();
    }

}
