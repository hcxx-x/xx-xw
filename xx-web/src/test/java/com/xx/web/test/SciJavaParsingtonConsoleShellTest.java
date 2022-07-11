package com.xx.web.test;

import org.junit.jupiter.api.Test;
import org.scijava.parsington.Variable;
import org.scijava.parsington.eval.DefaultTreeEvaluator;
import org.scijava.parsington.eval.EvaluatorConsole;

import java.io.IOException;


public class SciJavaParsingtonConsoleShellTest {

    public static void main(final String[] args) throws IOException {
        final DefaultTreeEvaluator evaluator = new DefaultTreeEvaluator();
        if (args.length > 0) {
            // Evaluate the given expressions.
            for (final String expression : args) {
                Object result = evaluator.evaluate(expression);
                if (result instanceof Variable) {
                    // Unwrap the variable.
                    result = evaluator.get((Variable) result);
                }
                System.out.println(result);
            }
        }
        else {
            // Show the REPL.
            new EvaluatorConsole(evaluator).showConsole();
        }
    }
}
