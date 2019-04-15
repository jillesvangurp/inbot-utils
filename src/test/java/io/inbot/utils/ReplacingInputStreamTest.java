package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class ReplacingInputStreamTest {

    @DataProvider
    public Object[][] samples() {
        return new Object[][] {
            {"abcdefghijk", "def","fed", "abcfedghijk"},
            {"abcdefghijkabcdefghijkabcdefghijk", "def","fed", "abcfedghijkabcfedghijkabcfedghijk"},
            {"abcdefghijk", "def","def", "abcdefghijk"},
            {"abcdefghijk", "def","", "abcghijk"},
            {"abcdefghijk", "def",null, "abcghijk"},
            {"abcdefghijk", "d","dd", "abcddefghijk"},
            {"", "d","dd", ""}
        };
    }

    @DataProvider
    public Object[][] newlineSamples() {
        return new Object[][] {
            {"foo\n\rbar\r","foo\nbar\n"},
            {"foo\rbar\r","foo\nbar\n"}
        };
    }

    @Test(dataProvider="newlineSamples")
    public void shouldFixNewlines(String input, String expected) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        try(InputStream ris = ReplacingInputStream.newLineNormalizingInputStream(bis)) {
            String result = new String(IOUtils.readBytes(ris), StandardCharsets.UTF_8);
            assertThat(result).isEqualTo(expected);
        }
    }

    @Test(dataProvider="samples")
    public void shouldReplace(String input, String pattern, String replacement, String expected) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        byte[] replacementBytes=null;
        if(replacement!=null) {
            replacementBytes = replacement.getBytes(StandardCharsets.UTF_8);
        }
        try(ReplacingInputStream ris = new ReplacingInputStream(bis, pattern.getBytes(StandardCharsets.UTF_8), replacementBytes)) {
            String result = new String(IOUtils.readBytes(ris), StandardCharsets.UTF_8);
            assertThat(result).isEqualTo(expected);
        }
    }

    @Test(dataProvider="samples")
    public void shouldReplaceUsingStringConstructor(String input, String pattern, String replacement, String expected) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        try(ReplacingInputStream ris = new ReplacingInputStream(bis, pattern, replacement)) {
            assertThat(new String(IOUtils.readBytes(ris), StandardCharsets.UTF_8)).isEqualTo(expected);
        }
    }
}
