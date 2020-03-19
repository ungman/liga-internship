package tests;

import util.annotation.*;

import static util.asserts.Assert.ownAssert;

public class Test2 {
    @Before
    public void init(){
//        System.out.println("init");
    }
    @Test
    public void test() {
        ownAssert(true).isTrue();
        ownAssert(false).isTrue();
        ownAssert(10).isTrue();

        ownAssert(null).isNotNull();
        ownAssert(new Boolean(true)).isNotNull();
        ownAssert(new String("value")).isNotNull();
        ownAssert(new String("value")).isNotNull();
    }

    @Test(statisticsTestLevel = StatisticsTestLevel.All,printTestResult = PrintTestResult.AFTER_ASSERT)
    public void test1() {
        ownAssert(true).isTrue();
        ownAssert(false).isTrue();
        ownAssert(10).isTrue();

        ownAssert(null).isNotNull();
        ownAssert(new Boolean(true)).isNotNull();
        ownAssert(new String("value")).isNotNull();
        ownAssert(new String("value")).isNotNull();
    }
    @After
    public void close(){
//        System.out.println("close");
    }
}
