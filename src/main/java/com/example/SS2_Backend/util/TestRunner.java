package com.example.SS2_Backend.util;

/*
 * Prior issue: cannot import JUnit due to Maven not automatically adding Framework CLASSPATH
 * Solution: add classpath
 */
import com.example.SS2_Backend.model.StableMatching.StableMatchingProblem;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(StableMatchingProblem.class);

		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

		System.out.println(result.wasSuccessful());
	}
}
