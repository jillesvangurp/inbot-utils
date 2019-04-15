package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;

@Test
public class ArrayFooTest {
    String[] arr = ArrayFoo.array("foo","bar");

    public void shouldCombineArrays() {
        String[] combined = ArrayFoo.combine(arr,arr);
        assertThat(combined.length).isEqualTo(arr.length + arr.length);
    }

    public void shouldConvertToSet() {
        String[] combined = ArrayFoo.combine(arr);
        Set<String> set = ArrayFoo.setOf(combined);
        assertThat(set.size()).isEqualTo(arr.length);
    }

    public void shouldStringify() {
        String str = ArrayFoo.stringify(arr);
        assertThat(str).startsWith("[");
        assertThat(str).containsSequence(arr[0],",",arr[1]);
        assertThat(str).endsWith("]");
    }

    public void shouldStringifyList() {
        String str = ArrayFoo.stringify(Arrays.asList(arr));
        assertThat(str).startsWith("[");
        assertThat(str).containsSequence(arr[0],",",arr[1]);
        assertThat(str).endsWith("]");
    }

    public void shouldTransformCollectionToArray() {
        List<String> list = new ArrayList<>();
        list.add("foo");
        list.add("bar");
        String[] array = ArrayFoo.array(list);
        assertThat(array[0]).isEqualTo("foo");
        assertThat(array[1]).isEqualTo("bar");
    }
}
