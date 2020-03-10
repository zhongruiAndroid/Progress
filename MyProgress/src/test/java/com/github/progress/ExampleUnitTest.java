package com.github.progress;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void sdf() throws Exception {
        float a=50;
        int b=30;
        int c=130;
        System.out.println(b*a/c);
    }
    @Test
    public void d() throws Exception {
        System.out.println(0%360);
        System.out.println(1%360);
        System.out.println(360%360);
        System.out.println(367%360);
    }
    @Test
    public void dd() throws Exception {
        float a=0.9f;
        int b= (int) a;
        int c= (int) 1.2f;
        int d= (int) 1.5f;
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
    }
}