package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.beust.jcommander.internal.Lists;
import java.util.List;
import org.testng.annotations.Test;

@Test
public class SimpleStringTrieTest {
    public void shouldTriePrefixes() {
        SimpleStringTrie trie = new SimpleStringTrie();
        // these prefixes we want to find
        trie.add("foofo");
        trie.add("foofoo");
        trie.add("fooboooooo");
        trie.add("1234");
        trie.add("123");
        // these inputs have no prefix that was added
        assertThat(trie.get("foo").isPresent()).isFalse();
        assertThat(trie.get("foobar").isPresent()).isFalse();
        assertThat(trie.get("abc").isPresent()).isFalse();
        assertThat(trie.get("1200").isPresent()).isFalse();
        // these inputs have matching prefixes that were added
        assertThat(trie.get("foofo").get()).isEqualTo("foofo");
        assertThat(trie.get("foofoxxxxxxxxxx").get()).isEqualTo("foofo");
        assertThat(trie.get("foofoo").get()).isEqualTo("foofoo");
        assertThat(trie.get("foofoooo").get()).isEqualTo("foofoo");
        assertThat(trie.get("fooboooooo").get()).isEqualTo("fooboooooo");
        assertThat(trie.get("1234").get()).isEqualTo("1234");
        assertThat(trie.get("1230").get()).isEqualTo("123");
    }

    public void shouldProduceAllExceptAlbania() {
        SimpleStringTrie trie = new SimpleStringTrie();
        trie.add("australia");
        trie.add("austria");
        trie.add("albania");
        assertThat(ArrayFoo.stringify(trie.match("au"))).contains("austria","australia");
    }

    public void shouldProduceAllNestedPrefixes() {
        SimpleStringTrie trie = new SimpleStringTrie();
        trie.add("ab");
        trie.add("abc");
        trie.add("abcd");
        trie.add("abcde");
        assertThat(trie.match("ab").size()).isEqualTo(4);
        assertThat(ArrayFoo.stringify(trie.match("a"))).contains("ab","abc","abcd","abcde");
        assertThat(trie.match("abc").size()).isEqualTo(3);
        assertThat(ArrayFoo.stringify(trie.match("abc"))).contains("abc","abcd","abcde").doesNotContain("ab\"");
    }

    public void shouldProduceAllPostFixes() {
        SimpleStringTrie trie = new SimpleStringTrie();

        List<String> strings = Lists.newArrayList("a","aa","aaa","ab","abb","aab");
        strings.forEach(trie::add);

        List<String> matches = trie.match("a");
        assertThat(matches.size()).isEqualTo(strings.size());

    }

    public void shouldMatch() {
        SimpleStringTrie trie = new SimpleStringTrie();
        trie.add("foofoo");
        trie.add("foobar");
        trie.add("bar");

        List<String> match = trie.match("fo");

        assertThat(match.contains("foofoo")).isTrue();
        assertThat(match.contains("foobar")).isTrue();
        assertThat(match.contains("bar")).isFalse();

        List<String> match2 = trie.match("fff");
        assertThat(match2.size()).isEqualTo(0);
    }

    public void shouldReturnMatchingPrefix() {
        SimpleStringTrie trie = new SimpleStringTrie();
        trie.add("a");
        trie.add("b");
        trie.add("c");

        List<String> match = trie.match("abc");
        assertThat(match.contains("a")).isTrue();
        assertThat(match.contains("abc")).isFalse();

    }
}
