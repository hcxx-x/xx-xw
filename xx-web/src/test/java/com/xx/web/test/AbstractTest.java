package com.xx.web.test;

import org.scijava.parsington.Function;
import org.scijava.parsington.Group;
import org.scijava.parsington.SyntaxTree;
import org.scijava.parsington.Variable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

/**
 * Base class for unit test classes.
 *
 * @author Curtis Rueden
 */
public class AbstractTest {

    // -- Helper methods --

    protected void assertString(final String expected, final Object actual) {
        assertNotNull(actual);
        assertSame(expected.getClass(), actual.getClass());
        assertEquals(expected, actual);
    }

    protected void assertNumber(final Number expected, final Object actual) {
        assertNotNull(actual);
        assertSame(expected.getClass(), actual.getClass());
        assertEquals(expected, actual);
    }

    protected void assertGroup(final Group expected, final int arity,
                               final Object token)
    {
        assertInstance(token, Group.class);
        final Group group = (Group) token;
        assertEquals(arity, group.getArity());
        assertTrue(expected.matches(group));
    }

    protected void assertFunction(final Object token) {
        assertInstance(token, Function.class);
    }

    protected void assertVariable(final String expected, final Object token) {
        assertInstance(token, Variable.class);
        assertEquals(expected, ((Variable) token).getToken());
    }

    protected void assertInstance(final Object token, final Class<?> c) {
        assertNotNull(token);
        assertTrue(c.isInstance(token), token.getClass().getName());
    }

    protected void assertCount(final int expected, final SyntaxTree tree) {
        assertNotNull(tree);
        assertEquals(expected, tree.count());
    }

    protected void assertUnary(final SyntaxTree tree) {
        assertCount(1, tree);
    }

    protected void assertBinary(final SyntaxTree tree) {
        assertCount(2, tree);
    }

    protected void assertToken(final String expected, final Object token) {
        assertNotNull(token);
        assertEquals(expected, token.toString());
    }

    protected void assertSameLists(final List<?> expected, final List<?> actual) {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertSame(expected.get(i), actual.get(i), "Non-matching index: " + i);
        }
    }

}