/*
 * Copyright 2023 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
