package io.inbot.utils.maps;

import static io.inbot.utils.maps.MapFactory.entry;
import static io.inbot.utils.maps.MapFactory.map;
import static io.inbot.utils.maps.MapFactory.richMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.annotations.Test;

@Test
public class MapFactoryTest {

    public void shouldCreateMap() {
        Map<String,Integer> map = map(entry("one", 1), entry("two",2), entry("three",3));
        assertThat(map.get("one")).isEqualTo(1);
        assertThat(map.get("two")).isEqualTo(2);
        assertThat(map.get("three")).isEqualTo(3);
    }

    public void shouldCreateConcurrentHashMap() {
        Map<String,Integer> map = map(() -> new ConcurrentHashMap<>(), entry("one", 1));
        assertThat(map.get("one")).isEqualTo(1);
    }

    public void shouldAddMultiMapCapability() {
        RichMultiMap<String, Integer> mm = MapFactory.multiMap();
        mm.putValue("x", 1);
        mm.putValue("x", 2);
        Collection<Integer> c = mm.get("x");
        assertThat(c.contains(1)).isEqualTo(true);
        assertThat(c.contains(2)).isEqualTo(true);
        assertThat(c.contains(3)).isEqualTo(false);
    }

    public void shouldGetOptionalFromRichMap() {
        RichMap<String, Integer> rm = richMap(
            entry("1", 1),
            entry("2",2)
        );
        assertThat(rm.maybeGet("2").isPresent()).isTrue();
        assertThat(rm.maybeGet("3").isPresent()).isFalse();
    }
}
