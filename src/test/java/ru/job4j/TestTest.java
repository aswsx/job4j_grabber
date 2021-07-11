package ru.job4j;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestTest {
    @Test
    public void whenIIsTwo() {
        var expected = 4;
        var rsl = TestClass.test(2);
        assertThat(rsl, is(expected));
    }
}