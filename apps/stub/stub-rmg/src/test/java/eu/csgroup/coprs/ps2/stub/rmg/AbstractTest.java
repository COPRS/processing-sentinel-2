package eu.csgroup.coprs.ps2.stub.rmg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
public abstract class AbstractTest {

    protected PodamFactory podamFactory = new PodamFactoryImpl();

    abstract void cleanup();

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
        cleanup();
    }

    @AfterEach
    public void after() {
        cleanup();
    }

}
