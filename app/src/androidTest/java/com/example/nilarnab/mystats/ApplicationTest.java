package com.example.nilarnab.mystats;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.TestSuite;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends TestSuite {

    public ApplicationTest() {
        super();
    }

    public static TestSuite suite() {
        return new TestSuiteBuilder(ApplicationTest.class).includeAllPackagesUnderHere().build();
    }
}