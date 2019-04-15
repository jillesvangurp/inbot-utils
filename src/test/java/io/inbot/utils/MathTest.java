package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

@Test
public class MathTest {

    public void shouldRound() {
        assertThat(""+Math.round(1.12345, 2)).isEqualTo("1.12");
    }

    public void shouldPow() {
        assertThat(Math.pow(2, 2)).isEqualTo(4l);
        assertThat(Math.pow(-2, 3)).isEqualTo(-8l);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowIfExceedsMaxLong() {
        Math.pow(Long.MAX_VALUE, 2);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowIfExceedsMinLong() {
        Math.pow(Long.MIN_VALUE, 3);
    }

    public void shouldSafeAbs() {
        assertThat(Math.safeAbs(Integer.MIN_VALUE)).isEqualTo(Integer.MAX_VALUE);
        assertThat(Math.safeAbs(Long.MIN_VALUE)).isEqualTo(Long.MAX_VALUE);
        assertThat(Math.safeAbs(-1)).isEqualTo(1);
        assertThat(Math.safeAbs(-1l)).isEqualTo(1l);
        assertThat(Math.safeAbs(1)).isEqualTo(1);
        assertThat(Math.safeAbs(1l)).isEqualTo(1l);
        assertThat(Math.safeAbs(Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
        assertThat(Math.safeAbs(Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
    }

    public void shouldNormalize() {
        assertThat(Math.normalize(.6666, 1)).isLessThanOrEqualTo(1.0).isGreaterThanOrEqualTo(0);
        assertThat(Math.normalize(666.6666, 66666)).isLessThanOrEqualTo(1.0).isGreaterThanOrEqualTo(0);
        assertThat(Math.normalize(1, 1)).isLessThan(Math.normalize(10, 1)).isLessThanOrEqualTo(1.0).isGreaterThanOrEqualTo(0);;
    }
}
