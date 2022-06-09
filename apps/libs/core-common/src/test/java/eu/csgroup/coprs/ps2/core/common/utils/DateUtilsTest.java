package eu.csgroup.coprs.ps2.core.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DateUtilsTest {

    private static final Instant instant = Instant.ofEpochMilli(1656582418000L);
    private static final Long instantEnd = 1656586139000L;
    private static final String expected = "2022-06-30T09:46:58.000Z";
    private static final String expectedShort = "20220630T094658";
    private static final String expectedLong = "2022-06-30T09:46:58.000000Z";
    private static final String elapsed = "01:02:01";


    @Test
    void toDate() {
        assertEquals(expected, DateUtils.toDate(instant));
    }

    @Test
    void toShortDate() {
        assertEquals(expectedShort, DateUtils.toShortDate(instant));
    }

    @Test
    void toLongDate() {
        assertEquals(expectedLong, DateUtils.toLongDate(instant));
    }

    @Test
    void toInstant() {
        assertEquals(instant, DateUtils.toInstant(expected));
    }

    @Test
    void elapsed() {
        Clock spyClock = Mockito.spy(Clock.class);
        try (MockedStatic<Clock> clockMock = Mockito.mockStatic(Clock.class)) {

            clockMock.when(() -> Clock.systemUTC()).thenReturn(spyClock);
            Mockito.when(spyClock.instant()).thenReturn(Instant.ofEpochMilli(instantEnd));

            assertEquals(elapsed, DateUtils.elapsed(instant));
        }
    }

}
