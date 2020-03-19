package tests.subtest;

import util.marker.IgnoreTestException;
import util.annotation.After;
import util.annotation.Before;
import util.annotation.Test;

import static util.asserts.Assert.ownAssert;

public class SubTest1 implements IgnoreTestException {
    @Before
    public void testOneBefore(){
        System.out.println("FirstBefore");
    }

    @Before
    public void testTwoBefore(){
        System.out.println("SecondBefore");
    }

    @Test
    public void firstTest(){
        System.out.println("firstTest");

        ownAssert(10).isEquals(11);
        ownAssert(11).isEquals(11);

        ownAssert().isNotNull();
        ownAssert(null).isNotNull();
        ownAssert("String").isNotNull();

        int a=10;
        int b=10;

        ownAssert(a==b).isTrue();
        ownAssert("AAA".equals("AAA")).isTrue();
        ownAssert("AAA".equals("AAB")).isTrue();
    }

    @Test
    public void secondTest(){
        System.out.println("SecondTest");
    }

    @After
    public  void testFirstAAfter(){
        System.out.println("FirstAfter");
    }

    @After
    public  void testSecondAAfter(){
        System.out.println("SecondAfter");

    }
}
