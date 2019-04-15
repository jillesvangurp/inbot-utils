package io.inbot.utils;

import org.testng.annotations.Test;

import static io.inbot.utils.PatternEvaluator.evaluator;
import static io.inbot.utils.PatternEvaluator.matches;
import static org.assertj.core.api.Assertions.assertThat;

@Test
public class PatternEvaluatorTest {

    public void shouldEvalPatterns() {
        PatternEvaluator<Integer, String> evaluator = evaluator(
            PatternEvaluator.equals(1,input->"one"),
            PatternEvaluator.equals(2,input->"two"),
            PatternEvaluator.equals(3,input->"three"),
            matches(input -> input>=3,input->"big")
        );
        assertThat(evaluator.evaluate(2).get()).isEqualTo("two");
        assertThat(evaluator.evaluate(20).get()).isEqualTo("big");
        assertThat(evaluator.evaluate(-1).isPresent()).isFalse();
    }
}
