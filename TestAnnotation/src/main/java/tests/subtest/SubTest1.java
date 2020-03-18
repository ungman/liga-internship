package tests.subtest;

import util.annotation.Test;

import static util.asserts.Assert.ownAssert;

public class SubTest1 {
    @Test
    public void test(){
        ownAssert(10).isEquals(11);
        ownAssert(11).isEquals(11);

        ownAssert().isNotNull();
        ownAssert(null).isNotNull();
        ownAssert("string").isNotNull();

        int a=10;
        int b=10;
        ownAssert(a==b).isTrue();
        ownAssert("AAA".equals("AAA")).isTrue();

    }
}
