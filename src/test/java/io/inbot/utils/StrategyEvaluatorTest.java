package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.Optional;
import org.testng.annotations.Test;

@Test
public class StrategyEvaluatorTest {
    public void shouldPickBestStrategy() {
        // if you have no strategy, the result is always empty
        assertThat(StrategyEvaluator.evaluate()).isEqualTo(Optional.empty());
        // it works with just one strategy
        Optional<Integer> result = StrategyEvaluator.evaluate(() -> Optional.of(42));
        assertThat(result).isEqualTo(Optional.of(42));
        // it will pick the first non empty strategy
        Optional<Integer> result2 = StrategyEvaluator.evaluate(
                () -> Optional.empty(),
                () -> Optional.of(42),
                () -> Optional.of(666));
        assertThat(result2
        ).isEqualTo(Optional.of(42));
    }
}
