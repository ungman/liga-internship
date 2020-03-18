package tests;

import util.annotation.After;
import util.annotation.Before;
import util.annotation.Test;


import static util.asserts.Assert.ownAssert;


public class Test1 {



    @Before
    public void init() {

    }

    @After
    public void close() {

    }

    @Test
    public void test() {
        ownAssert(true).isTrue();
        ownAssert(false).isTrue();
        ownAssert(10).isTrue();

        ownAssert(null).isNotNull();
        ownAssert(new Boolean(true)).isNotNull();
        ownAssert(new String("value")).isNotNull();


    }

    public void withoutAnn() {
    }


}
