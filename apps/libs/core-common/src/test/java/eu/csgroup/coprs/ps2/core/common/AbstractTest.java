package eu.csgroup.coprs.ps2.core.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
public abstract class AbstractTest {

    protected PodamFactory podamFactory = new PodamFactoryImpl();

    public abstract void setup();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        setup();
    }

}
