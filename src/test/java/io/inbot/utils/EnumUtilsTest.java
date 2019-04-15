package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

@Test
public class EnumUtilsTest {
    enum Foo {
        foo,bar;
    }

    public void shouldGetEnumValue() {
        assertThat(EnumUtils.getEnumValue(Foo.class, "foo").get()).isEqualTo(Foo.foo);
        assertThat(EnumUtils.getEnumValue(Foo.class, "boo")).isEmpty();
    }
}
